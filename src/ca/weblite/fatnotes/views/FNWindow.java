/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author shannah
 */
public class FNWindow extends JFrame {
    private final FNEditor editor;
    private final FNTitleLabel title = new FNTitleLabel();
    public final FNToolBar toolbar = new FNToolBar();
    public FNWindow(FNEditor editor) {
        this.editor = editor;
        JPanel north = new JPanel();
        north.setLayout(new BorderLayout());
        north.add(title, BorderLayout.WEST);
        
        
        north.add(toolbar, BorderLayout.CENTER);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(editor), BorderLayout.CENTER);
        getContentPane().add(north, BorderLayout.NORTH);
        
        
        
    }

    public void setDocumentTitle(String title) {
        this.title.setText(title);
    }
    
    private class FNTitleLabel extends JLabel {
        
    }
}
