/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.views;

import ca.weblite.fatnotes.editorkit.FNHTMLEditorKit;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.jdesktop.swingx.JXEditorPane;

/**
 *
 * @author shannah
 */
public class FNEditor extends JXEditorPane {

    private UndoManager undoManager;
    private UndoHandler undoHandler = new UndoHandler();
    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();
    
    public static final String REDO_ACTION = "redoKeystroke";
    public static final String UNDO_ACTION = "undoKeystroke";

    public FNEditor() {
        //HTMLEditorKit editorKit = new FNHTMLEditorKit();
        //setEditorKit(editorKit);
        //setDocument(editorKit.createDefaultDocument());
        //setContentType("text/html");
        setPreferredSize(new Dimension(800, 600));
        KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.META_MASK);
        KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.META_MASK);
        KeyStroke redoKeystroke2 = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.SHIFT_MASK | Event.META_MASK);

        undoAction = new UndoAction();
        getInputMap().put(undoKeystroke, UNDO_ACTION);
        getActionMap().put(UNDO_ACTION, undoAction);

        redoAction = new RedoAction();
        getInputMap().put(redoKeystroke, REDO_ACTION);
        getInputMap().put(redoKeystroke2, REDO_ACTION);
        getActionMap().put(REDO_ACTION, redoAction);
        getDocument().addUndoableEditListener(undoHandler);
        setDragEnabled(true);
        
        

    }

    @Override
    public void setText(String t) {
        super.setText(t); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    public UndoAction getUndoAction() {
        return undoAction;
    }
    
    public RedoAction getRedoAction() {
        return redoAction;
    }

    @Override
    public void setDocument(Document doc) {
        Document oldDoc = getDocument();
        if (oldDoc != null) {
            oldDoc.removeUndoableEditListener(undoHandler);
        }
        super.setDocument(doc);
        doc.addUndoableEditListener(undoHandler);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
    
   
    
    public void setUndoManager(UndoManager mgr) {
        
        this.undoManager = mgr;
    }

    class UndoHandler implements UndoableEditListener {

        /**
         * Messaged when the Document has created an edit, the edit is added to
         * <code>undoManager</code>, an instance of UndoManager.
         */
        public void undoableEditHappened(UndoableEditEvent e) {

            if (undoManager == null) {
                return;
            }
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    public class UndoAction extends AbstractAction {

        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (undoManager == null) {
                return;
            }
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
                // TODO deal with this
                //ex.printStackTrace();
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if (undoManager == null) {
                return;
            }
            if (undoManager.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    class RedoAction extends AbstractAction {

        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (undoManager == null) {
                return;
            }
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                // TODO deal with this
                ex.printStackTrace();
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if (undoManager == null) {
                return;
            }
            if (undoManager.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }
    
    public void resetUndoQueue() {
        if (undoManager != null) {
            undoManager.discardAllEdits();
        }
    }
    
    public UndoManager getUndoManager() {
        return undoManager;
    }
    
    
    
    public void insertHtml(String html) throws BadLocationException, IOException {
        UndoManager mgr = undoManager;
        UndoManager edit = new UndoManager();
        undoManager = edit;
        int selectionStart = getSelectionStart();
        HTMLDocument doc = (HTMLDocument)getDocument();
        Element el = doc.getCharacterElement(selectionStart).getParentElement();
        FNHTMLEditorKit kit = (FNHTMLEditorKit)getEditorKit();
        if ((html.trim().startsWith("<p ") || html.trim().startsWith("<p>")) && html.trim().endsWith("</p>")) {
            html = html.substring(html.indexOf(">")+1);
            html = html.substring(0, html.lastIndexOf("<"));
        }
        String uuid = UUID.randomUUID().toString();
        doc.insertString(selectionStart, uuid, null);
        //String innerText = doc.getText(el.getStartOffset(), el.getEndOffset() - el.getStartOffset());
        String innerText = kit.getInnerHtmlOfTag(el, doc);
        //System.out.println("Inner before: "+innerText);
        innerText = innerText.replace(uuid, html);
        //System.out.println("Inner after: "+innerText);
        doc.setInnerHTML(el, innerText);
        //System.out.println("After insertion "+ getText());
        edit.end();
        if (mgr != null) {
            mgr.addEdit(edit);
        }
        undoManager = mgr;
        
    }
    

   
    
}
