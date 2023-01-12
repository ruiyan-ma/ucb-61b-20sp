package gitlet;

import java.io.File;
import java.util.List;
import java.util.Queue;

/** This class represents the branch directory in BRANCH_HEAD_FOLDER.
 *  @author ryan ma */

class BranchDir extends Dir {

    /** Folder for storing the HEAD UID of each branch. */
    static final File BRANCH_HEAD_FOLDER = new File(".gitlet/refs/heads");

    @Override
    File getFolder() {
        return BRANCH_HEAD_FOLDER;
    }

    @Override
    protected void deleteFile(String fileName) {
        getPathOfFile(fileName).delete();
    }

    /** Return a list of all branches. */
    List<String> getAllBranches() {
        return getAllFiles();
    }

    /** Return true if this repository contains branch BRANCH. */
    boolean containsBranch(String branch) {
        return checkExist(branch);
    }

    /** Delete the branch BRANCHNAME. */
    void deleteBranch(String branchName) {
        deleteFile(branchName);
    }

    /** Return the HEAD UID of branch BRANCHNAME. */
    String getHeadOfBranch(String branchName) {
        return readFromFile(branchName);
    }

    /** Return commit of branch BRANCHNAME. */
    CommitData getCommitOfBranch(String branchName) {
        return Repo.getObjectDir().getCommit(getHeadOfBranch(branchName));
    }

    /** Change the HEAD UID of branch BRANCHNAME. */
    void changeBranchHead(String branchName, String uid) {
        writeToFile(branchName, uid);
    }

    /** Return the history uids of branch BRANCHNAME. */
    Queue<CommitData> getHistoryOfBranch(String branchName) {
        return Repo.getObjectDir().getHistoryOfCommit(
                   getHeadOfBranch(branchName));
    }

}
