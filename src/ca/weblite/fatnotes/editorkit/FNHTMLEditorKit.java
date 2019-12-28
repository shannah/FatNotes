/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editorkit;

import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNParagraphType;
import ca.weblite.fatnotes.views.FNEditor;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author shannah
 */
public class FNHTMLEditorKit extends HTMLEditorKit {

    private FNDocument document;
    private FNEditor editor;

    public FNHTMLEditorKit(FNDocument doc, FNEditor editor) {
        this.document = doc;
        this.editor = editor;
    }

    @Override
    public ViewFactory getViewFactory() {
        return new FNViewFactory(document, editor);
    }

    @Override
    public Document createDefaultDocument() {
        StyleSheet styles = getStyleSheet();
        StyleSheet ss = new StyleSheet();

        ss.addStyleSheet(styles);

        FNHTMLDocument doc = new FNHTMLDocument(ss);
        doc.setParser(getParser());
        doc.setAsynchronousLoadPriority(4);
        doc.setTokenThreshold(100);
        //try {
        //    doc.setOuterHTML(doc.getDefaultRootElement(), "<html><head></head><body><p></p></body></html>");
        //} catch (BadLocationException|IOException ble){}
        return doc;
    }

    //private Parser parser;
    //@Override
    //protected Parser getParser() {
    //    if (parser == null) {
    //        parser = new FNParserDelegator(editor);
    //    }
    //    return parser;
    //}
    /**
     * The method gets inner HTML of given element. If the element is named
     * <code>p-implied</code> or <code>content</code>, it returns null.
     *
     * @param e element
     * @param d document containing given element
     * @return the inner HTML of a HTML tag or null, if e is not a valid HTML
     * tag
     * @throws IOException
     * @throws BadLocationException
     */
    public String getInnerHtmlOfTag(Element e, Document d) throws IOException, BadLocationException {
        if (e.getName().equals("p-implied") || e.getName().equals("content")) {
            return null;
        }

        CharArrayWriter caw = new CharArrayWriter();
        int i;
        final String startTag = "<" + e.getName();
        final String endTag = "</" + e.getName() + ">";
        final int startTagLength = startTag.length();
        final int endTagLength = endTag.length();

        write(caw, d, e.getStartOffset(), e.getEndOffset() - e.getStartOffset());
        //we have the element but wrapped as full standalone HTML code beginning with HTML start tag
        //thus we need unpack our element
        StringBuilder str = new StringBuilder(caw.toString());
        while (str.length() >= startTagLength) {
            if (str.charAt(0) != '<') {
                str.deleteCharAt(0);
            } else if (!str.substring(0, startTagLength).equals(startTag)) {
                str.delete(0, startTagLength);
            } else {
                break;
            }
        }
        //we've found the beginning of the tag
        for (i = 0; i < str.length(); i++) { //skip it...
            if (str.charAt(i) == '>') {
                break; //we've found end position of our start tag
            }
        }
        str.delete(0, i + 1); //...and eat it
        //skip the content
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '<' && i + endTagLength < str.length() && str.substring(i, i + endTagLength).equals(endTag)) {
                break; //we've found the end position of inner HTML of our tag
            }
        }
        str.delete(i, str.length()); //now just remove all from i position to the end

        return str.toString().trim();
    }
    
    public Element getBlockElement(int pos, FNHTMLDocument d) {
        Element el = d.getCharacterElement(pos);
        HTML.Tag tag = (HTML.Tag)el.getAttributes().getAttribute(StyleConstants.NameAttribute);
        while (tag == null || !tag.isBlock()) {
            System.out.println("EL:"+el.getName());
            el = el.getParentElement();
            tag = (HTML.Tag)el.getAttributes().getAttribute(StyleConstants.NameAttribute);
        }
        return el;
    }
    
    public Element setBlockTag(int pos, FNHTMLDocument d, HTML.Tag tag) throws IOException, BadLocationException {
        int selectionStart = editor.getSelectionStart();
        int selectionEnd = editor.getSelectionEnd();
        int caretPos = editor.getCaretPosition();
        Element blockEl = getBlockElement(pos, d);
        HTML.Tag oldTag = (HTML.Tag)blockEl.getAttributes().getAttribute(StyleConstants.NameAttribute);
        
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(tag).append(">").append(getInnerHtmlOfTag(blockEl, d)).append("</").append(tag).append(">");
        
        d.setOuterHTML(blockEl, sb.toString());
        editor.setSelectionEnd(selectionEnd);
        editor.setSelectionStart(selectionStart);
        editor.setCaretPosition(caretPos);
        return blockEl;
    }
    
    public HTML.Tag getTagForParagraphType(FNParagraphType ptype){
        switch (ptype) {
            case HEADING1: return HTML.Tag.H1;
            case HEADING2: return HTML.Tag.H2;
            case HEADING3: return HTML.Tag.H3;
            case HEADING4: return HTML.Tag.H4;
            case HEADING5: return HTML.Tag.H5;
            case HEADING6: return HTML.Tag.H6;
            case NORMAL: return HTML.Tag.P;
        }
        return HTML.Tag.P;
    }
    
    public FNParagraphType getParagraphTypeForTag(HTML.Tag tag) {
        
        if (HTML.Tag.H1.equals(tag)) {
            return FNParagraphType.HEADING1;
        } else if (HTML.Tag.H2.equals(tag)) {
            return FNParagraphType.HEADING2;
            
        } else if (HTML.Tag.H3.equals(tag)) {
            return FNParagraphType.HEADING3;
        } else if (HTML.Tag.H4.equals(tag)) {
            return FNParagraphType.HEADING4;
        } else if (HTML.Tag.H5.equals(tag)) {
            return FNParagraphType.HEADING5;
        } else if (HTML.Tag.H6.equals(tag)) {
            return FNParagraphType.HEADING6;
        } else {
            return FNParagraphType.NORMAL;
        }
    }

}
