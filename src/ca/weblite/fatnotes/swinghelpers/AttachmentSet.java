/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.swinghelpers;

import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNDocumentFile;
import ca.weblite.fatnotes.models.FNDocumentFileSet;
import ca.weblite.fatnotes.models.FNDocumentSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Element;



/**
 *
 * @author shannah
 */
public class AttachmentSet implements Iterable<Element> {
    private List<Element> attachments = new ArrayList<>();
    private JEditorPane editor;
    
    public AttachmentSet(JEditorPane editor) {
        this.editor = editor;
    }

    public void add(Element el) {
        if (!HTMLDocumentHelper.isAttachment(el)) {
            throw new IllegalArgumentException("Attempt to add non-attachment element to attachment set");
        }
        attachments.add(el);
    }
    
    @Override
    public Iterator<Element> iterator() {
        return attachments.iterator();
    }
    
    public FNDocumentFileSet toDocumentFileSet(FNDocument doc) {
        FNDocumentFileSet out = new FNDocumentFileSet(doc);
        HTMLDocumentHelper h = new HTMLDocumentHelper(editor);
        for (Element e : this) {
            FNDocumentFile file = h.parseAttachment(doc, e);
            if (file != null) {
                out.add(file);
            }
        }
        return out;
        
    }
    
    public AttachmentSet minus(AttachmentSet toRemove) {
        HashMap<String,Element> out = new HashMap<String,Element>();
        for (Element e : this) {
            String attachmentStr = HTMLDocumentHelper.attr(e, "data-attachment");
            if (attachmentStr != null) {
                out.put(attachmentStr, e);
            }
        }
        
        for (Element e : toRemove) {
            String attachmentStr = HTMLDocumentHelper.attr(e, "data-attachment");
            if (attachmentStr != null) {
                out.remove(attachmentStr);
            }
        }
        
        AttachmentSet res = new AttachmentSet(editor);
        for (Element el : out.values()) {
            res.add(el);
        }
        return res;
        
        
    }
    
    public boolean isEmpty() {
        return attachments.isEmpty();
    }
    
}
