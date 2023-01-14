package gitlet.repo;

import gitlet.objects.CommitData;

import java.util.List;
import java.util.Queue;

/**
 * This class represents the branch directory in BRANCH_HEAD_FOLDER.
 *
 * @author ryan ma
 */

public class BranchFolder extends Folder {

    /**
     * Folder for storing the HEAD UID of each branch.
     */
    public static final String FOLDER_NAME = ".gitlet/refs/heads";

    public BranchFolder() {
        super(FOLDER_NAME);
    }

    /**
     * Return a list of all branches.
     */
    public List<String> getAllBranches() {
        return getAllFileName();
    }

    /**
     * Return true if this repository contains the given branch.
     */
    public boolean hasBranch(String branch) {
        return checkExist(branch);
    }

    /**
     * Return the HEAD UID of the branch.
     */
    public String getHeadUid(String branchName) {
        return readFromFile(branchName);
    }

    /**
     * Change the HEAD UID of the branch.
     */
    public void setHeadUid(String branchName, String uid) {
        writeToFile(branchName, uid);
    }

    /**
     * Delete the branch with the given name.
     */
    public void deleteBranch(String branchName) {
        deleteFile(branchName);
    }

    /**
     * Return the history commit of the branch .
     */
    public Queue<CommitData> getHistoryOfBranch(String branchName) {
        String uid = getHeadUid(branchName);
        CommitData commit = Repo.objectFolder.getCommit(uid);
        return Repo.objectFolder.getHistoryOfCommit(commit);
    }
}
