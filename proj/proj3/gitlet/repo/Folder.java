package gitlet.repo;

import gitlet.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class is the parent class for any folder.
 *
 * @author ryan ma
 */

public class Folder {

    /**
     * Constructor for all folders.
     */
    Folder(String folderName) {
        folder = new File(folderName);
    }

    /**
     * Return a list of all files name in this Dir.
     */
    public List<String> getAllFileName() {
        return Utils.plainFilenamesIn(folder);
    }

    /**
     * Return the absolute path of the given file.
     */
    public File getFile(String fileName) {
        return Utils.join(folder, fileName);
    }

    /**
     * Return true if the file exists in this Dir.
     */
    public boolean checkExist(String fileName) {
        return getFile(fileName).exists();
    }

    /**
     * Return String read from this file.
     */
    public String readFromFile(String fileName) {
        return Utils.readContentsAsString(getFile(fileName));
    }

    /**
     * Write content to file.
     */
    public void writeToFile(String fileName, String content) {
        Utils.writeContents(getFile(fileName), content);
    }

    /**
     * Add the file to the current folder.
     */
    public void addFile(String fileName) throws IOException {
        assert !getAllFileName().contains(fileName);
        getFile(fileName).createNewFile();
    }

    /**
     * Delete the file.
     */
    public void deleteFile(String fileName) {
        getFile(fileName).delete();
    }

    /**
     * Add a sub-folder to this directory.
     *
     * @param folderName: the sub-folder name.
     */
    public void addFolder(String folderName) {
        getFile(folderName).mkdir();
    }

    /**
     * The folder file for this directory.
     */
    public final File folder;
}
