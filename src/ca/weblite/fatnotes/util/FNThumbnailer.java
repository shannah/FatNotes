/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.util;

import de.uni_siegen.wineme.come_in.thumbnailer.ThumbnailerException;
import de.uni_siegen.wineme.come_in.thumbnailer.ThumbnailerManager;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODExcelConverterThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODHtmlConverterThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODPowerpointConverterThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODWordConverterThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.NativeImageThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.OpenOfficeThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.PDFBoxThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.ScratchThumbnailer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jirau.DWGThumbnailer;

/**
 *
 * @author shannah
 */
public class FNThumbnailer {
    private static ThumbnailerManager thumbnailer;
    static {
        try {
            thumbnailer = new ThumbnailerManager();
            if (classExists("de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.NativeImageThumbnailer"))
			thumbnailer.registerThumbnailer(new NativeImageThumbnailer());

		thumbnailer.registerThumbnailer(new OpenOfficeThumbnailer());
		thumbnailer.registerThumbnailer(new PDFBoxThumbnailer());
		
		try {
			thumbnailer.registerThumbnailer(new JODWordConverterThumbnailer());
			thumbnailer.registerThumbnailer(new JODExcelConverterThumbnailer());
			thumbnailer.registerThumbnailer(new JODPowerpointConverterThumbnailer());
			thumbnailer.registerThumbnailer(new JODHtmlConverterThumbnailer());
		} catch (IOException e) {
			e.printStackTrace();
		}

		thumbnailer.registerThumbnailer(new ScratchThumbnailer());
		
		thumbnailer.registerThumbnailer(new DWGThumbnailer());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void generateThumbnail(File file, File dest, int width, int height) throws IOException {
        thumbnailer.setImageSize(width, height, 0);
        try {
            thumbnailer.generateThumbnail(file, dest);
        } catch (ThumbnailerException ex) {
            throw new IOException(ex);
        }
                
    } 
    
    private static boolean classExists(String qualifiedClassname) {
        try {
            Class.forName(qualifiedClassname);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
