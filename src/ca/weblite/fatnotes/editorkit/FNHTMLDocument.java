/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editorkit;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author shannah
 */
public class FNHTMLDocument extends HTMLDocument {
    
    public FNHTMLDocument(StyleSheet ss) {
        super(ss);
    }
    
    public void fnWriteLock() {
        writeLock();
    }
    
    public void fnWriteUnlock() {
        writeUnlock();
    }

    @Override
    protected void create(ElementSpec[] data) {
        super.create(data); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    public HTMLEditorKit.ParserCallback getReader(int pos) {
        //Object desc = getProperty(Document.StreamDescriptionProperty);
        //if (desc instanceof URL) {
        //    setBase((URL)desc);
        //}
        return super.getReader(pos);
        //return new FNHTMLReader(pos);
    }

    @Override
    public HTMLEditorKit.ParserCallback getReader(int pos, int popDepth, int pushDepth, HTML.Tag insertTag) {
        //return new FNHTMLReader(pos, popDepth, pushDepth, insertTag);
        return super.getReader(pos, popDepth, pushDepth, insertTag);
    }
    
    

    @Override
    public boolean getPreservesUnknownTags() {
        return super.getPreservesUnknownTags(); //To change body of generated methods, choose Tools | Templates.
    }
 
    
    
    public class FNHTMLReader extends HTMLDocument.HTMLReader {

        public FNHTMLReader(int offset) {
            super(offset);
        }

        public FNHTMLReader(int pos, int popDepth, int pushDepth, HTML.Tag insertTag) {
            super(pos, popDepth, pushDepth, insertTag);
        }

        
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            super.handleStartTag(t, a, pos);
        }

        @Override
        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (t.toString().equals("attachment")) {
                System.out.println("Registering attachment tag");
                registerTag(t, new SpecialAction());
            }
            super.handleSimpleTag(t, a, pos); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void addSpecialElement(HTML.Tag t, MutableAttributeSet a) {
            super.addSpecialElement(t, a); 
        }

        @Override
        protected void addContent(char[] data, int offs, int length, boolean generateImpliedPIfNecessary) {
            super.addContent(data, offs, length, generateImpliedPIfNecessary); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void handleEndTag(HTML.Tag t, int pos) {
            super.handleEndTag(t, pos); //To change body of generated methods, choose Tools | Templates.
        }
        
        

        
        

    }
    
    
    
    
    
    
}
