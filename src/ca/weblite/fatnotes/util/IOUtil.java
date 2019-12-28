/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author shannah
 */
public class IOUtil {
    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[4096];
        int len=0;
        while ((len = input.read(buf)) > -1) {
            if (len > 0) {
                output.write(buf, 0, len);
            }
        }
    }
}
