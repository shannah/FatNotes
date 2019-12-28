/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.models;

/**
 *
 * @author shannah
 */
public enum FNParagraphType {
    NORMAL("p", "Normal Text"),
    HEADING1("h1", "Heading 1"),
    HEADING2("h2", "Heading 2"),
    HEADING3("h3", "Heading 3"),
    HEADING4("h4", "Heading 4"),
    HEADING5("h5", "Heading 5"),
    HEADING6("h6", "Heading 6");
    
    private String tag;
    private String label;
    FNParagraphType(String htmlTag, String label) {
        this.tag = htmlTag;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
    
    
    
}
