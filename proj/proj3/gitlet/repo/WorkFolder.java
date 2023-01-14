package gitlet.repo;

import gitlet.Utils;
import gitlet.objects.Bolb;
import gitlet.objects.CommitData;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for handling all operations with working
 * directory.
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
     * Return the uid of file FILENAME.
     */
    public String getUidOfFile(String fileName) {
        Bolb bolb = new Bolb(readFromFile(fileName));
        return bolb.getUID();
    }

    /**
     * Check out the given file in the given commit.
     */
    public void checkoutFileWithCommit(CommitData commit, String fileName) throws IOException {
        if (!checkExist(fileName)) {
            addFile(fileName);
        }
        Bolb bolb = Repo.objectFolder.getBolb(commit, fileName);
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
                if (!Repo.currCommitContainsFile(fileName)) {
                    untracked = true;
                    break;
                } else if (!Repo.currCommitSameFile(fileName)) {
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
        Set<String> filesToBeDeleted = Repo.getCurrCommit().getAllFileName();
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
