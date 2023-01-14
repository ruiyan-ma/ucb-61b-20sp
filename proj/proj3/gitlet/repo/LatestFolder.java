package gitlet.repo;

import java.util.List;

/**
 * This class represents the ".gitlet/latest" folder.
 * It records the latest commit UID of each branch.
 *
 * @author ryan ma
 */

public class LatestFolder extends Folder {

    /**
     * Folder for all branches the latest commit UID files.
     * Note this is not branch's head.
     */
    public static final String FOLDER_NAME = ".gitlet/latest";

    public LatestFolder() {
        super(FOLDER_NAME);
    }

    /**
     * Return all branches.
     */
    public List<String> getAllBranches() {
        return getAllFileName();
    }

    /**
     * Return the latest commit uid of the given branch.
     */
    public String getLatestUid(String branchName) {
        return readFromFile(branchName);
    }

    /**
     * Set the latest commit uid for the given branch.
     */
    public void setLatestUid(String branchName, String uid) {
        writeToFile(branchName, uid);
    }
}
