/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.controllers;

import ca.weblite.fatnotes.models.FNContext;
import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.swinghelpers.FNUIManagerHelper;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author shannah
 */
public class FNAppController {
    private List<FNWindowController> windows = new ArrayList<>();
    private FNMainWindowController mainMenu;
    private FNContext context = new FNContext() {
        
        {
            getActionMap().put(FNContext.NEW_ACTION, 
                    new AbstractAction("New") {
                    
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        createNew();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                
                
            });
        }
        @Override
        protected void notifySaveLock() {
            synchronized(saveLock) {
                saveLock.notify();
            }
        }
        
        
        
    };
    
    private final Object saveLock = new Object();
    
    private Thread saveThread = new Thread(()->{
        while (true) {
            context.saveAll();
            synchronized(saveLock) {
                try {
                    Thread.sleep(1000);
                    saveLock.wait();
                } catch (InterruptedException ex) {}
            }
        }
    });
    
    
    
    
    public void launch() {
        FNUIManagerHelper.initUI();
        saveThread.start();
        showMainMenu();
    }
    
    public FNWindowController findDocumentWindow(File file) {
        for (FNWindowController win : windows) {
            if (win.getDocument().getFile().equals(file)) {
                return win;
            }
        }
        return null;
    }
    
    public void createNew() throws IOException {
        
        FNDocument doc = context.createNewDocument();
        
        FNWindowController window = new FNWindowController(this, doc) {
            @Override
            protected void windowClosed() {
                windows.remove(this);
                context.removeDocument(getDocument());
                
            }
            
        };
        windows.add(window);
        context.addDocument(doc);
        window.bringToFront();
    }
    
    public FNContext getContext() {
        return context;
    }
    
    public void open(File file) throws IOException {
        FNWindowController currentWindow = findDocumentWindow(file);
        if (currentWindow != null) {
            currentWindow.bringToFront();
            return;
        }
        FNDocument doc = context.findDocumentForFile(file);
        if (doc == null) {
            doc = new FNDocument(file);
            context.addDocument(doc);
        }
        
        FNWindowController window = new FNWindowController(this, doc) {
            @Override
            protected void windowClosed() {
                windows.remove(this);
                context.removeDocument(getDocument());
                
            }
            
        };
            
        windows.add(window);
        
        
        window.bringToFront();
        
    }
    
    public void showMainMenu() {
        if (mainMenu == null) {
            mainMenu = new FNMainWindowController(context) {
                @Override
                protected void openFile(File file) {
                    try {
                        open(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                
            };
        }
        mainMenu.show();
    }
    
    
    
}
