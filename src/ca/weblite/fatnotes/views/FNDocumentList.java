/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.fatnotes.views;

import ca.weblite.fatnotes.models.FNContext;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 *
 * @author shannah
 */
public class FNDocumentList extends JXTreeTable {
    
    protected void documentNameChanged(File document, int column, String newName) throws Exception {
        
    }
    
    public FNDocumentList(FNContext context) {
        FileSystemModel model = new FileSystemModel() {
            final File documentsDir = context.getFNDocumentsDir();
            @Override
            public boolean isLeaf(Object node) {
                File f= (File)node;
                if (documentsDir.equals(f.getParentFile())) {
                    return true;
                }
                return false;
            }

            @Override
            public Object getValueAt(Object node, int column) {
                if (column == 1) {
                    File f = (File)node;
                    File index = new File(f, "index.html");
                    Date dt = new Date(index.lastModified());
                    return dt;
                }
                return super.getValueAt(node, column);
            }

            @Override
            public boolean isCellEditable(Object node, int column) {
                return column == 0;
            }

            @Override
            public void setValueAt(Object value, Object node, int column) {
                if (column == 0) {
                    try {
                        documentNameChanged((File)node, column, (String)value);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                }
                super.setValueAt(value, node, column);
            }
            
            
            
            
            
        };
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int[] rows = getSelectedRows();
                    for (int i : rows) {
                        Object object= getValueAt(i, 0);
                        TreePath path= getPathForRow(i);
                        Object o= path.getLastPathComponent();
                        File rowFile = (File)o;
                        openFile(rowFile);

                    }
                }
                super.mouseClicked(e);
            }
            
        });
        model.setRoot(context.getFNDocumentsDir());
        
        setTreeTableModel(model);
        int numColumns = getColumnCount();
        List<TableColumn> toRemove = new ArrayList<>();
        for (int i=0; i<numColumns; i++) {
            TableColumn col = getColumn(i);
            if (i == 1 || i == 2) {
                toRemove.add(col);
            }
        }
        for (TableColumn col : toRemove) {
            removeColumn(col);
        }
        
        
       
    }
    protected void openFile(File file) {
            
    }
}
