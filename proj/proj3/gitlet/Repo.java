package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.TreeMap;

/** This class represents the repository in gitlet.
 *  This class provides methods for reading and writing things from repository.
 *  This class implements lazy policy.
 *  @author ryan ma */

class Repo {

    /** The main gitlet folder. */
    static final File GITLET_FOLDER = new File(".gitlet");

    /** File for HEAD. */
    static final File HEAD_FILE = new File(".gitlet/HEAD");

    /** Create current commit. */
    private static void createCommit() {
        if (currCommit == null) {
            currCommit = objectDir.getCommit(getCurrHead());
        }
    }

    /** Create current stage. */
    private static void createStage() {
        if (stage == null) {
            stage = Stage.readFromFile();
        }
    }

    /** Create current branch. */
    private static void createBranch() {
        if (currBranch == null) {
            currBranch = Utils.readContentsAsString(HEAD_FILE);
        }
    }

    /** Update all. */
    static void update() {
        currCommit = null;
        currBranch = null;
        stage = null;
    }

    /** Check out file FILENAME in current commit. */
    static void checkoutFileInCurrCommit(String fileName) throws IOException {
        workDir.checkoutFileInCommit(currCommit, fileName);
    }

    /** Return current commit. */
    static CommitData getCurrCommit() {
        createCommit();
        return currCommit;
    }

    /** Return stage. */
    static Stage getStage() {
        createStage();
        return stage;
    }

    /** Return current branch. */
    static String getCurrBranch() {
        createBranch();
        return currBranch;
    }

    /** Change current commit to COMMITDATA. */
    static void changeCurrCommit(CommitData commitData) {
        currCommit = commitData;
    }

    /** Return workDir. */
    static WorkDir getWorkDir() {
        return workDir;
    }

    /** Return branchDir. */
    static BranchDir getBranchDir() {
        return branchDir;
    }

    /** Return logDir. */
    static LogDir getLogDir() {
        return logDir;
    }

    /** Return branchLatestDir. */
    static BranchLatestDir getBranchLatestDir() {
        return branchLatestDir;
    }

    /** Return objectDir. */
    static ObjectDir getObjectDir() {
        return objectDir;
    }



    /** Change the current branch to BRANCHNAME. */
    static void changeCurrBranch(String branchName) {
        head.writeToFile(branchName);
    }

    /** Return the HEAD UID of the current branch. */
    static String getCurrHead() {
        return branchDir.getHeadOfBranch(getCurrBranch());
    }

    /** Change the HEAD of current branch to UID. */
    static void changeCurrBranchHead(String uid) {
        createBranch();
        branchDir.changeBranchHead(currBranch, uid);
    }

    /** Return true if current commit contains file FILENAME. */
    static boolean currCommitContainsFile(String fileName) {
        createCommit();
        return currCommit.containFile(fileName);
    }

    /** Return true if the file FILENAME in current working directory is the
     *  same with the file in commit COMMIT. */
    static boolean currCommitSameFile(String fileName) {
        createCommit();
        return currCommit.compareFile(fileName, workDir.getUidOfFile(fileName));
    }

    /** Return the map of current commit. */
    static TreeMap<String, String> getCurrCommitMap() {
        createCommit();
        return currCommit.getMap();
    }

    /** Return the history uid of current branch. */
    static Queue<CommitData> getHistoryOfCurrBranch() {
        createCommit();
        return objectDir.getHistoryOfCommit(currCommit);
    }

    /** Write commit COMMIT into log of current branch. */
    static void writeLogToCurrBranch() {
        createCommit();
        createBranch();
        logDir.writeLogToBranch(currCommit, currBranch);
    }

    /** Return true if the file FILENAME in current working directory is the
     *  same with the file in staging area. */
    static boolean stageNotContainSameFile(String fileName) {
        createStage();
        return !stage.compareFile(fileName, workDir.getUidOfFile(fileName));
    }

    /** Work dir. */
    private static final WorkDir workDir = new WorkDir();

    /** Head file. */
    private static final GitletFile head = new GitletFile(HEAD_FILE);

    /** Branch Dir. */
    private static final BranchDir branchDir = new BranchDir();

    /** Log Dir. */
    private static final LogDir logDir = new LogDir();

    /** Branch latest commit dir. */
    private static final BranchLatestDir branchLatestDir = new BranchLatestDir();

    /** Objects dir. */
    private static final ObjectDir objectDir = new ObjectDir();

    /** current commit. Only use when needed. */
    private static CommitData currCommit;

    /** current branch. Only used when needed. */
    private static String currBranch;

    /** Stage. Only used when needed. */
    private static Stage stage;

}
