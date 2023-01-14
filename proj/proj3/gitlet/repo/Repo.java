package gitlet.repo;

import gitlet.Utils;
import gitlet.objects.CommitData;
import gitlet.objects.Stage;

import java.io.File;
import java.util.Queue;

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

    /**
     * Create current commit.
     */
    private static void createCurrCommit() {
        if (currCommit == null) {
            currCommit = objectFolder.getCommit(getCurrHeadUid());
        }
    }

    /**
     * Create current stage.
     */
    private static void createStage() {
        if (stage == null) {
            stage = Stage.readFromFile();
        }
    }

    /**
     * Create current branch.
     */
    private static void createBranch() {
        if (currBranch == null) {
            currBranch = Utils.readContentsAsString(HEAD_FILE);
        }
    }

    /**
     * Update all.
     */
    public static void update() {
        currCommit = null;
        currBranch = null;
        stage = null;
    }

    /**
     * Return current commit.
     */
    public static CommitData getCurrCommit() {
        createCurrCommit();
        return currCommit;
    }

    /**
     * Return current branch.
     */
    public static String getCurrBranch() {
        createBranch();
        return currBranch;
    }

    /**
     * Return stage.
     */
    public static Stage getStage() {
        createStage();
        return stage;
    }

    /**
     * Return the head UID of the current branch.
     */
    public static String getCurrHeadUid() {
        return branchFolder.getHeadUid(getCurrBranch());
    }

    /**
     * Change current commit to the given commit.
     */
    public static void setCurrCommit(CommitData commitData) {
        currCommit = commitData;
    }

    /**
     * Change the current branch to the given branch.
     */
    public static void setCurrBranch(String branchName) {
        Utils.writeContents(HEAD_FILE, branchName);
    }

    /**
     * Change the HEAD of current branch to UID.
     */
    public static void setCurrHeadUid(String uid) {
        createBranch();
        branchFolder.setHeadUid(currBranch, uid);
    }

    /**
     * Return true if current commit contains file FILENAME.
     */
    public static boolean currCommitContainsFile(String fileName) {
        createCurrCommit();
        return currCommit.containsFile(fileName);
    }

    /**
     * Return true if the file FILENAME in current working directory is the
     * same with the file in the given commit.
     */
    public static boolean currCommitSameFile(String fileName) {
        createCurrCommit();
        return currCommit.compareFile(fileName, workFolder.getUidOfFile(fileName));
    }

    /**
     * Return the history uid of current branch.
     */
    public static Queue<CommitData> getHistoryOfCurrCommit() {
        createCurrCommit();
        return objectFolder.getHistoryOfCommit(currCommit);
    }

    /**
     * Write current commit into log of current branch.
     */
    public static void writeLogToCurrBranch() {
        createCurrCommit();
        createBranch();
        logFolder.writeLogToBranch(currBranch, currCommit);
    }

    /**
     * Return false if the given file in current working directory is not the
     * same as the file in staging area.
     */
    public static boolean stageNotContainSameFile(String fileName) {
        createStage();
        return !stage.compareFile(fileName, workFolder.getUidOfFile(fileName));
    }

    /**
     * Work dir.
     */
    public static final WorkFolder workFolder = new WorkFolder();

    /**
     * Branch Dir.
     */
    public static final BranchFolder branchFolder = new BranchFolder();

    /**
     * Branch latest commit dir.
     */
    public static final BranchLatestFolder branchLatestFolder = new BranchLatestFolder();

    /**
     * Log Dir.
     */
    public static final LogFolder logFolder = new LogFolder();

    /**
     * Objects dir.
     */
    public static final ObjectFolder objectFolder = new ObjectFolder();

    /**
     * Current commit. Only use when needed.
     */
    private static CommitData currCommit;

    /**
     * Current branch. Only used when needed.
     */
    private static String currBranch;

    /**
     * Stage. Only used when needed.
     */
    private static Stage stage;

}
