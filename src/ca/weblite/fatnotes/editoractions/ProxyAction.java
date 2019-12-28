/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editoractions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author shannah
 */
public class ProxyAction extends AbstractAction {
    private Action action;
    
    public ProxyAction(Action defaultAction) {
        this.action = defaultAction;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Performing default action "+action+" with event "+e);
        action.actionPerformed(e);
    }
    
}
