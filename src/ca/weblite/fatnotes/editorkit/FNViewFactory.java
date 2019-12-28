/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editorkit;

import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.swinghelpers.HTMLDocumentHelper;
import static ca.weblite.fatnotes.swinghelpers.HTMLDocumentHelper.isAttachment;
import ca.weblite.fatnotes.views.FNEditor;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author shannah
 */
public class FNViewFactory extends HTMLEditorKit.HTMLFactory {
    
    private FNDocument document;
    private FNEditor editor;
    
    public FNViewFactory(FNDocument doc, FNEditor editor) {
        this.document = doc;
        this.editor = editor;
    }

    
     static HTML.Tag getHTMLTagByElement(final Element elem) {
        final Object result = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
        return (result instanceof HTML.Tag) ? (HTML.Tag)result : null;
    }
    
    @Override
    public View create(Element elem) {
        
        //System.out.println("Creating view for "+elem.getName()+" "+elem.getStartOffset()+"-"+elem.getEndOffset());
        HTML.Tag tag = getHTMLTagByElement(elem);
        if (isAttachment(elem)) {
            //System.out.println("Creating attachment view");
            return new FNAttachmentTagView(editor, document, elem);
        } else if (HTML.Tag.IMG.equals(tag)) {
            return new FNImageView(editor, elem);
        
        } else if (tag instanceof HTML.UnknownTag && elem.getName().equals("attachment")) {
            return new FNAttachmentTagView(editor, document, elem);
        }
    
        
        return super.create(elem);
    }
    
   
    
}
