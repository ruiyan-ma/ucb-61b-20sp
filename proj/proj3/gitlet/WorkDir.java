package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/** This class is responsible for handling all operations with working
 *  directory.
 *  @author ryan ma
 *  */

class WorkDir extends Dir {

    /** Current working directory. */
    static final File CWD = new File(".");

    @Override
    File getFolder() {
        return CWD;
    }

    /** Return the uid of file FILENAME. */
    String getUidOfFile(String fileName) {
        return Utils.sha1(readFromFile(fileName));
    }

    /** Check out file FILENAME in commit COMMIT. */
    void checkoutFileInCommit(CommitData commit,
                              String fileName) throws IOException {
        if (!checkExist(fileName)) {
            addFile(fileName);
        }
        Bolb bolb = Repo.getObjectDir().getBolbInCommit(commit, fileName);
        writeToFile(fileName, bolb.getContent());
    }

    /** Return the condition for check out all files in working directory to
     *  commit COMMIT. */
    boolean conditionForCheckoutAllFiles(CommitData commit) {
        boolean untrackedAndRewrite = false;
        List<String> workingFiles = getAllFiles();
        for (String file : workingFiles) {
            if (commit.containFile(file)) {
                if (!Repo.currCommitContainsFile(file)) {
                    untrackedAndRewrite = true;
                    break;
                } else if (
                    !Repo.currCommitSameFile(file)) {
                    untrackedAndRewrite = true;
                    break;
                }
            }
        }
        return untrackedAndRewrite;
    }

    /** Check out all files in working directory to commit COMMIT. */
    void checkoutAllFilesWithCommit(CommitData
                                    commit) throws IOException {
        Set<String> filesInCommit = commit.getAllFiles();
        Set<String> filesToBeDeleted = Repo.getCurrCommit().getAllFiles();
        for (String file : filesInCommit) {
            checkoutFileInCommit(commit, file);
            filesToBeDeleted.remove(file);
        }
        for (String delFile : filesToBeDeleted) {
            File workingFile = getPathOfFile(delFile);
            if (workingFile.exists()) {
                deleteFile(delFile);
            }
        }
    }

}
