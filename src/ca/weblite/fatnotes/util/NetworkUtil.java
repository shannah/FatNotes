/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author shannah
 */
public class NetworkUtil {
    public static InputStream openConnection(URL u) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)u.openConnection();
        conn.setInstanceFollowRedirects(true);
        return conn.getInputStream();
    }
    
    public static void downloadToFile(URL u, File dest) throws IOException {
        try (InputStream input = openConnection(u); FileOutputStream output = new FileOutputStream(dest)) {
            IOUtil.copy(input, output);
        }
    }
}
