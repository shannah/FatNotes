/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.controllers;

import ca.weblite.fatnotes.models.FNContext;
import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.views.FNMainMenuWindow;
import java.io.File;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author shannah
 */
public class FNMainWindowController {
    private FNContext context;
    private FNMainMenuWindow view;
    
    
    public FNMainWindowController(FNContext context) {
        this.context = context;
        
    }
   
    public void getNewAction(Action action) {
        
    }
    
    public void show() {
        if (view == null) {
            view = new FNMainMenuWindow(context) {
                @Override
                protected void documentNameChanged(File document, int column, String newName) throws Exception  {
                    FNMainWindowController.this.documentNameChanged(document, column, newName);
                }

                @Override
                protected void openFile(File file) {
                    FNMainWindowController.this.openFile(file);
                }
                
                
                
            };
            view.setJMenuBar(new FNMainWindowMenu());
        }
        if (!view.isVisible()) {
            view.pack();
            view.setVisible(true);
        } else {
            view.requestFocus();
        }
    }
    
    private void documentNameChanged(File document, int column, String newName) throws Exception {
        if (!document.getName().equals(newName)) {
            FNDocument doc = context.findDocumentForFile(document);
            if (doc == null) {
                doc = new FNDocument(document);
                
            }
            doc.changeName(newName);
            
        }
    }
    
    
    protected void openFile(File file) {
        
    }
    
    public class FNMainWindowMenu extends JMenuBar {

        public FNMainWindowMenu() {
            JMenu file = new JMenu("File");
            if (context.getActionMap().get(FNContext.NEW_ACTION) != null) {
                JMenuItem newNote = new JMenuItem(context.getActionMap().get(FNContext.NEW_ACTION));
                file.add(newNote);
            }
            add(file);
        }
        
    }
    
}
