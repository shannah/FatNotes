/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.controllers;

import ca.weblite.fatnotes.editorkit.FNHTMLDocument;
import ca.weblite.fatnotes.editorkit.FNHTMLEditorKit;
import ca.weblite.fatnotes.models.FNContext;
import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNDocumentFileSet;
import ca.weblite.fatnotes.models.FNDocumentFragment;
import ca.weblite.fatnotes.views.FNEditor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.UUID;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import junit.framework.TestCase;

import org.semanticdesktop.aperture.util.FileUtil;

/**
 *
 * @author shannah
 */
public class FNEditorControllerTest extends TestCase {
    
    FNContext context;
    FNDocument document;
    FNEditorController controller;
    public FNEditorControllerTest() {
    }
    
    
    public static void setUpClass() {
    }
    
    
    public static void tearDownClass() {
    }
    
    
    public void setUp() throws Exception {
        context = new FNContext();
        File file = File.createTempFile("document", "");
        file.delete();
        file.mkdir();
        
        
        document = new FNDocument(file);
        controller = new FNEditorController(context, document); 
    }
    
    
    public void tearDown() {
        controller.close();
        FileUtil.deltree(document.getFile());
        
    }

    private static String stripSpaces(String str) {
        return str.replace(" ", "").replace("\n", "").replace("\r", "");
    }
    
    
    
    public void testInsertHTML() throws Exception {
        EventQueue.invokeAndWait(()->{
            try {
                _testInsertHTML();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    
    public void _testInsertHTML() throws Exception {
        
        UndoManager undo = new UndoManager();
        FNEditor editor = controller.getView();
        FNHTMLEditorKit kit = (FNHTMLEditorKit)editor.getEditorKit();
        editor.setUndoManager(undo);
        String contents = "<html><head></head><body><p>Hello World</p></body></html>";
        editor.setText(contents);
        
        assertEquals(stripSpaces(contents), stripSpaces(editor.getText()));
        
        editor.setSelectionStart(0);
        editor.setSelectionEnd(4);
        assertEquals("Hel", stripSpaces(editor.getSelectedText()));
        editor.setSelectionStart(4);

        HTMLDocument doc = (HTMLDocument)editor.getDocument();
        Position pos = doc.createPosition(editor.getSelectionStart());
        Element el = doc.getCharacterElement(1).getParentElement();
        undo.discardAllEdits();
        editor.insertHtml("<i>Foo</i>");
        
        
        //kit.insertHTML(doc, 1+editor.getSelectionStart(), "<i>Foo</i>", 0, 0, null);
        String expected = "<html><head></head><body><p>Hel<i>Foo</i>loWorld</p></body></html>";
        System.out.println(expected);
        System.out.println(stripSpaces(editor.getText()));
        assertEquals(expected, stripSpaces(editor.getText()));
        
        
        undo.undo();
        expected = "<html><head></head><body><p>HelloWorld</p></body></html>";
        System.out.println(expected);
        System.out.println(stripSpaces(editor.getText()));
        assertEquals(expected, stripSpaces(editor.getText()));
        
        
        
        editor.setText("<html><body><p>Hello <img src='example.gif'> World</p></body></html>");
        editor.setSelectionStart(8);
        editor.insertHtml("<i>Foo</i>");
        expected = "<html><head></head><body><p>Hello<imgsrc=\"example.gif\"><i>Foo</i>World</p></body></html>";
        System.out.println(expected);
        System.out.println(stripSpaces(editor.getText()));
        assertEquals(expected, stripSpaces(editor.getText()));
        
        contents = "<html><head></head><body><p>Hello World</p></body></html>";
        editor.setText(contents);
        editor.setSelectionStart(6);
        editor.insertHtml("<p>Insert</p>");
        expected = "<html><head></head><body><p>HelloInsertWorld</p></body></html>";
        System.out.println(expected);
        System.out.println(stripSpaces(editor.getText()));
        assertEquals(expected, stripSpaces(editor.getText()));
        
        
        contents = "<html><head></head><body><p>Hello World</p></body></html>";
        editor.setText(contents);
        kit.setBlockTag(3, (FNHTMLDocument)(HTMLDocument)editor.getDocument(), HTML.Tag.H1);
        expected = "<html><head></head><body><h1>HelloWorld</h1></body></html>";
        System.out.println(expected);
        System.out.println(stripSpaces(editor.getText()));
        assertEquals(expected, stripSpaces(editor.getText()));
    }

    
}
