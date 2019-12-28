/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shannah
 */
public class FileUtil {
    
    
    public static byte[] createChecksum(File filename) throws IOException {
        try {
            InputStream fis =  new FileInputStream(filename);
            
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;
            
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            
            fis.close();
            return complete.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
   }

   // see this How-to for a faster way to convert
   // a byte array to a HEX string
   public static String getMD5Checksum(File filename) throws IOException {
       byte[] b = createChecksum(filename);
       String result = "";

       for (int i=0; i < b.length; i++) {
           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
       }
       return result;
   }

    public static void copy(File source, File dest) throws IOException {
        InputStream is = null;
        
        OutputStream os = null;
        
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable t){}
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Throwable t){}
            }
            
        }
    }
    
    public static String readFileToString(File filePath) throws IOException
    {
        String content = new String ( Files.readAllBytes( filePath.toPath() ) );
 
        return content;
    }
    
    public static void writeStringToFile(String contents, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(contents.getBytes("UTF-8"));
        }
    }
    
    public static File createUniqueName(File file) {
        String base = file.getName();
        String index = "";
        if (base.matches("-\\d+$")) {
            index = base.substring(base.lastIndexOf("-")+1);
            base = base.substring(0, base.lastIndexOf("-"));
        }
        while (file.exists()) {
            index = index.isEmpty() ? "2" : "" + (Integer.parseInt(index)+1);
            file = new File(file.getParentFile(), base+index);
        }
        return file;
    }
}
