/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Scanner;
import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;

/**
 *
 * @author shannah
 */
public class RTFUtil {
    public static String toHTML(InputStream input) throws Exception {
        
    JEditorPane p = new JEditorPane();
    p.setContentType("text/rtf");

    EditorKit kitRtf = p.getEditorKitForContentType("text/rtf");
    kitRtf.read(input, p.getDocument(), 0);
    kitRtf = null;

    EditorKit kitHtml = p.getEditorKitForContentType("text/html");
    Writer writer = new StringWriter();
    kitHtml.write(writer, p.getDocument(), 0, p.getDocument().getLength());

    return writer.toString();
}
}
