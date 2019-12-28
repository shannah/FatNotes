/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.swinghelpers;

import ca.weblite.fatnotes.editorkit.FNHTMLDocument;
import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNDocumentFile;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

/**
 *
 * @author shannah
 */
public class HTMLDocumentHelper {
    
    JEditorPane view;
    public HTMLDocumentHelper(JEditorPane pane) {
        view = pane;
    }
    
    public static Element findAttachmentAt(JEditorPane view, int pos) {
        return new HTMLDocumentHelper(view).findAttachmentAt(pos);
    }
    
    public Element findAttachmentAt(int pos) {
        HTMLDocument doc = (HTMLDocument)view.getDocument();
        for (Element e : findAttachments(doc.getDefaultRootElement(), null)) {
            if (e.getStartOffset() <= pos && e.getEndOffset() >=pos) {
                return e;
            }
        }
        return null;
    }
    
    public static AttachmentSet findAttachments(JEditorPane view, Element root, AttachmentSet out) {
        return new HTMLDocumentHelper(view).findAttachments(root, out);
    }
    
    public static String attr(Element el, String key) {
        AttributeSet a = el.getAttributes();

        SimpleAttributeSet value = (SimpleAttributeSet) a.getAttribute(HTML.Tag.A);
        if (value != null) {
            return (String) value.getAttribute(key);
            
        }
        return null;
    }
    
    public static void setAttribute(Element el, String key, String val) {
        AttributeSet a = el.getAttributes();

        SimpleAttributeSet value = (SimpleAttributeSet) a.getAttribute(HTML.Tag.A);
        if (value != null) {
            value.removeAttribute(key);
            value.addAttribute(key, val);
            
        }
    }
    
    public AttachmentSet findAttachments() {
        return findAttachments(((HTMLDocument)view.getDocument()).getDefaultRootElement(), null);
    }
    
    public Element getRootElement() {
        return ((HTMLDocument)view.getDocument()).getDefaultRootElement();
    }
    
    public ImageSet findImages() {
        return findImages(getRootElement(), null);
    }
    
    public static HTML.Tag getHTMLTagByElement(final Element elem) {
        final Object result = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
        return (result instanceof HTML.Tag) ? (HTML.Tag)result : null;
    }
    
    public ImageSet findImages(Element root, ImageSet out) {
        if (out == null) {
            out = new ImageSet(view);
        }
        
        
        if (isImage(root)) {
            
            out.add(root);
            
        } else {
            System.out.println("No image found in "+root);
        }
        
        int len = root.getElementCount();
        for (int i=0; i<len; i++) {
            Element child = root.getElement(i);
            findImages(child, out);
        }
        return out;
    }
    
    public AttachmentSet findAttachments(Element root, AttachmentSet out) {
        if (out == null) {
            out = new AttachmentSet(view);
        }
        
        AttributeSet a = root.getAttributes();

        SimpleAttributeSet value = (SimpleAttributeSet) a.getAttribute(HTML.Tag.A);
        if (value != null) {
            String attachment = (String) value.getAttribute("data-attachment");
            if (attachment != null) {
                out.add(root);
            }
        }
        
        int len = root.getElementCount();
        for (int i=0; i<len; i++) {
            Element child = root.getElement(i);
            findAttachments(child, out);
        }
        return out;
    }
    
    public FNDocumentFile parseAttachment(FNDocument doc, Element el) {
        AttributeSet a = el.getAttributes();

        SimpleAttributeSet value = (SimpleAttributeSet) a.getAttribute(HTML.Tag.A);
        if (value != null) {
            String attachment = (String) value.getAttribute("data-attachment");
            if (attachment != null) {
                File f = new File(doc.getFilesDirectory(), attachment);
                if (f.exists()) {
                    return new FNDocumentFile(doc, f);
                }
            }
        }
        value = (SimpleAttributeSet) a.getAttribute(HTML.Tag.IMG);
        if (value != null) {
            String attachment = (String) value.getAttribute("data-attachment");
            if (attachment != null) {
                File f = new File(doc.getFilesDirectory(), attachment);
                if (f.exists()) {
                    return new FNDocumentFile(doc, f);
                }
            }
        }
        if ("img".equals(el.getName()) && a.getAttribute("data-file") != null) {
            System.out.println("Found data-file attr");
            String attachment = (String) a.getAttribute("data-file");
            if (attachment != null) {
                File f = new File(doc.getFilesDirectory(), attachment);
                if (f.exists()) {
                    return new FNDocumentFile(doc, f);
                }
            }
        }
        if ("img".equals(el.getName()) && a.getAttribute("data-attachment") != null) {
            System.out.println("Found data-attachment attr");
            String attachment = (String) a.getAttribute("data-attachment");
            if (attachment != null) {
                File f = new File(doc.getFilesDirectory(), attachment);
                if (f.exists()) {
                    return new FNDocumentFile(doc, f);
                }
            }
        }
        
        
        return null;
    }
    
    public static boolean isImage(Element el) {
        return getHTMLTagByElement(el) == HTML.Tag.IMG && el.getAttributes().getAttribute("data-attachment") == null;
        

    }
    
    public static boolean isAttachment(Element el) {
    
        AttributeSet a = el.getAttributes();

        SimpleAttributeSet value = (SimpleAttributeSet) a.getAttribute(HTML.Tag.A);
        if (value != null) {
            
            String attachment = (String) value.getAttribute("data-attachment");
            if (attachment != null) {
                System.out.println("Case 1");
                return true;
                
            }
        }
        
        if ("img".equals(el.getName()) && a.getAttribute("data-attachment") != null) {
            System.out.println("Found data-attachment attr");
            String attachment = (String) a.getAttribute("data-attachment");
            if (attachment != null) {
                System.out.println("case 3");
               return true;
            }
        }
        
        return false;

    }
    
    public String getHtmlString(int startPos, int endPos) {
        HTMLDocument htmlDoc = (HTMLDocument)view.getDocument();
        StringWriter buf = new StringWriter();
        HTMLWriter htmlWriter = new HTMLWriter(buf, htmlDoc, startPos, endPos);
        try {
            htmlWriter.write();
        } catch (IOException|BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        
        String htmlFragment = buf.toString();
        return htmlFragment;
    }
    
    public String getSelectedHtmlString() {
        return getHtmlString(view.getSelectionStart(), view.getSelectionEnd());
    }
    
    
    public void writeLock(Runnable r) {
       ;
        try ( AutoCloseable cl =  new AutoCloseable() {
            {
                ((FNHTMLDocument)view.getDocument()).fnWriteLock();
            }
            @Override
            public void close() throws Exception {
                ((FNHTMLDocument)view.getDocument()).fnWriteUnlock();
            }
            
        }) {
            r.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
