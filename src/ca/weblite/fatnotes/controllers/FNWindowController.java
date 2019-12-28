/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.controllers;

import ca.weblite.fatnotes.editorkit.FNHTMLDocument;
import ca.weblite.fatnotes.editorkit.FNHTMLEditorKit;
import ca.weblite.fatnotes.views.FNWindow;
import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNParagraphType;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.undo.UndoManager;
import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.swing.FontIcon;

/**
 *
 * @author shannah
 */
public class FNWindowController {
    private final FNAppController appController;
    private final FNWindow view;
    private final FNDocument document;
    private final FNEditorController editor;
    private static final int ICON_SIZE = 20;
    private static final Color ICON_COLOR = new Color(95, 99, 104);
    private UndoManager undoManager = new UndoManager();
    
    public FNWindowController(FNAppController appController, FNDocument document) {
        this.appController = appController;
        this.document = document;
        this.editor = new FNEditorController(appController.getContext(), document);
        this.editor.getView().setUndoManager(undoManager);
        
        
        this.view = new FNWindow(this.editor.getView());
        
        view.toolbar.undo.setAction(editor.getView().getUndoAction());
        setIcon(view.toolbar.undo, FontIcon.of(Material.UNDO, ICON_SIZE, ICON_COLOR));
        
        view.toolbar.redo.setAction(editor.getView().getRedoAction());
        setIcon(view.toolbar.redo, FontIcon.of(Material.REDO, ICON_SIZE, ICON_COLOR));
        
        view.toolbar.headings.addActionListener(evt->{
            if (disableHeadingsActionEvent) {
                disableHeadingsActionEvent = false;
                return;
            }
            JComboBox cb = (JComboBox)evt.getSource();
            FNParagraphType ptype = (FNParagraphType)cb.getSelectedItem();
            FNHTMLEditorKit kit = (FNHTMLEditorKit)editor.getView().getEditorKit();
            
            try {
                kit.setBlockTag(editor.getView().getSelectionStart(), (FNHTMLDocument) editor.getView().getDocument(), kit.getTagForParagraphType(ptype));
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (BadLocationException ex) {
                Logger.getLogger(FNWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        view.toolbar.bold.setAction(new StyledEditorKit.BoldAction());
        setIcon(view.toolbar.bold, FontIcon.of(Material.FORMAT_BOLD, ICON_SIZE, ICON_COLOR));
        view.toolbar.italic.setAction(new StyledEditorKit.ItalicAction());
        setIcon(view.toolbar.italic, FontIcon.of(Material.FORMAT_ITALIC, ICON_SIZE, ICON_COLOR));
        view.toolbar.underline.setAction(new StyledEditorKit.UnderlineAction());
        setIcon(view.toolbar.underline, FontIcon.of(Material.FORMAT_UNDERLINED, ICON_SIZE, ICON_COLOR));
        view.toolbar.left.setAction(new StyledEditorKit.AlignmentAction("Left", StyleConstants.ALIGN_LEFT));
        setIcon(view.toolbar.left, FontIcon.of(Material.FORMAT_ALIGN_LEFT, ICON_SIZE, ICON_COLOR));
        view.toolbar.center.setAction(new StyledEditorKit.AlignmentAction("Center", StyleConstants.ALIGN_CENTER));
        setIcon(view.toolbar.center, FontIcon.of(Material.FORMAT_ALIGN_CENTER, ICON_SIZE, ICON_COLOR));
        view.toolbar.right.setAction(new StyledEditorKit.AlignmentAction("Right", StyleConstants.ALIGN_RIGHT));
        setIcon(view.toolbar.right, FontIcon.of(Material.FORMAT_ALIGN_RIGHT, ICON_SIZE, ICON_COLOR));
        view.toolbar.justify.setAction(new StyledEditorKit.AlignmentAction("Justify", StyleConstants.ALIGN_JUSTIFIED));
        setIcon(view.toolbar.justify, FontIcon.of(Material.FORMAT_ALIGN_JUSTIFY, ICON_SIZE, ICON_COLOR));
        
        
        editor.getView().addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                EventQueue.invokeLater(()->{;
                    syncAttributesWithUI(((StyledEditorKit)editor.getView().getEditorKit()).getInputAttributes());
                });
            }
        });
        
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                editor.close();
                FNWindowController.this.windowClosed();
            }
            
            
            
        });
        view.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                editor.getView().updateUI();
            }
            
        });
        view.setTitle(document.getTitle());
        
        
        updateView();
    }
    private boolean disableHeadingsActionEvent = false;
    private void syncAttributesWithUI(AttributeSet attributes) {
        view.toolbar.bold.setSelected(StyleConstants.isBold(attributes));
        view.toolbar.italic.setSelected(StyleConstants.isItalic(attributes));
        view.toolbar.underline.setSelected(StyleConstants.isUnderline(attributes));
        view.toolbar.left.setSelected(StyleConstants.getAlignment(attributes) == StyleConstants.ALIGN_LEFT);
        view.toolbar.center.setSelected(StyleConstants.getAlignment(attributes) == StyleConstants.ALIGN_CENTER);
        view.toolbar.right.setSelected(StyleConstants.getAlignment(attributes) == StyleConstants.ALIGN_RIGHT);
        view.toolbar.justify.setSelected(StyleConstants.getAlignment(attributes) == StyleConstants.ALIGN_JUSTIFIED);
        FNHTMLEditorKit kit = (FNHTMLEditorKit)editor.getView().getEditorKit();
        Element blockEl = kit.getBlockElement(editor.getView().getSelectionStart(), (FNHTMLDocument) editor.getView().getDocument());
        
        if (blockEl != null) {
            HTML.Tag tag = (HTML.Tag)blockEl.getAttributes().getAttribute(StyleConstants.NameAttribute);
            FNParagraphType currSelection = (FNParagraphType)view.toolbar.headings.getSelectedItem();
            if (!currSelection.equals(kit.getParagraphTypeForTag(tag))) {
                System.out.println("Found block tag "+tag);
                disableHeadingsActionEvent = true;
                try {
                    view.toolbar.headings.setSelectedItem(kit.getParagraphTypeForTag(tag));
                } finally {
                    disableHeadingsActionEvent = false;
                }
            }
        }
    }
    
    private void setIcon(AbstractButton btn, Icon icon) {
        btn.setIcon(icon);
        btn.setToolTipText(btn.getText());
        btn.setText("");
        
    }
    
    protected void windowClosed() {
        
    }
    
    public void updateView() {
        view.setDocumentTitle(document.getTitle());
    }
    
    public FNDocument getDocument() {
        return document;
    }

    public void bringToFront() {
        if (!view.isVisible()) {
            view.pack();
            view.setVisible(true);
        } else {
            view.requestFocus();
        }
    }
    
    
   
    
}
