package gitlet;

import java.io.File;

/** This class represents the log directory in LOG_FOLDER.
 *  @author ryan ma */

class LogDir extends Dir {

    /** Folder for storing the HEAD UID of each branch. */
    static final File LOG_FOLDER = new File(".gitlet/logs/refs/heads");

    @Override
    File getFolder() {
        return LOG_FOLDER;
    }

    /** Return String. Read log record of branch BRANCHNAME. */
    String readLogOfBranch(String branchName) {
        return readFromFile(branchName);
    }

    /** Write commit COMMIT into log of branch BRANCHNAME. */
    void writeLogToBranch(CommitData commit, String branchName) {
        String log = commit.getLog() + readLogOfBranch(branchName);
        writeToFile(branchName, log);
    }

}
