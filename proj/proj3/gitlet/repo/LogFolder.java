package gitlet.repo;

import gitlet.objects.CommitData;

/**
 * This class represents the log folder.
 *
 * @author ryan ma
 */

public class LogFolder extends Folder {

    /**
     * Folder for storing the HEAD UID of each branch.
     */
    public static final String LOG_FOLDER = ".gitlet/logs/refs/heads";

    LogFolder() {
        super(LOG_FOLDER);
    }

    /**
     * Return String. Read log record of the given branch.
     */
    public String readLogOfBranch(String branchName) {
        return readFromFile(branchName);
    }

    /**
     * Write the given commit into log of the given branch.
     */
    public void writeLogToBranch(String branchName, CommitData commit) {
        String log = commit.getLog() + readLogOfBranch(branchName);
        writeToFile(branchName, log);
    }
}
