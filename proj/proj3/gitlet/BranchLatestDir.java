package gitlet;

import java.io.File;

/** This class represents the branch latest directory in BRANCH_LATEST_COMMIT.
 *  @author ryan ma */

class BranchLatestDir extends Dir {

    /** Folder for all branches latest commit UID files.
     *  Note this is not branch's head. */
    static final File BRANCH_LATEST_COMMIT = new File(".gitlet/branchesLatest");

    @Override
    File getFolder() {
        return BRANCH_LATEST_COMMIT;
    }

    /** Return the latest commit of branch BRANCH. */
    CommitData getLatestCommit(String branch) {
        return Repo.getObjectDir().getCommit(readFromFile(branch));
    }

    /** Write commit UID into latest commit file of branch BRANCHNAME. */
    void writeLatestCommit(String branchName, String uid) {
        writeToFile(branchName, uid);
    }

    @Override
    protected void deleteFile(String fileName) {
        getPathOfFile(fileName).delete();
    }

}
