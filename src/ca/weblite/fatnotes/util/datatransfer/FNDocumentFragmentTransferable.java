/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util.datatransfer;

import ca.weblite.fatnotes.models.FNDocument;
import ca.weblite.fatnotes.models.FNDocumentFragment;
import ca.weblite.fatnotes.views.FNEditor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

/**
 *
 * @author shannah
 */
public class FNDocumentFragmentTransferable implements Transferable {
    
    public static DataFlavor documentFragmentFlavor =  null; 
    
    
    
    
    static {
        try {
            documentFragmentFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\""+FNDocumentFragment.class.getName()+"\"");
        } catch (ClassNotFoundException cnfe) {
            
        }
    }
    
    private static List<DataFlavor> htmlFlavors = new ArrayList<>(3);

        static {

            try {
                htmlFlavors.add(new DataFlavor("text/html;class=java.lang.String"));
                htmlFlavors.add(new DataFlavor("text/html;class=java.io.Reader"));
                htmlFlavors.add(new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"));
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

        }
    public static DataFlavor serializedDocumentFragmentFlavor =  null; 
    static {
        try {
            serializedDocumentFragmentFlavor = new DataFlavor(DataFlavor.javaSerializedObjectMimeType + ";class=\""+FNDocumentFragment.class.getName()+"\"");
        } catch (ClassNotFoundException cnfe) {
            
        }
    }
    
    private FNDocumentFragment payload;
    
    
    public FNDocumentFragmentTransferable(FNDocumentFragment payload) {
        this.payload = payload;
    }
    
    public static FNDocumentFragmentTransferable createTransferableFragmentWithSelection(FNEditor editor, FNDocument doc) {
        int startPos = editor.getSelectionStart();
        int endPos = editor.getSelectionEnd();
        String selectedString = editor.getSelectedText();
        HTMLDocument htmlDoc = (HTMLDocument)editor.getDocument();
        StringWriter buf = new StringWriter();
        HTMLWriter htmlWriter = new HTMLWriter(buf, htmlDoc, startPos, endPos-startPos);
        
        try {
            htmlWriter.write();
        } catch (IOException|BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        
        String htmlFragment = buf.toString();
        System.out.println("html fragment is "+htmlFragment);
        FNDocumentFragment frag = new FNDocumentFragment();
        frag.setStartPos(startPos);
        frag.setEndPos(endPos);
        frag.setStringSegment(selectedString);
        frag.setHtmlFragment(htmlFragment);
        frag.setDocumentName(doc.getTitle());
        return new FNDocumentFragmentTransferable(frag);
        
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[] {
                documentFragmentFlavor,
                serializedDocumentFragmentFlavor,
                htmlFlavors.get(0),
                htmlFlavors.get(1),
                htmlFlavors.get(2),
                DataFlavor.javaFileListFlavor,
                DataFlavor.fragmentHtmlFlavor,
                DataFlavor.stringFlavor
                
            };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        
        return Arrays.asList(getTransferDataFlavors()).contains(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(documentFragmentFlavor)) {
            return payload;
        } else if (flavor.equals(serializedDocumentFragmentFlavor)) {
            return payload;
        } else if (flavor.equals(DataFlavor.javaFileListFlavor)) {
            return Arrays.asList(payload.getDocument().getFile());
                    
        } else if (flavor.equals(DataFlavor.fragmentHtmlFlavor) || htmlFlavors.contains(flavor)) {
            return payload.getHtmlFragment();
        } else if (String.class.equals(flavor.getRepresentationClass())) {
            return payload.getStringSegment();
        } else if (Reader.class.equals(flavor.getRepresentationClass())) {
            return new StringReader(payload.getStringSegment());
        } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
            return new StringBufferInputStream(payload.getStringSegment());
        }
        return new UnsupportedFlavorException(flavor);
    }
    
    public FNDocumentFragment getPayload() {
        return payload;
    }
    
}
