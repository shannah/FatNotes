/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.views;


import ca.weblite.fatnotes.models.FNParagraphType;
import com.sun.java.accessibility.util.SwingEventMonitor;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicToggleButtonUI;

/**
 *
 * @author shannah
 */
public class FNToolBar extends JPanel {
    private static final Color selectedBackgroundColor = new Color(232, 240, 254);
    private static final Color unselectedBackgroundColor = Color.WHITE;
    
    public final JToggleButton bold = new JToggleButton("Bold"), italic = new JToggleButton("Italic"), underline = new JToggleButton("underline");
    public final JToggleButton left = new JToggleButton("Left"), center = new JToggleButton("Center"), right = new JToggleButton("Right"), justify = new JToggleButton("Justify");
    public final ButtonGroup alignment = new ButtonGroup();
    {
        alignment.add(left);
        alignment.add(center);
        alignment.add(right);
        alignment.add(justify);
    }
    public final JButton undo = new JButton("Undo"), redo = new JButton("Redo");
    public final JComboBox headings = new JComboBox(FNParagraphType.values());
    
    public FNToolBar() {
        BoxLayout l = new BoxLayout(this,BoxLayout.X_AXIS);
        
        setLayout(l);
        setBackground(Color.WHITE);
        
        
        add(undo);
        undo.setUI(new FNButtonUI());
        
        add(redo);
        redo.setUI(new FNButtonUI());
        
        add(new JSeparator(JSeparator.VERTICAL));
        
        add(headings);
        headings.setUI(new FNComboBoxUI());
        
        add(new JSeparator(JSeparator.VERTICAL));
        
        
        add(bold);
        bold.setUI(new FNToggleButtonUI());
        
        add(italic);
        italic.setUI(new FNToggleButtonUI());
        
        add(underline);
        underline.setUI(new FNToggleButtonUI());
        
        add(new JSeparator(JSeparator.VERTICAL));
        add(left);
        left.setUI(new FNToggleButtonUI());
        
        add(center);
        center.setUI(new FNToggleButtonUI());
        
        add(right);
        right.setUI(new FNToggleButtonUI());
        
        add(justify);
        justify.setUI(new FNToggleButtonUI());
        
        
        
        
        
        
        
    }

    
    
    private void syncStyles(AbstractButton btn) {
        btn.setBorderPainted(false);
        //btn.setBorder(BorderFactory.createEmptyBorder());
        if (btn.isSelected()) {
            System.out.println("selected");
            
            btn.setBackground(selectedBackgroundColor);
        } else {
            btn.setBackground(unselectedBackgroundColor);
            System.out.println("Unselected");
        }
        btn.repaint();
    }
    
    private class FNButtonUI extends BasicButtonUI {

        public FNButtonUI() {
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c); //To change body of generated methods, choose Tools | Templates.
            AbstractButton b = (AbstractButton)c;
            c.setRequestFocusEnabled(false);
            b.setMargin(new Insets(3, 3, 3, 3));
            b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            b.setBackground(unselectedBackgroundColor);
            
            b.setText("");
        }
        
        

        @Override
        public void update(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton)c;
            if (!b.getText().isEmpty()) {
                b.setText("");
            }
            super.update(g, c);
            
            
            
            
            
        }
        
    }
    
    private class FNToggleButtonUI extends BasicToggleButtonUI {
        
        @Override
        public void installUI(JComponent c) {
            
            super.installUI(c); //To change body of generated methods, choose Tools | Templates.
            JToggleButton b = (JToggleButton)c;
            c.setRequestFocusEnabled(false);
            
            b.setMargin(new Insets(3, 3, 3, 3));
            b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        }
        
        

        @Override
        public void update(Graphics g, JComponent c) {
            super.update(g, c);
            JToggleButton b = (JToggleButton)c;
            if (b.isSelected()) {
                b.setBackground(selectedBackgroundColor);
            } else {
                b.setBackground(unselectedBackgroundColor);
            }
        }
        
    }
    
    private class FNComboBoxUI extends BasicComboBoxUI {

        @Override
        public void installUI(JComponent c) {
            c.setRequestFocusEnabled(false);
            JComboBox cb = (JComboBox)c;
            super.installUI(c); 
            arrowButton.setUI(new FNButtonUI());
            cb.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            c.setBackground(unselectedBackgroundColor);
        }
        
    }
    
    
    
    
}
