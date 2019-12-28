/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.editorkit;

import ca.weblite.fatnotes.views.FNEditor;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument.HTMLReader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.AttributeList;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.DocumentParser;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author shannah
 */
public class FNParserDelegator extends ParserDelegator {
    private static DTD dtd;
    private FNEditor editor;
    public FNParserDelegator(FNEditor editor) {
        this.editor = editor;
        setDefaultDTD();
        
    }

    /**
     * Sets the default DTD.
     */
    protected static void setDefaultDTD() {
        getDefaultDTD();
    }

    private static synchronized DTD getDefaultDTD() {
        
        if (dtd == null) {
            DTD _dtd = null;
            // (PENDING) Hate having to hard code!
            String nm = "html32";
            try {
                _dtd = DTD.getDTD(nm);
            } catch (IOException e) {
                // (PENDING) UGLY!
                System.out.println("Throw an exception: could not get default dtd: " + nm);
            }
            dtd = createDTD(_dtd, nm);

        }

        return dtd;
    }

    /**
     * Recreates a DTD from an archived format with the specified {@code name}.
     *
     * @param dtd a DTD
     * @param name the name of the resource, relative to the  ParserDelegator class.
     * @return the DTD with the specified {@code name}.
     */
    protected static DTD createDTD(DTD dtd, String name) {

        InputStream in = null;
        boolean debug = true;
        try {
            String path = name + ".bdtd";
            in = getResourceAsStream(path);
            if (in != null) {
                dtd.read(new DataInputStream(new BufferedInputStream(in)));
                DTD.putDTDHash(name, dtd);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        javax.swing.text.html.parser.Element img=dtd.getElement("img");
        javax.swing.text.html.parser.Element p=dtd.getElement("p");
        
        AttributeList atts = new AttributeList("data-file");
        atts.type = 1;
        atts.modifier = 2;
        javax.swing.text.html.parser.Element attachment = dtd.defineElement("attachment", img.getType(), img.omitStart(), img.omitEnd(),img.getContent(),img.exclusions, img.inclusions,atts);
        
        
        return dtd;
    }

   

    public void parse(Reader r, HTMLEditorKit.ParserCallback cb, boolean ignoreCharSet) throws IOException {
        //if (!(cb instanceof FNHTMLDocument.FNHTMLReader)) {
        //    
        //   cb = ((FNHTMLDocument)editor.getDocument()).getReader(0);
        //}
        new DocumentParser(getDefaultDTD()).parse(r, cb, ignoreCharSet);
    }

    /**
     * Fetch a resource relative to the ParserDelegator classfile.
     * If this is called on 1.2 the loading will occur under the
     * protection of a doPrivileged call to allow the ParserDelegator
     * to function when used in an applet.
     *
     * @param name the name of the resource, relative to the
     *  ParserDelegator class.
     * @return a stream representing the resource
     */
    static InputStream getResourceAsStream(final String name) {
        return AccessController.doPrivileged(
            new PrivilegedAction<InputStream>() {
                public InputStream run() {
                    return ParserDelegator.class.getResourceAsStream(name);
                }
        });
    }

    private void readObject(ObjectInputStream s)
        throws ClassNotFoundException, IOException {
        s.defaultReadObject();
        setDefaultDTD();
    }
}
