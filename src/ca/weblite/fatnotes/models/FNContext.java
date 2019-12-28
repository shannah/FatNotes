/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.models;

import ca.weblite.fatnotes.models.FNDocument.FNDocumentListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.ActionMap;

/**
 *
 * @author shannah
 */
public class FNContext {
    private File baseDir = new File(System.getProperty("user.home") + File.separator + ".fatnotes");
    private FNDocumentSet documents = new FNDocumentSet();
    private HashSet<FNDocument> saveQueue = new HashSet<FNDocument>();
    private ActionMap actionMap = new ActionMap();
    public static String NEW_ACTION = "new";
    
    public FNContext() {
        
    }
    
    public ActionMap getActionMap() {
        return actionMap;
    }
    
    public FNContext(File baseDir) {
        this.baseDir = baseDir;
    }
    
    private FNDocumentListener documentListener = new FNDocumentListener() {
        @Override
        public void bufferChanged(FNDocument document, String oldContents, String newContents) {
            synchronized(saveQueue) {
                saveQueue.add(document);
                
            }
            notifySaveLock();
        }
        
    };
    
    public File getFNDocumentsDir() {
        return new File(baseDir, "Documents");
    }
    
    public FNDocument findDocumentForFile(File f) {
        return documents.findDocumentForFile(f);
    }
    
    public void addDocument(FNDocument doc) {
        documents.add(doc);
        doc.addDocumentListener(documentListener);
    }
    
    public void removeDocument(FNDocument doc) {
        doc.removeDocumentListzener(documentListener);
        documents.remove(doc);
    }
    
    public FNDocument createNewDocument() throws IOException  {
        getFNDocumentsDir().mkdirs();
        File docsDir = getFNDocumentsDir();
        String index = "";
        String base = "Untitled";
        while (new File(docsDir, base+index).exists()) {
            if ("".equals(index)) {
                index = "1";
            }
            index = (Integer.parseInt(index)+1)+"";
        }
        File f = new File(docsDir, base+index);
        f.mkdir();
        return new FNDocument(f);
        
    }

    public void saveAll() {
        List<FNDocument> toSave;
        synchronized(saveQueue) {
            toSave = new ArrayList<>(saveQueue);
            saveQueue.clear();
        }
        for (FNDocument doc : toSave) {
            try {
                doc.save();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    protected void notifySaveLock() {
        
    }
    
    
    
}
