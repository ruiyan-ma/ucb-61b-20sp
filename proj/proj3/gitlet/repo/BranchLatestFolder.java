package gitlet.repo;

import gitlet.objects.CommitData;

/**
 * This class represents the latest branch directory in BRANCH_LATEST_COMMIT.
 *
 * @author ryan ma
 */

public class BranchLatestFolder extends Folder {

    /**
     * Folder for all branches the latest commit UID files.
     * Note this is not branch's head.
     */
    public static final String FOLDER_NAME = ".gitlet/branchesLatest";

    public BranchLatestFolder() {
        super(FOLDER_NAME);
    }

    /**
     * Return the head uid of the given branch.
     */
    public String getHeadUid(String branchName) {
        return readFromFile(branchName);
    }

    /**
     * Write commit UID into the latest commit file of the given branch.
     */
    public void writeLatestCommit(String branchName, String uid) {
        writeToFile(branchName, uid);
    }

    /**
     * Return the latest commit of a branch.
     */
    public CommitData getLatestCommit(String branch) {
        String headUid = getHeadUid(branch);
        return Repo.objectFolder.getCommit(headUid);
    }

}
