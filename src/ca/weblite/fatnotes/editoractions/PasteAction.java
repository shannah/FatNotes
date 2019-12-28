/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editoractions;

import ca.weblite.fatnotes.controllers.FNEditorController;
import ca.weblite.fatnotes.models.FNDocumentFileSet;
import ca.weblite.fatnotes.models.FNDocumentFragment;
import ca.weblite.fatnotes.util.RTFUtil;
import ca.weblite.fatnotes.util.datatransfer.FNDocumentFragmentTransferable;
import ca.weblite.fatnotes.util.datatransfer.HtmlSelection;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.TransferHandler;

/**
 *
 * @author shannah
 */
public class PasteAction extends ProxyAction {
    private FNEditorController controller;
    public PasteAction(FNEditorController controller, Action defaultPasteAction) {
        super(defaultPasteAction);
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        TransferHandler th = controller.getView().getTransferHandler();
        try {
            th.importData(controller.getView(), t);
        } catch (Throwable ex){}
    }
    
    public void actionPerformed2(ActionEvent e) {
        System.out.println("Performing paste");
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        StringBuilder sb = new StringBuilder();
        
        if (t == null)
            return;
        try {
            FNDocumentFragment fragment = (FNDocumentFragment) t.getTransferData(FNDocumentFragmentTransferable.documentFragmentFlavor);
            controller.importFragment(fragment);
            return;
        } catch (UnsupportedFlavorException|IOException ex){
            if (ex instanceof IOException) {
                ex.printStackTrace();
            }
        }
        try {
            List<File> files = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
            
            FNDocumentFileSet fs = controller.importFiles(files.toArray(new File[files.size()]));
            String html = fs.toHtml();
            /*
            //HtmlSelection htmlSelection = new HtmlSelection(html);
            StringSelection htmlSelection = new StringSelection(html);
            c.setContents(htmlSelection, null );
            System.out.println("Contents is now "+htmlSelection+" from "+html);
            System.out.println("T: "+c.getContents(this));
            for (DataFlavor fl : c.getContents(this).getTransferDataFlavors()) {
                System.out.println("Flavour: "+fl);
            }
            controller.paste();
            return;
*/  
            controller.insertHtml(html);
            return;
                    
        } catch (Exception ex){

            
        }
        
        try {
            InputStream rtfInput = (InputStream)t.getTransferData(new DataFlavor("text/rtf;representationclass=java.io.InputStream"));
            String html = RTFUtil.toHTML(rtfInput);
            
            controller.insertHtml(html);
            return;
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        
        try {
            String htmlInput = ""+t.getTransferData(DataFlavor.allHtmlFlavor);
            //System.out.println("Html is "+htmlInput);
            htmlInput = controller.importImages(htmlInput, null, null);
            System.out.println("html -< "+htmlInput);
            controller.insertHtml(""+htmlInput);
            return;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        
        System.out.println(Arrays.toString(t.getTransferDataFlavors()));
        super.actionPerformed(e); 
    }
    
    
}
