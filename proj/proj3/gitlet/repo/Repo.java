package gitlet.repo;

import gitlet.Utils;
import gitlet.objects.CommitData;
import gitlet.objects.Stage;

import java.io.File;

/**
 * This class represents the repository in gitlet.
 * This class provides methods for reading and writing things from repository.
 * This class implements lazy policy.
 *
 * @author ryan ma
 */

public class Repo {

    /**
     * The main gitlet folder.
     */
    public static final File GITLET_FOLDER = new File(".gitlet");

    /**
     * File for HEAD.
     */
    public static final File HEAD_FILE = new File(".gitlet/HEAD");

    public Repo() {
        workFolder = new WorkFolder();
        objectFolder = new ObjectFolder();
        branchFolder = new BranchFolder();
        latestFolder = new LatestFolder();
        logFolder = new LogFolder();
    }

    /**
     * Update the current branch, stage and the current commit.
     */
    public void update() {
        currBranch = Utils.readContentsAsString(HEAD_FILE);
        stage = Stage.readFromFile();
        currCommit = objectFolder.getCommit(getCurrHeadUid());
    }

    public CommitData getCurrCommit() {
        if (currCommit == null) {
            currCommit = objectFolder.getCommit(getCurrHeadUid());
        }

        return currCommit;
    }

    public String getCurrBranch() {
        if (currBranch == null) {
            currBranch = Utils.readContentsAsString(HEAD_FILE);
        }

        return currBranch;
    }

    public Stage getStage() {
        if (stage == null) {
            stage = Stage.readFromFile();
        }
        return stage;
    }

    public void setCurrCommit(CommitData commitData) {
        currCommit = commitData;
    }

    public void setCurrBranch(String branchName) {
        currBranch = branchName;
        Utils.writeContents(HEAD_FILE, branchName);
    }

    /**
     * Get the head uid of the current branch.
     */
    public String getCurrHeadUid() {
        return branchFolder.getHeadUid(getCurrBranch());
    }


    /**
     * Set the head uid for the current branch.
     */
    public void setCurrHeadUid(String uid) {
        branchFolder.setHeadUid(getCurrBranch(), uid);
    }

    public final WorkFolder workFolder;

    public final BranchFolder branchFolder;

    public final LatestFolder latestFolder;

    public final LogFolder logFolder;

    public final ObjectFolder objectFolder;

    private CommitData currCommit;

    private String currBranch;

    private Stage stage;
}
