/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editoractions;

import ca.weblite.fatnotes.controllers.FNEditorController;
import ca.weblite.fatnotes.util.datatransfer.FNDocumentFragmentTransferable;
import ca.weblite.fatnotes.views.FNEditor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author shannah
 */
public class CopyAction extends ProxyAction {
    private FNEditorController controller;
    public CopyAction(FNEditorController ctrl, Action defaultAction) {
        super(defaultAction);
        controller = ctrl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FNDocumentFragmentTransferable documentSelection = FNDocumentFragmentTransferable.createTransferableFragmentWithSelection(
                controller.getView(), 
                controller.getDocument()
        );
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(documentSelection, null);
        //super.actionPerformed(e);
    }
    
    
    
}
