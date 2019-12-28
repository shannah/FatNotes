/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editorkit;

import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNDocumentFile;
import ca.weblite.fatnotes.swinghelpers.HTMLDocumentHelper;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.Thumbnailer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.View;
import javax.swing.text.html.HTML;

/**
 *
 * @author shannah
 */
public class FNAttachmentView extends View {
    private Icon fileIcon;

    private FNDocument document;
    private FNDocumentFile file;
    private int w = 200;
    private int h = 200;
    private static final int BORDER_WIDTH = 10;
    private static JLabel fileLabel = new JLabel();
    static {
        fileLabel.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
    }

    public FNAttachmentView(FNDocument document, Element el) {
        super(el);
        this.document = document;
    }
    
   
    
   
    
    private FNDocumentFile getFile() {
       
        if (file == null) {
            String attachment = HTMLDocumentHelper.attr(getElement(), "data-attachment");
            
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
    
    /**
     * Determines the preferred span for this view along an
     * axis.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @return   the span the view would like to be rendered into;
     *           typically the view is told to render into the span
     *           that is returned, although there is no guarantee;
     *           the parent may choose to resize or break the view
     */
    @Override
    public float getPreferredSpan(int axis) {
        fileLabel.setText(getFile().getFile().getName());
        fileLabel.setIcon(getIcon());
        fileLabel.setVerticalTextPosition(BOTTOM);
        fileLabel.setHorizontalTextPosition(CENTER);
        if (axis == View.X_AXIS) {
            
            return fileLabel.getPreferredSize().width;
        } else {
            return fileLabel.getPreferredSize().height;
        }
    }

    @Override
    public void paint(Graphics g, Shape allocation) {
        //try {
            //ImageIcon icn = getFile().getThumbnail(allocation.getBounds().width, allocation.getBounds().height);
            //icn.paintIcon(null, g, allocation.getBounds().x, allocation.getBounds().y);
            
        //} catch (IOException ex) {
        //    ex.printStackTrace();
        //}
        
        JTextComponent tc = (JTextComponent)getContainer();
        Highlighter hl = tc.getHighlighter();
        if (hl instanceof LayeredHighlighter) {
            ((LayeredHighlighter)hl).paintLayeredHighlights(g,
                                                            getStartOffset(),
                                                            getEndOffset(),
                                                            allocation, tc, this);
        }
        
        
        fileLabel.setIcon(getIcon());
        fileLabel.setForeground(Color.blue);
        fileLabel.setText(getFile().getFile().getName());
        fileLabel.setVerticalTextPosition(BOTTOM);
        fileLabel.setHorizontalTextPosition(CENTER);
        g = g.create(allocation.getBounds().x, allocation.getBounds().y, allocation.getBounds().width, allocation.getBounds().height);
        fileLabel.setBounds(allocation.getBounds());
        fileLabel.paint(g);
        g.dispose();
    }

    
    
    @Override
    public Shape modelToView(final int pos, final Shape shape, final Bias bias)
        throws BadLocationException {

        Rectangle rc = shape.getBounds();
        if (pos <= getStartOffset()) {
            return new Rectangle(rc.x, rc.y, 0, rc.height);
        }
        return new Rectangle(rc.x + rc.width, rc.y, 0, rc.height);
    }

    @Override
    public int viewToModel(final float x, final float y, final Shape shape,
                           final Bias[] bias) {

        Rectangle rc = shape.getBounds();
        if (x < rc.x + rc.width/* / 2*/) {
            bias[0] = Bias.Forward;
            return getStartOffset();
        }
        bias[0] = Bias.Backward;
        return getEndOffset();
    }

    @Override
    public String getToolTipText(float x, float y, Shape allocation) {
        return file.getFile().getAbsolutePath();
    }

    
    
    
    
}
