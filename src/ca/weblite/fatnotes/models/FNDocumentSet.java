/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author shannah
 */
public class FNDocumentSet implements Iterable<FNDocument> {
    
    
    private ArrayList<FNDocument> documents = new ArrayList<>();

    @Override
    public Iterator<FNDocument> iterator() {
        return documents.iterator();
    }
    
    public FNDocument findDocumentForFile(File file) {
        for (FNDocument doc : documents) {
            if (doc.getFile().equals(file) || doc.getFile().getParentFile().equals(file)) {
                return doc;
            }
        }
        return null;
    }
    
    public void add(FNDocument doc) {
        documents.add(doc);
    }
    
    public void remove(FNDocument doc) {
        documents.remove(doc);
    }
}
