/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author shannah
 */
public class FNDocumentFileSet implements Iterable<FNDocumentFile> {
    private FNDocument document;
    private List<FNDocumentFile> files = new ArrayList<>();

    public FNDocumentFileSet(FNDocument doc) {
        this.document = doc;
    }
    
    public FNDocumentFileSet(FNDocument doc, FNDocumentFile... files) {
        this(doc);
        for (FNDocumentFile f : files) {
            this.files.add(f);
        }
    }
    
    @Override
    public Iterator<FNDocumentFile> iterator() {
        return files.iterator();
    }
    
    public FNDocumentFileSet add(File... files) {
        for (File f : files) {
            this.files.add(new FNDocumentFile(document, f));
        }
        return this;
    }
    
    public FNDocumentFileSet add(FNDocumentFile... files) {
        for (FNDocumentFile f : files) {
            if (f.getDocument() != document) {
                throw new IllegalArgumentException("Attempt to add FNDocumentFile to different document file set.");
            }
            this.files.add(f);
        }
        return this;
    }
    
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        for (FNDocumentFile file : this) {
            sb.append(file.toHtml()).append(" ");
        }
        return sb.toString();
    }
    
}
