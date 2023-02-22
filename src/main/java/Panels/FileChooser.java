package Panels;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A custom file chooser that adds functionality such as asking if the user wants to overwrite an existing file when saving one
 * and automatically adding an extension when creating a file
 * This class and methods are adapted from <a href="https://geek.starbean.net/2009/11/15/custom-jfilechooser/">https://geek.starbean.net/2009/11/15/custom-jfilechooser/</a>
 */
public class FileChooser extends JFileChooser {

    private final String[] extensions;

    public FileChooser(String[] extensions) {
        this.extensions = extensions;
    }

    /**
     * Gets the extension that should be used according to the currently selected filter
     * @return the extension that should be used according to the currently selected filter
     */
    private String getCurrentExtension() {
        FileFilter filter = getFileFilter();
        for(String extension : extensions) {
            if(filter.accept(new File("a." + extension)))
                return extension;
        }
        return null;
    }

    /**
     * Returns the currently selected file, if the file has no extension one is added based off the currently selected filter
     * @return the currently selected file, if the file has no extension one is added based off the currently selected filter
     */
    @Override
    public File getSelectedFile() {
        File selectedFile = super.getSelectedFile();
        if(isAcceptAllFileFilterUsed() || selectedFile == null)
            return selectedFile;
        String extension = getCurrentExtension();
        String name = selectedFile.getName();
        if(!name.contains(".") || !getFileFilter().accept(selectedFile))
            return new File(selectedFile.getParentFile(), name + "." + extension);
        return selectedFile;
    }

    @Override
    public void approveSelection() {
        if(getDialogType() == SAVE_DIALOG) {
            File selectedFile = getSelectedFile();
            if(selectedFile != null && selectedFile.exists()) {
                int response = JOptionPane.showConfirmDialog(this, selectedFile.getName() + " already exists.\nDo you want to replace it?", "Confirm Save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if(response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }
        super.approveSelection();
    }
}
