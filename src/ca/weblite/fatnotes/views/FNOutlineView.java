/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.views;

import ca.weblite.fatnotes.editorkit.FNHTMLDocument;
import ca.weblite.fatnotes.editorkit.FNHTMLEditorKit;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import static javax.swing.text.html.HTML.Tag.H1;
import static javax.swing.text.html.HTML.Tag.H2;
import static javax.swing.text.html.HTML.Tag.H3;
import static javax.swing.text.html.HTML.Tag.H4;
import static javax.swing.text.html.HTML.Tag.H5;
import static javax.swing.text.html.HTML.Tag.H6;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author shannah
 */
public class FNOutlineView extends JXPanel {
    private FNEditor editor;
    
    

    
    private class OutlineNode {
        private final Element element;
        
        private OutlineNode previous, next, parent, child;
        
        public OutlineNode(Element el) {
            this.element = el;
        }
        
        public OutlineNode lastChild() {
            OutlineNode lastChild = child;
            while (lastChild.next != null) {
                lastChild = lastChild.next;
            }
            return lastChild;
        }
        
        public OutlineNode appendChild(Element el) {
            OutlineNode node = new OutlineNode(el);
            node.parent = this;
            if (child == null) {
                child = node;
            } else {
                OutlineNode lastChild = lastChild();
                lastChild.next = node;
                node.previous = lastChild;
            }
            return node;
        }
        
        public void build() {
            Element nextHeading = findNextHeading(element, getElementIndex(element), true);
            if (nextHeading != null) {
                HTML.Tag currTag = getTag(element);
                HTML.Tag headingTag = getTag(nextHeading);
                int currLevel = getHeadingLevel(currTag);
                int nextLevel = getHeadingLevel(headingTag);
                
                if (nextLevel > currLevel) {
                    child = new OutlineNode(nextHeading);
                    child.parent = this;
                    child.build();
                    
                } else if (nextLevel == currLevel) {
                    next = new OutlineNode(nextHeading);
                    next.parent = parent;
                    next.previous = this;
                    next.build();
                } else { //if (nextLevel < currLevel) {
                    OutlineNode nextParent = parent;
                    while (getHeadingLevel(getTag(nextParent.element)) >= nextLevel) {
                        nextParent = nextParent.parent;
                    }
                    
                    // nextParent level should now be less than the nextLevel
                    nextParent.appendChild(nextHeading).build();
                    
                    
                }
            }
                
            
        }
    }
    
    
    
    private int getHeadingLevel(HTML.Tag tag) {
        if (tag.equals(H1)) {
            return 1;
        }
        if (tag.equals(H2)) {
            return 2;
        }
        if (tag.equals(H3)) {
            return 3;
            
        }
        if (tag.equals(H4)) {
            return 4;
        }
        if (tag.equals(H5)) {
            return 5;
        }
        if (tag.equals(H6)) {
            return 6;
        }
        return 0;
        
    }
    
    private void update() {
        Element root = doc().getDefaultRootElement();
        OutlineNode rootNode = new OutlineNode(root);
        removeAll();
        
        
        
        
    }
    
    private static boolean isHeading(HTML.Tag tag) {
        return tag.equals(H1) || tag.equals(H2) || tag.equals(H3) || tag.equals(H4) || tag.equals(H5) || tag.equals(H6);
    }
    
    private HTML.Tag getTag(Element el) {
        return (HTML.Tag)el.getAttributes().getAttribute(StyleConstants.NameAttribute);
    }
    
    private int getElementIndex(Element el) {
        Element parent = el.getParentElement();
        if (parent == null) {
            return -1;
        }
        int len = parent.getElementCount();
        for (int i=0; i<len; i++) {
            if (el.equals(parent.getElement(i))) {
                return i;
            }
        }
        return -1;
    }
    
    private Element findNextHeading(Element start, int elementIndex, boolean skipStart) {
        HTML.Tag tag = (HTML.Tag)start.getAttributes().getAttribute(StyleConstants.NameAttribute);
        if (!skipStart && isHeading(tag)) {
            return start;
        }
        Element found = null;
        int len = start.getElementCount();
        if (len > 0) {
            found = findNextHeading(start.getElement(0), 0, false);
            if (found != null) {
                return found;
            }
        }
        Element parent = start.getParentElement();
        int parentLen = parent.getElementCount();
        if (parentLen > elementIndex+1) {
            found = findNextHeading(parent.getElement(elementIndex+1), elementIndex+1, false);
            if (found != null) {
                return found;
            }
        }
        
        return null;
       
    }
    
    private FNHTMLEditorKit kit() {
        return (FNHTMLEditorKit)editor.getEditorKit();
    }
    
    private FNHTMLDocument doc() {
        return (FNHTMLDocument)editor.getDocument();
    }
    
}
