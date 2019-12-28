/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.swing.plaf;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author shannah
 */
public class FNButtonUI extends BasicButtonUI {

    public static final int BUTTON_HEIGHT = 24;
    @Override
    public void update(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        super.update(g, c);
        if (isInToolBar(button)) {
            button.setBackground(Color.WHITE);
        }
    }
    
     private boolean isInToolBar(AbstractButton button) {
        return SwingUtilities.getAncestorOfClass(JToolBar.class, button) != null;
    }
   
    
}
