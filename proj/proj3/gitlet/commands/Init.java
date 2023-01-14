package gitlet.commands;

import gitlet.Main;
import gitlet.Utils;
import gitlet.objects.CommitData;
import gitlet.objects.Stage;
import gitlet.repo.Repo;

import java.io.File;
import java.io.IOException;

/**
 * This class is the init command class.
 * This command is responsible for creating all necessary folders
 * and files, creating a root commit, create an empty stage,
 * and creating master branch.
 *
 * @author ryan ma
 */

public class Init extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Init(String[] args) {
        super(args, 0);
        checkOperandsNum();
    }

    @Override
    void checkOperands() {
        if (Repo.GITLET_FOLDER.exists()) {
            Main.exitWithError("A Gitlet version-control "
                    + "system already exists "
                    + "in the current directory.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();
        createAll();
        createMaster();
        createRoot();
    }

    /**
     * Create root commit and return its UID.
     */
    private void createRoot() throws IOException {
        CommitData root = new CommitData("initial commit");
        String rootUID = root.getUID();
        Repo.objectFolder.save(root);
        Repo.branchFolder.setHeadUid("master", rootUID);
        Repo.setCurrBranch("master");
        Repo.logFolder.writeLogToBranch(root, "master");
        Repo.branchLatestFolder.writeLatestCommit("master", rootUID);
    }

    /**
     * Create branch master.
     */
    private void createMaster() throws IOException {
        Repo.branchFolder.addFile("master");
        Repo.logFolder.addFile("master");
        Repo.branchLatestFolder.addFile("master");
    }

    /**
     * Create all.
     */
    private void createAll() throws IOException {
        Repo.GITLET_FOLDER.mkdir();
        Repo.HEAD_FILE.createNewFile();
        createIndexFile();
        createRefsFolder();
        createObjectsFolder();
        createLogFolder();
        Repo.branchLatestFolder.folder.mkdir();
    }

    /**
     * Create index file.
     */
    private void createIndexFile() throws IOException {
        Stage.INDEX_FILE.createNewFile();
        Stage stage = new Stage();
        stage.save();
    }

    /**
     * Create objects folder.
     */
    private void createObjectsFolder() {
        Repo.objectFolder.folder.mkdir();
        Repo.objectFolder.addFolder("info");
        Repo.objectFolder.addFolder("pack");
    }

    /**
     * Create refs folder.
     */
    private void createRefsFolder() {
        File refs = Utils.join(Repo.GITLET_FOLDER, "refs");
        refs.mkdir();
        File heads = Utils.join(refs, "heads");
        heads.mkdir();
        File remotes = Utils.join(refs, "remotes");
        remotes.mkdir();
    }

    /**
     * Create log folder.
     */
    private void createLogFolder() {
        File logs = Utils.join(Repo.GITLET_FOLDER, "logs");
        logs.mkdir();
        File refs = Utils.join(logs, "refs");
        refs.mkdir();
        File heads = Utils.join(refs, "heads");
        heads.mkdir();
        File remotes = Utils.join(refs, "remotes");
        remotes.mkdir();
    }
}
