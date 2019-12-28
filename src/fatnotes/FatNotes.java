/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fatnotes;

import ca.weblite.fatnotes.controllers.FNAppController;
import ca.weblite.fatnotes.views.FNWindow;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author shannah
 */
public class FatNotes implements Runnable {

    private FNAppController app;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        eventQueue.push(new MyEventQueue());

        EventQueue.invokeLater(new FatNotes());
    }

    @Override
    public void run() {
        app = new FNAppController();
        app.launch();
        
        
    }
    
    public static class MyEventQueue extends EventQueue {

        @Override
        public void postEvent(AWTEvent event) {
            
            super.postEvent(event); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
}
