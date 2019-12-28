/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.models;

import ca.weblite.fatnotes.util.FNThumbnailer;
import java.awt.Desktop;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.text.Element;

/**
 *
 * @author shannah
 */
public class FNDocumentFile {
    private final FNDocument document;
    private final File file;
    private Map<String,ImageIcon> thumbnails = new HashMap<>();
    private static int DEFAULT_THUMB_WIDTH = 100;
    private static int DEFAULT_THUMB_HEIGHT = 100;
    
    public FNDocumentFile(FNDocument doc, File file) {
        this.document = doc;
        this.file = file;
    }
    
    public FNDocument getDocument() {
        return document;
    }
    
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        //sb.append("<a href='#' data-attachment='").append(file.getName()).append("'>").append(file.getName()).append("</a>");
        //sb.append("<a href='#' data-attachment='").append(file.getName()).append("'>").append("&nbsp;").append("</a>");
        sb.append("<img data-attachment=\"").append(file.getName()).append("\" >");

        return sb.toString();
    }
    
    public void openInSystemEditor() throws IOException {
        Desktop.getDesktop().edit(file);
    }

    public File getFile() {
        return file;
    }
    
    
    
    public File getThumbnailFile(int w, int h) {
        return new File(document.getThumbnailsDirectory(), file.getName()+".thumb."+w+"x"+h+".png");
    }
    
    public ImageIcon generateThumbnail(int w, int h) throws IOException {
        File thumbs = document.getThumbnailsDirectory();
        
        if (!thumbs.exists()) {
            thumbs.mkdir();
        }
        if (file.exists()) {
            FNThumbnailer.generateThumbnail(file, getThumbnailFile(w, h), w, h);
        } else {
            URL brokenImage = getClass().getResource("/ca/weblite/fatnotes/assets/baseline_broken_image_black_48dp.png");
            return new ImageIcon(brokenImage);
        }
        
        return new ImageIcon(getThumbnailFile(w, h).getAbsolutePath());
        
        
    }
    
    public ImageIcon loadThumbnail(int w, int h) throws IOException {
        File thumbnailFile = getThumbnailFile(w, h);
        if (!thumbnailFile.exists()) {
            return generateThumbnail(w, h);
        }
        return new ImageIcon(thumbnailFile.getAbsolutePath());
    }
    
    public ImageIcon getThumbnail(int w, int h) throws IOException{
        ImageIcon thumb = thumbnails.get(w+"x"+h);
        if (thumb == null) {
            thumb = loadThumbnail(w, h);
            thumbnails.put(w+"x"+h, thumb);
        }
        return thumb;
    }
    
}
