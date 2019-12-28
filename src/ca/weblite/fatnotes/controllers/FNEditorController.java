/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.controllers;

import ca.weblite.fatnotes.editoractions.CopyAction;
import ca.weblite.fatnotes.views.FNEditor;
import ca.weblite.fatnotes.editoractions.PasteAction;
import ca.weblite.fatnotes.editorkit.FNHTMLDocument;
import ca.weblite.fatnotes.editorkit.FNHTMLEditorKit;
import ca.weblite.fatnotes.models.FNContext;
import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNDocument.FNDocumentEditor;
import ca.weblite.fatnotes.models.FNDocument.ImportFileResult;
import ca.weblite.fatnotes.models.FNDocument.ImportFilesResult;
import ca.weblite.fatnotes.models.FNDocumentFile;
import ca.weblite.fatnotes.models.FNDocumentFileSet;
import ca.weblite.fatnotes.models.FNDocumentFragment;
import ca.weblite.fatnotes.swinghelpers.AttachmentSet;
import ca.weblite.fatnotes.swinghelpers.HTMLDocumentHelper;
import ca.weblite.fatnotes.swinghelpers.ImageSet;
import ca.weblite.fatnotes.util.FileUtil;
import ca.weblite.fatnotes.util.NetworkUtil;
import ca.weblite.fatnotes.util.datatransfer.FNEditorTransferHandler;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.RunElement;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author shannah
 */
public class FNEditorController {
    private final FNEditor view;
    private final FNDocument document;
    private boolean closed;
    private boolean modelChanged;
    private final FNContext context;
    
    private FNDocumentEditor fnDocumentEditor = new FNDocumentEditor() {

        @Override
        public void flushBuffer() {
            document.setBuffer(view.getText());
        }
        
    };
    
    
    public final FNEditor createEditor() {
        FNEditor editor = new FNEditor();
        editor.setEditorKit(new FNHTMLEditorKit(document, editor));
        editor.setDocument(editor.getEditorKit().createDefaultDocument());
        editor.setContentType("text/html");
        
        editor.setTransferHandler(new FNEditorTransferHandler(this));
        try {
            ((HTMLDocument)editor.getDocument()).setBase(document.getFile().toURI().toURL());
        } catch (IOException ex){}
        return editor;
        
    }
    
    public FNEditorController(FNContext context, FNDocument document) {
        this.context = context;
        this.document = document;
        view = createEditor();
        setupActions();
        loadContentsFromDisk();
        view.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                Element src = e.getSourceElement();
                HTMLDocumentHelper h = new HTMLDocumentHelper(view);
                FNDocumentFile docFile = h.parseAttachment(document, src);
                if (docFile != null) {
                    try {
                        docFile.openInSystemEditor();
                        
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                
            }
        });
        
        
        // Double-click handler to open files
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                HTMLDocumentHelper h = new HTMLDocumentHelper(view);
                if (e.getClickCount() == 2) {
                    System.out.println(view.getText());
                    Point point = e.getPoint();
                    int pos = view.viewToModel(point);
                    if (pos >= 0) {
                        Element attachmentEl = h.findAttachmentAt(pos);
                        if (attachmentEl != null) {
                            FNDocumentFile file = h.parseAttachment(document, attachmentEl);
                            if (file != null) {
                                try {
                                    file.openInSystemEditor();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        
                    }
                }
                super.mouseClicked(e); 
            }
            
        });
        
        // Monitory to sync changes in view to model whcih will initiate autosave
        final Object changeMonitorLock = new Object();
        Thread changeMonitor = new Thread(()->{
            while (!closed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    
                }
                if (modelChanged) {
                    modelChanged = false;
                    document.setBuffer(view.getText());
                }
                
                synchronized(changeMonitorLock) {
                    try {
                        changeMonitorLock.wait();
                    } catch (InterruptedException ex) {
                        
                    }
                }
            }
        });
        changeMonitor.start();
        view.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                document.addDocumentEditor(fnDocumentEditor);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                document.removeDocumentEditor(fnDocumentEditor);
                super.componentHidden(e);
            }
            
            
            
            
        });
        
        view.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                modelChanged = true;
                synchronized(changeMonitorLock) {
                    changeMonitorLock.notify();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                modelChanged = true;
                synchronized(changeMonitorLock) {
                    changeMonitorLock.notify();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                modelChanged = true;
                synchronized(changeMonitorLock) {
                    changeMonitorLock.notify();
                }
            }
            
        });
        

    }
    

    private void loadContentsFromDisk() {
        SwingWorker worker = new SwingWorker() {
            private String contents;
            @Override
            protected Object doInBackground() throws Exception {
                contents = document.loadContentsAsHTML();
                return contents;
            }

            @Override
            protected void done() {
                super.done();
                
                view.setText(contents);
                view.resetUndoQueue();
            }
            
            
            
        };
        worker.execute();
    }
    
    public void paste() {
        view.paste();
    }
    public String importImages(String html, File srcFile, String baseUrl) throws IOException {
        FNEditor tmpEditor = createEditor();
        tmpEditor.setText(html);
        importImages(tmpEditor, srcFile, baseUrl);
        return tmpEditor.getText();
    }
    
    private static void printAttNames(Element el) {
        Enumeration en = el.getAttributes().getAttributeNames();
        while (en.hasMoreElements()) {
            System.out.println(en.nextElement());
        }
    }
    
    public void insertHtml(String html) {
        try {
            view.insertHtml(html);
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    
    public void insertHtml_old(String _html) {
        System.out.println("Inserting html "+_html);
        HTMLDocumentHelper h = new HTMLDocumentHelper(view);
        h.writeLock(()->{
            String html = _html;
            HTMLDocument doc = (HTMLDocument)view.getDocument();
            Element p = doc.getParagraphElement(view.getSelectionStart());
            printAttNames(p);
            System.out.println(p.getName()+"/"+p.getAttributes().getAttribute("name"));
            System.out.println("p.name="+p.getName()+", parentName="+p.getParentElement().getName());
            boolean wrapped;
            
            if ("p-implied".equals(p.getName().toLowerCase())) {
                //html = "<div>-" + html+"-</div>";
                try {
                    doc.insertAfterEnd(p, "<p>&nbsp;</p>");
                } catch (BadLocationException|IOException ble) {}
                p = doc.getParagraphElement(view.getSelectionStart());

            } else {

            }
        
             System.out.println(p.getName()+"/"+p.getAttributes().getAttribute("name"));
            System.out.println("p.name="+p.getName()+", parentName="+p.getParentElement().getName());
            if (!p.getName().equals("p")){
                System.out.println("Wrapping in <p>");
                html = "<p>"+html+"</p>";
                wrapped = true;
                
            } else {
                System.out.println("P: "+p.getAttributes().getAttribute(HTML.Tag.P));
                wrapped = false;
            }
            String fHtml = html;
            String placeholder = UUID.randomUUID().toString();
            System.out.println("Placeholder: "+placeholder);
            try {
                view.getDocument().insertString(view.getSelectionStart(), placeholder, null);
                int startPos = view.getSelectionStart();
                String htmlContents = view.getText();
                System.out.println("Old contents: "+htmlContents);
                htmlContents = htmlContents.replace(placeholder, _html);
                System.out.println("New contents will be "+htmlContents);
                view.setText(htmlContents);
                view.setSelectionStart(startPos);
            } catch (BadLocationException ex) {
               
            }
            //try {
                //System.out.println("Inserting html: "+fHtml);
                //HTMLEditorKit kit = (HTMLEditorKit)view.getEditorKit();
                //if (html.contains("<attachment")) {
                //    doc.insertBeforeEnd(p, html);
                //}
                //kit.insertHTML(doc, view.getSelectionStart(), fHtml, 0, 0, null);
                System.out.println("After insert: "+view.getText());
                //doc.dump(System.out);
            //} catch (BadLocationException ex) {
            //    ex.printStackTrace();
            //} catch (IOException ex) {
            //    //Logger.getLogger(FNEditorController.class.getName()).log(Level.SEVERE, null, ex);
            //    ex.printStackTrace();
            //}
        });
        
        
    }
    
    private void setupActions() {
        Action origPasteAction = view.getActionMap().get("paste-from-clipboard");
        view.getActionMap().put("paste-from-clipboard", new PasteAction(this, origPasteAction));
        //System.out.println(Arrays.toString(view.getActionMap().allKeys()));
        
        //Action origCopyAction = view.getActionMap().get("copy-to-clipboard");
        //view.getActionMap().put("copy-to-clipboard", new CopyAction(this, origCopyAction));
        
    }
            
    
    public FNDocumentFileSet importFiles(File... files) throws IOException {
        return document.importFiles(files);
    }
    
    public FNEditor getView() {
        return view;
    }
    
    public FNDocument getDocument() {
        return document;
    }
    
    
    
    public void importImages(FNEditor tmpEditor, File srcFile, String baseHref) throws IOException {
        HTMLDocumentHelper h = new HTMLDocumentHelper(tmpEditor);
        document.getImagesDirectory().mkdirs();
        ImageSet imagesToImport = h.findImages();
        for (Element img : imagesToImport) {
            Enumeration names = img.getAttributes().getAttributeNames();
            
            while (names.hasMoreElements()) {
                System.out.println("Name="+names.nextElement());
            }
            RunElement atts = (RunElement)img.getAttributes();

            if (atts == null) {
                throw new IllegalStateException("No img tag returned from findImages()");
            }
            String src = (String)atts.getAttribute(HTML.Attribute.SRC);
            if (src == null) {
                System.out.println("No src");
                continue;
            }
            System.out.println("src is "+src);
            if (src.startsWith("http://") || src.startsWith("https://")) {
                URL imageUrl = new URL(src);
                File destImage = FileUtil.createUniqueName(new File(document.getImagesDirectory(), new File(imageUrl.getPath()).getName()));
                NetworkUtil.downloadToFile(imageUrl, destImage);
                h.writeLock(()->{
                    atts.addAttribute("data-orig-src", src);
                    atts.removeAttribute(HTML.Attribute.SRC);
                    atts.addAttribute(HTML.Attribute.SRC, "images/"+destImage.getName());
                    
                    //RunElement runEl = (RunElement)img.getAttributes().getAttribute(HTML.Tag.IMG);
                });          
            } else if (src.startsWith("images/") && srcFile != null) {
                System.out.println("Importing image "+src);
                File srcImageFile = new File(srcFile, src);
                File destImageFile = new File(document.getImagesDirectory(), src.substring(src.indexOf("/")+1));
                if (srcImageFile.exists() && !destImageFile.equals(srcImageFile)) {
                    System.out.println("src exists and is different than dest");
                    if (!destImageFile.exists() || !FileUtil.getMD5Checksum(destImageFile).equals(FileUtil.getMD5Checksum(srcImageFile))) {
                        // There is already an image at this destination.
                        destImageFile = FileUtil.createUniqueName(destImageFile);
                        System.out.println("Copying "+srcImageFile+" to "+destImageFile);
                        FileUtil.copy(srcImageFile, destImageFile);
                        final File fdestImageFile = destImageFile;
                        h.writeLock(()->{
                            atts.addAttribute("data-orig-src", src);
                            atts.removeAttribute(HTML.Attribute.SRC);
                            atts.addAttribute(HTML.Attribute.SRC, "images/"+fdestImageFile.getName());
                        });
                    }
                }
            }
        }
    }
    
    /**
     * Imports a document fragment that has been copied from this or another editor.
     * @param fragment
     * @throws IOException 
     */
    public void importFragment(FNDocumentFragment fragment) throws IOException {
        FNDocument sourceDoc = fragment.getDocument(context);
        FNEditor tmpEditor = createEditor();
        tmpEditor.setText(fragment.getHtmlFragment());
        if (!sourceDoc.getFile().equals(document.getFile())) {
            importImages(tmpEditor, sourceDoc.getFile(), null);
        }
        
        HTMLDocumentHelper tmpHelper = new HTMLDocumentHelper(tmpEditor);
        AttachmentSet attachmentsToImport = tmpHelper.findAttachments();
        if (!attachmentsToImport.isEmpty()) {
            FNDocumentFileSet filesToImport = attachmentsToImport.toDocumentFileSet(sourceDoc);
            ImportFilesResult res = document.importFiles(filesToImport);
            HashMap<String,String> renames = new HashMap<>();
            for (ImportFileResult fileRes : res) {
                if (fileRes.getError() != null) {
                    throw new IOException("Failed to import fragment into document.", fileRes.getError());
                }
                String newName = fileRes.getDest().getFile().getName();
                String oldName = fileRes.getSource().getFile().getName();
                if (!oldName.equals(newName)) {
                    renames.put(oldName, newName);
                }
            }
            
            for (Element el : attachmentsToImport) {
                String attachmentStr = HTMLDocumentHelper.attr(el, "data-attachment");
                if (renames.containsKey(attachmentStr)) {
                    HTMLDocumentHelper.setAttribute(el, "data-attachment", renames.get(attachmentStr));
                }
            }
        }
        tmpEditor.selectAll();
        String str = tmpHelper.getSelectedHtmlString();
        String openTag = "<body>";
        String closeTag = "</body>";
        int bodyPos = str.indexOf(openTag);
        if (bodyPos >= 0) {
            str = str.substring(bodyPos + openTag.length());
        }
        bodyPos = str.indexOf(closeTag);
        if (bodyPos >= 0) {
            str = str.substring(0, bodyPos);
        }
        
        int startPos = view.getSelectionStart();
        int endPos = view.getSelectionEnd();
        
        HTMLDocumentHelper h = new HTMLDocumentHelper(view);
        FNHTMLDocument htmlDoc = (FNHTMLDocument)view.getDocument();
        h.writeLock(()->{
            if (endPos > startPos) {
                try {
                    htmlDoc.remove(startPos, endPos-startPos);
                } catch (BadLocationException ex) {

                }
            }
        });
        
        
        insertHtml(str);
        
        
        
        
        
    }
    
    public void close() {
        document.setBuffer(view.getText());
        closed = true;
    }
    
    
     
}
