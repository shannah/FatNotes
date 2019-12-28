/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util.datatransfer;

import ca.weblite.fatnotes.models.FNDocumentFile;
import ca.weblite.fatnotes.views.FNEditor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

/**
 *
 * @author shannah
 */
public class FNAttachmentTransferHandler extends TransferHandler {
    private FNDocumentFile file;
    private Element element;
    private FNEditor editor;

    public FNAttachmentTransferHandler(FNDocumentFile file, Element element, FNEditor editor) {
        this.file = file;
        this.element = element;
        this.editor= editor;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{
                    DataFlavor.javaFileListFlavor
                };
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.javaFileListFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (!DataFlavor.javaFileListFlavor.equals(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                List<File> out = new ArrayList<>();
                out.add(file.getFile());
                return out;
            }
            
        };
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {
            try {
                editor.getDocument().remove(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
            } catch (BadLocationException ex) {
            
            }
        }
        super.exportDone(source, data, action);
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        super.exportAsDrag(comp, e, action); 
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    
    
    
    
    
}
