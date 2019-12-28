/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.swinghelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Element;



/**
 *
 * @author shannah
 */
public class ImageSet implements Iterable<Element> {
    private List<Element> images = new ArrayList<>();
    private JEditorPane editor;
    
    public ImageSet(JEditorPane editor) {
        this.editor = editor;
    }

    public void add(Element el) {
        if (!HTMLDocumentHelper.isImage(el)) {
            throw new IllegalArgumentException("Attempt to add non-image element to image set");
        }
        images.add(el);
    }
    
    @Override
    public Iterator<Element> iterator() {
        return images.iterator();
    }
    
   
    
    public ImageSet minus(ImageSet toRemove) {
        HashMap<String,Element> out = new HashMap<String,Element>();
        for (Element e : this) {
            String attachmentStr = HTMLDocumentHelper.attr(e, "src");
            if (attachmentStr != null) {
                out.put(attachmentStr, e);
            }
        }
        
        for (Element e : toRemove) {
            String attachmentStr = HTMLDocumentHelper.attr(e, "src");
            if (attachmentStr != null) {
                out.remove(attachmentStr);
            }
        }
        
        ImageSet res = new ImageSet(editor);
        for (Element el : out.values()) {
            res.add(el);
        }
        return res;
        
        
    }
    
    public boolean isEmpty() {
        return images.isEmpty();
    }
    
}
