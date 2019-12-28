/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editorkit;

import ca.weblite.fatnotes.views.FNEditor;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.ImageView;

/**
 *
 * @author shannah
 */
public class FNImageView extends ImageView {
    private float maxHeight=-1;
    public FNEditor editor;
    public FNImageView(FNEditor editor, Element elem) {
        super(elem);
        this.editor = editor;
    }

    private int calcMaxWidth() {
        JScrollPane scrollPane = scrollPane();
        if (scrollPane != null) {
            return (int)(scrollPane.getWidth() * 0.9f);
        }
        return 100000;
    }
    
    @Override
    public float getPreferredSpan(int axis) {
        
        float span = super.getPreferredSpan(axis); 
        if (axis == X_AXIS) {
            JScrollPane scrollPane = scrollPane();
            if (scrollPane != null) {
                float maxWidth = scrollPane.getWidth() * 0.9f;
                if (span > maxWidth) {
                    float ratio = maxWidth/span;
                    maxHeight = super.getPreferredSpan(Y_AXIS) * ratio;
                    span = maxWidth;
                } else {
                    maxHeight = -1;
                }
            } else {
                maxHeight = -1;
            }
            
        } else {
            if (maxHeight > 0) {
                span = maxHeight;
            }
        }
        return span;
    }
    
    private JScrollPane scrollPane() {
        Container cmp = editor;
        while (cmp != null && !(cmp instanceof JScrollPane)) {
            cmp = cmp.getParent();
        }
        if (cmp instanceof JScrollPane) {
            return (JScrollPane)cmp;
        }
        return null;
    }

    @Override
    public void paint(Graphics g, Shape a) {
        getPreferredSpan(X_AXIS);
        Image im = getImage();
        Rectangle bounds = a.getBounds();
        if (im != null) {
            if (im.getWidth(editor) > bounds.width) {
                Graphics2D g2 = (Graphics2D)g.create();
                AffineTransform t = new AffineTransform();

                double tx = bounds.x;
                double ty = bounds.y;
                t.translate(tx, ty);
                double ratio = im.getWidth(editor) / (double)bounds.width;
                t.scale(1/ratio, 1/ratio);
                t.translate(-tx, -ty);
                AffineTransform t2 = g2.getTransform();
                t2.concatenate(t);
                g2.setTransform(t2);
                //g2.setClip(bounds.x, bounds.y, (int)(bounds.width * ratio), (int)(bounds.height * ratio));
                super.paint(g2, new Rectangle(bounds.x, bounds.y, (int)(bounds.width * ratio), (int)(bounds.height * ratio)));
                g2.dispose();
            } else {
                super.paint(g, a);
            }
        } else {
            super.paint(g, a);
        }
    }

    
    
   
   
    
    
}
