package gitlet.repo;

import gitlet.Utils;
import gitlet.objects.Bolb;
import gitlet.objects.CommitData;
import gitlet.objects.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static gitlet.Main.repo;

/**
 * This class is responsible for handling all operations in the working directory.
 *
 * @author ryan ma
 */

public class WorkFolder extends Folder {

    /**
     * Current working directory.
     */
    static final String CWD = ".";

    WorkFolder() {
        super(CWD);
    }

    /**
     * Return the uid of a file.
     */
    public String getUidOfFile(String fileName) {
        return Utils.sha1(readFromFile(fileName));
    }

    /**
     * Compare the content of a file with a given commit.
     */
    public boolean compareFile(String fileName, CommitData commit) {
        return getUidOfFile(fileName).equals(commit.getBolbUid(fileName));
    }

    /**
     * Compare the content of a file with a given stage.
     */
    public boolean compareFile(String fileName, Stage stage) {
        return getUidOfFile(fileName).equals(stage.getBolbUid(fileName));
    }

    /**
     * Check out the given file in the given commit.
     */
    public void checkoutFileWithCommit(CommitData commit, String fileName) throws IOException {
        if (!checkExist(fileName)) {
            addFile(fileName);
        }
        Bolb bolb = repo.objectFolder.getBolb(commit, fileName);
        writeToFile(fileName, bolb.getContent());
    }

    /**
     * Return the condition for check out all files in working directory to the given commit.
     */
    public boolean canNotCheckoutAllFiles(CommitData commit) {
        boolean untracked = false, changed = false;
        List<String> workingFiles = getAllFileName();

        for (String fileName : workingFiles) {
            if (commit.containsFile(fileName)) {
                if (!repo.getCurrCommit().containsFile(fileName)) {
                    untracked = true;
                    break;
                } else if (!compareFile(fileName, repo.getCurrCommit())) {
                    changed = true;
                    break;
                }
            }
        }

        return untracked || changed;
    }

    /**
     * Check out all files in working directory to commit.
     */
    public void checkoutAllFilesWithCommit(CommitData commit) throws IOException {
        Set<String> filesInCommit = commit.getAllFileName();
        Set<String> filesToBeDeleted = repo.getCurrCommit().getAllFileName();
        filesToBeDeleted.removeAll(filesInCommit);

        for (String fileName : filesInCommit) {
            checkoutFileWithCommit(commit, fileName);
        }

        for (String delFile : filesToBeDeleted) {
            deleteFile(delFile);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (checkExist(fileName)) {
            Utils.restrictedDelete(getFile(fileName));
        }
    }
}
