/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editorkit;

import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNDocumentFile;
import ca.weblite.fatnotes.swinghelpers.HTMLDocumentHelper;
import ca.weblite.fatnotes.util.datatransfer.FNAttachmentTransferHandler;
import ca.weblite.fatnotes.views.FNEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;

/**
 *
 * @author shannah
 */
public class FNAttachmentTagView extends ComponentView {
    private FNEditor editor;
    private FNDocument document;
    private static final int BORDER_WIDTH=10;
    private Icon fileIcon;
    private FNDocumentFile file;
    
    public FNAttachmentTagView(FNEditor editor, FNDocument document, Element elem) {
        super(elem);
        this.editor = editor;
        this.document = document;
    }
    
    
      
    private FNDocumentFile getFile() {
       
        if (file == null) {
            String attachment = (String)getElement().getAttributes().getAttribute("data-attachment");
            if (attachment == null) {
                attachment = (String)getElement().getAttributes().getAttribute("data-file");
            }
            File f = new File(
                    document.getFilesDirectory(),
                    attachment
            );
            file = new FNDocumentFile(document, f);
        }
        
        return file;
    }
    
    private Icon loadIcon() {
        try {
            Image img = ch.randelshofer.quaqua.osx.OSXFile.getIconImage(getFile().getFile(), 64);
            return new ImageIcon(img);
        } catch (Throwable t) {
        
            Icon fileIcon = FileSystemView.getFileSystemView().getSystemIcon(getFile().getFile());
            //fileIcon = ((ImageIcon) ico).getImage();
            return fileIcon;
        }
               
    }
    
    private Icon getIcon() {
        if (fileIcon == null) {
            return loadIcon();
        }
        return fileIcon;
    }

    @Override
    protected Component createComponent() {
        JLabel button = new JLabel("Button : text unknown") {
            @Override
            public void paint(Graphics g) {
                JTextComponent tc = (JTextComponent)getContainer();
                Highlighter hl = tc.getHighlighter();
                if (hl instanceof LayeredHighlighter) {
                    ((LayeredHighlighter)hl).paintLayeredHighlights(g,
                                                                    getStartOffset(),
                                                                    getEndOffset(),
                                                                    getBounds(), tc, FNAttachmentTagView.this);
                }
                super.paint(g);
            }
            
        };
        
        button.setTransferHandler(new FNAttachmentTransferHandler(getFile(), getElement(), editor));
        button.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        button.setIcon(getIcon());
        button.setForeground(Color.blue);
        button.setText(getFile().getFile().getName());
        button.setVerticalTextPosition(BOTTOM);
        button.setHorizontalTextPosition(CENTER);
        
       
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                HTMLDocumentHelper h = new HTMLDocumentHelper(editor);
                if (e.getClickCount() == 2) {
                    
                    FNDocumentFile file = getFile();
                    if (file != null) {
                        try {
                            file.openInSystemEditor();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                       
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
               
            }
            
            
            
        });
        
        button.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                 button.getTransferHandler().exportAsDrag(button, e, TransferHandler.MOVE);
            }
            
        });
 
        //try {
            //int start=getElement().getStartOffset();
            //int end=getElement().getEndOffset();
            String text = (String)getElement().getAttributes().getAttribute("data-attachment");
            if (text == null) {
                text = (String)getElement().getAttributes().getAttribute("data-file");
            }
            //String text = getElement().getDocument().getText(start, end-start);
            button.setText(text);
        //} catch (BadLocationException e) {
        //    e.printStackTrace();
        //}

        return button;
    }
    
    
    
}
