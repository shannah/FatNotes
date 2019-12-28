/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.views;

import ca.weblite.fatnotes.models.FNContext;
import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JScrollPane;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTitledPanel;

/**
 *
 * @author shannah
 */
public class FNMainMenuWindow extends JXFrame {
    private FNDocumentList documentList;
    
    public FNMainMenuWindow(FNContext context) {
        setTitle("Fat Notes");
        documentList = new FNDocumentList(context) {
            @Override
            protected void documentNameChanged(File document, int column, String newName) throws Exception {
                FNMainMenuWindow.this.documentNameChanged(document, column, newName);
            }

            @Override
            protected void openFile(File file) {
                FNMainMenuWindow.this.openFile(file);
            }
            
            
            
        };
        JScrollPane scroller = new JScrollPane(documentList);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JXTitledPanel("Notes", scroller), BorderLayout.CENTER);
    }
    
    protected void documentNameChanged(File document, int column, String newName) throws Exception {
        
    }
    
    protected void openFile(File file) {
        
    }
}
