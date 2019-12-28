/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util.datatransfer;

import ca.weblite.fatnotes.controllers.FNEditorController;
import ca.weblite.fatnotes.models.FNDocumentFileSet;
import ca.weblite.fatnotes.models.FNDocumentFragment;
import ca.weblite.fatnotes.util.FileUtil;
import ca.weblite.fatnotes.util.ImageUtil;
import ca.weblite.fatnotes.util.RTFUtil;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.im.InputContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.NONE;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author shannah
 */
public class FNEditorTransferHandler extends TransferHandler {

    private FNEditorController controller;
    
    public FNEditorTransferHandler(FNEditorController controller) {
        this.controller = controller;
    }
    
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clipboard, int action) {
        if (comp instanceof JTextComponent) {
            FNDocumentFragmentTransferable documentSelection = FNDocumentFragmentTransferable.createTransferableFragmentWithSelection(
                controller.getView(), 
                    controller.getDocument()
            );
            JTextComponent text = (JTextComponent) comp;
            int p0 = text.getSelectionStart();
            int p1 = text.getSelectionEnd();
            clipboard.setContents(documentSelection, null);
            if (action == TransferHandler.MOVE) {
                 try {

                    Document doc = text.getDocument();
                    doc.remove(p0, p1 - p0);
                 } catch (BadLocationException ble){}

            }
        }
        
    }
    public void exportToClipboard2(JComponent comp, Clipboard clipboard,
            int action) throws IllegalStateException {
        if (comp instanceof JTextComponent) {
            JTextComponent text = (JTextComponent) comp;
            int p0 = text.getSelectionStart();
            int p1 = text.getSelectionEnd();
            if (p0 != p1) {
                try {
                    Document doc = text.getDocument();
                    String srcData = doc.getText(p0, p1 - p0);
                    StringSelection contents = new StringSelection(srcData);

                    // this may throw an IllegalStateException,
                    // but it will be caught and handled in the
                    // action that invoked this method
                    clipboard.setContents(contents, null);

                    if (action == TransferHandler.MOVE) {
                        doc.remove(p0, p1 - p0);
                    }
                } catch (BadLocationException ble) {
                }
            }
        }
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        System.out.println("Trying to import data from transferable "+t);
        StringBuilder sb = new StringBuilder();
        
        if (t == null)
            return false;
        if (t.isDataFlavorSupported(FNDocumentFragmentTransferable.documentFragmentFlavor)) {
            try {
                FNDocumentFragment fragment = (FNDocumentFragment) t.getTransferData(FNDocumentFragmentTransferable.documentFragmentFlavor);
                System.out.println("Importing fragment");
                controller.importFragment(fragment);
                return true;
            } catch (UnsupportedFlavorException|IOException ex){
                if (ex instanceof IOException) {
                    ex.printStackTrace();
                }
            } catch (Throwable th) {
                
            }
        }
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List<File> files = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
                System.out.println("Files: "+files);
                FNDocumentFileSet fs = controller.importFiles(files.toArray(new File[files.size()]));
                String html = fs.toHtml();
                /*
                //HtmlSelection htmlSelection = new HtmlSelection(html);
                StringSelection htmlSelection = new StringSelection(html);
                c.setContents(htmlSelection, null );
                System.out.println("Contents is now "+htmlSelection+" from "+html);
                System.out.println("T: "+c.getContents(this));
                for (DataFlavor fl : c.getContents(this).getTransferDataFlavors()) {
                    System.out.println("Flavour: "+fl);
                }
                controller.paste();
                return;
    */  
                controller.insertHtml(html);
                return true;

            } catch (Exception ex){
                //ex.printStackTrace();

            }
        
        }
        
        
        
        try {
            InputStream rtfInput = (InputStream)t.getTransferData(new DataFlavor("text/rtf;representationclass=java.io.InputStream"));
            String html = RTFUtil.toHTML(rtfInput);
            
            controller.insertHtml(html);
            return true;
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        
        if (t.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
            try {
                String htmlInput = ""+t.getTransferData(DataFlavor.allHtmlFlavor);
                //System.out.println("Html is "+htmlInput);
                htmlInput = controller.importImages(htmlInput, null, null);
                System.out.println("html -< "+htmlInput);
                controller.insertHtml(""+htmlInput);
                return true;
            } catch (Exception ex){
                //ex.printStackTrace();
            }
        }
        
        if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                Image img = (Image)t.getTransferData(DataFlavor.imageFlavor);
                BufferedImage bimg = ImageUtil.toBufferedImage(img);
                File outputFile = new File(controller.getDocument().getImagesDirectory(), "pasted-image-"+System.currentTimeMillis()+".png");
                outputFile.getParentFile().mkdirs();
                outputFile = FileUtil.createUniqueName(outputFile);
                
                ImageIO.write(bimg, "png", outputFile);
                String imgUrl = "images/" + URLEncoder.encode(outputFile.getName(), "UTF-8");
                String htmlSnippet = "<img src='"+imgUrl+"'>";
                controller.insertHtml(htmlSnippet);
                
                
            } catch (UnsupportedFlavorException|IOException ex) {
                Logger.getLogger(FNEditorTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        
        return importData2(comp, t);
    }
    
    public boolean importData2(JComponent comp, Transferable t) {
        if (comp instanceof JTextComponent) {
            DataFlavor flavor = getFlavor(t.getTransferDataFlavors());

            if (flavor != null) {
                InputContext ic = comp.getInputContext();
                if (ic != null) {
                    ic.endComposition();
                }
                try {
                    String data = (String) t.getTransferData(flavor);

                    ((JTextComponent) comp).replaceSelection(data);
                    return true;
                } catch (UnsupportedFlavorException ufe) {
                } catch (IOException ioe) {
                }
            }
        }
        return false;
    }

    public boolean canImport(JComponent comp,
            DataFlavor[] transferFlavors) {
        JTextComponent c = (JTextComponent) comp;
        if (!(c.isEditable() && c.isEnabled())) {
            return false;
        }
        if (getFlavor(transferFlavors) != null) {
            return true;
        } else {
            for (DataFlavor f : transferFlavors) {
                if (f.equals(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
                if (f.equals(FNDocumentFragmentTransferable.documentFragmentFlavor)){
                    return true;
                }
                if (f.equals(DataFlavor.allHtmlFlavor)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public int getSourceActions(JComponent c) {
        return  MOVE;
    }
    
    

    private DataFlavor getFlavor(DataFlavor[] flavors) {
        if (flavors != null) {
            for (DataFlavor flavor : flavors) {
                if (flavor.equals(DataFlavor.stringFlavor)) {
                    return flavor;
                }
            }
        }
        return null;
    }

    @Override
    protected Transferable createTransferable(JComponent comp) {
        if (comp instanceof JTextComponent) {
            return FNDocumentFragmentTransferable.createTransferableFragmentWithSelection(
                controller.getView(), 
                    controller.getDocument()
            );
            
        } 
        return super.createTransferable(comp);
        
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (source instanceof JEditorPane && data instanceof FNDocumentFragmentTransferable) {
            FNDocumentFragmentTransferable fragmentData = (FNDocumentFragmentTransferable)data;
            JEditorPane jtc = (JEditorPane)source;
            try {
                int start = fragmentData.getPayload().getStartPos();
                int end = fragmentData.getPayload().getEndPos();
                jtc.getDocument().remove(start, end-start);
            } catch (BadLocationException ble){}
        }
    }
    
    
    
    

}
