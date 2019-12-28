/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.models;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author shannah
 */
public class FNDocumentFragment implements Serializable {
    
    /**
     * @return the documentName
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * @param documentName the documentName to set
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * @return the htmlFragment
     */
    public String getHtmlFragment() {
        return htmlFragment;
    }

    /**
     * @param htmlFragment the htmlFragment to set
     */
    public void setHtmlFragment(String htmlFragment) {
        this.htmlFragment = htmlFragment;
    }
    private String documentName;
    private String htmlFragment;
    private String stringSegment;
    private int startPos, endPos;
    private File contextRoot;
    
    public FNDocument getDocument(FNContext context) {
        File f = new File(context.getFNDocumentsDir(), documentName);
        FNDocument doc = context.findDocumentForFile(f);
        if (doc == null) {
            doc = new FNDocument(f);
        }
        return doc;
    }
    
    public FNDocument getDocument() {
        
        FNContext ctx = getContextRoot() == null ? new FNContext() : new FNContext(getContextRoot());
        
        return getDocument(ctx);
    }

    /**
     * @return the stringSegment
     */
    public String getStringSegment() {
        return stringSegment;
    }

    /**
     * @param stringSegment the stringSegment to set
     */
    public void setStringSegment(String stringSegment) {
        this.stringSegment = stringSegment;
    }

    /**
     * @return the startPos
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * @param startPos the startPos to set
     */
    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    /**
     * @return the endPos
     */
    public int getEndPos() {
        return endPos;
    }

    /**
     * @param endPos the endPos to set
     */
    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    /**
     * @return the contextRoot
     */
    public File getContextRoot() {
        return contextRoot;
    }

    /**
     * @param contextRoot the contextRoot to set
     */
    public void setContextRoot(File contextRoot) {
        this.contextRoot = contextRoot;
    }
}
