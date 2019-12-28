/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class HtmlSelection implements Transferable {

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

    private String html;

    public HtmlSelection(String html) {
        this.html = html;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return (DataFlavor[]) htmlFlavors.toArray(new DataFlavor[htmlFlavors.size()]);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return htmlFlavors.contains(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (String.class.equals(flavor.getRepresentationClass())) {
            return html;
        } else if (Reader.class.equals(flavor.getRepresentationClass())) {
            return new StringReader(html);
        } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
            return new StringBufferInputStream(html);
        }
        throw new UnsupportedFlavorException(flavor);
    }
}
