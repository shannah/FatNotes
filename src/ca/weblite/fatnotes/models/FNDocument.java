/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.models;

import ca.weblite.fatnotes.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *
 * @author shannah
 */
public class FNDocument {
    private File file;
    private boolean isTemp;
    private String buffer;
    private List<FNDocumentEditor> editors = new ArrayList<>();
    private List<FNDocumentListener> listeners = new ArrayList<>();

    public void save() throws IOException {
        FileUtil.writeStringToFile(buffer, new File(file, "index.html"));
    }
    
    public static interface FNDocumentListener {
        public void bufferChanged(FNDocument document, String oldContents, String newContents);
    }
    
    public static interface FNDocumentEditor {
        public void flushBuffer();
    }
    
    public void addDocumentEditor(FNDocumentEditor editor) {
        editors.add(editor);
    }
    
    public void removeDocumentEditor(FNDocumentEditor editor) {
        editors.remove(editor);
    }
    
    public void setBuffer(String buffer) {
        boolean changed = false;
        if (!buffer.equals(this.buffer)) {
            changed = true;
        }
        this.buffer = buffer;
        fireBufferChanged();
    }
    
    private void fireBufferChanged() {
        for (FNDocumentListener l : listeners) {
            l.bufferChanged(this, null, buffer);
        }
    }
    
    public FNDocument(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }
    
    public File getFilesDirectory() {
        return new File(file, "files");
    }
    
    public File getImagesDirectory() {
        return new File(file, "images");
    }
    
    public File getThumbnailsDirectory() {
        return new File(file, "thumbnails");
    }
    
    
    
    public FNDocumentFileSet importFiles(File... files) throws IOException {
        List<File> destFiles = new ArrayList<>();
        getFilesDirectory().mkdirs();
        for (File f : files) {
            if (!f.exists()) {
                throw new IOException("Cannot import file "+f+" because it does not exist.");
            }
            if (f.getParentFile().equals(getFilesDirectory())) {
                destFiles.add(f);
            } else {
                
                File dest = new File(getFilesDirectory(), f.getName());
                if (dest.exists()) {
                    String destMd5 = FileUtil.getMD5Checksum(dest);
                    String srcMd5 = FileUtil.getMD5Checksum(f);
                    if (destMd5.equals(srcMd5)) {
                        destFiles.add(f);
                        continue;
                    }
                }
                
                FileUtil.copy(f, dest);
                destFiles.add(dest);
            }
        }
        FNDocumentFileSet out = new FNDocumentFileSet(this);
        for (File f : destFiles) {
            out.add(f);
        }
        return out;
    }
    
    public String loadContentsAsHTML() throws IOException {
        buffer = FileUtil.readFileToString(new File(file, "index.html"));
        return buffer;
    }
    
    public String getBuffer() {
        return buffer;
    }
    
    public String getTitle() {
        return file.getName();
    }
    
    

    public void changeName(String newName) throws IOException {
        File newFile = new File(file.getParentFile(), newName);
        if (newFile.exists()) {
            throw new IOException("File named "+newName+" already exists");
        }
        flushBuffers();
        
        
        
    }
    
    public void flushBuffers() {
        for (FNDocumentEditor editor : editors) {
            editor.flushBuffer();
        }
    }
    
    public void addDocumentListener(FNDocumentListener l) {
        listeners.add(l);
    }
    
    public void removeDocumentListzener(FNDocumentListener l) {
        listeners.remove(l);
    }
    
    public class ImportFilesResult implements Iterable<ImportFileResult>{
        private List<ImportFileResult> results = new ArrayList<>();

        @Override
        public Iterator<ImportFileResult> iterator() {
            return results.iterator();
        }
        
        public void add(ImportFileResult res) {
            results.add(res);
        }
        
    }
    
    public class ImportFileResult {

        /**
         * @return the source
         */
        public FNDocumentFile getSource() {
            return source;
        }

        /**
         * @return the dest
         */
        public FNDocumentFile getDest() {
            return dest;
        }
        private FNDocumentFile source;
        private FNDocumentFile dest;
        private Throwable error;
        
        ImportFileResult() {
            
        }
        
        public Throwable getError() {
            return error;
        }
        
        
        
               
    }
    
    public ImportFilesResult importFiles(FNDocumentFileSet files) {
        ImportFilesResult res = new ImportFilesResult();
        getFilesDirectory().mkdirs();
        for (FNDocumentFile file : files) {
            ImportFileResult currRes = new ImportFileResult();
            currRes.source = file;
            FNDocument fileDoc = file.getDocument();
            if (fileDoc.getFile().equals(this.getFile())) {
                continue;
            }
            File srcFile = file.getFile();
            
            String srcMd5;
            try {
                srcMd5 = FileUtil.getMD5Checksum(srcFile);
            } catch (IOException ex) {
                currRes.error = ex;
                continue;
            }
            
            File destFile = new File(getFilesDirectory(), file.getFile().getName());
            String index = "";
            String base = destFile.getName();
            
            if (base.matches("-(\\d+)$")) {
                index = base.substring(base.lastIndexOf("-")+1);
                base = base.substring(0, base.lastIndexOf("-"));
            }
            try {
                while (destFile.exists() && !srcMd5.equals(FileUtil.getMD5Checksum(destFile))) {
                    index = index.isEmpty() ? "2" : String.valueOf(Integer.parseInt(index)+1);
                    destFile = new File(getFilesDirectory(), base+index);
                }
                
                FNDocumentFile destDocFile = new FNDocumentFile(this, destFile);
                FileUtil.copy(srcFile, destFile);
                currRes.dest = destDocFile;
                
            } catch (Throwable t) {
                currRes.error = t;
            }
            
            res.add(currRes);
            
            
        }
        return res;
    }
    
}
