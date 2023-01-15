package gitlet.commands;

import gitlet.Main;
import gitlet.Utils;
import gitlet.objects.CommitData;
import gitlet.objects.Stage;

import java.io.File;
import java.io.IOException;

import static gitlet.Main.repo;

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
        if (repo.GITLET_FOLDER.exists()) {
            Main.exitWithError("A Gitlet version-control system already exists "
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
        repo.objectFolder.save(root);

        repo.setCurrBranch("master");
        repo.branchFolder.setHeadUid("master", rootUID);
        repo.latestFolder.setLatestUid("master", rootUID);
        repo.logFolder.writeLogToBranch("master", root);
    }

    /**
     * Create branch master.
     */
    private void createMaster() throws IOException {
        repo.branchFolder.addFile("master");
        repo.logFolder.addFile("master");
        repo.latestFolder.addFile("master");
    }

    /**
     * Create all.
     */
    private void createAll() throws IOException {
        repo.GITLET_FOLDER.mkdir();
        repo.HEAD_FILE.createNewFile();
        createIndexFile();
        createRefsFolder();
        createObjectsFolder();
        createLogFolder();
        repo.latestFolder.folder.mkdir();
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
        repo.objectFolder.folder.mkdir();
        repo.objectFolder.addFolder("info");
        repo.objectFolder.addFolder("pack");
    }

    /**
     * Create refs folder.
     */
    private void createRefsFolder() {
        File refs = Utils.join(repo.GITLET_FOLDER, "refs");
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
        File logs = Utils.join(repo.GITLET_FOLDER, "logs");
        logs.mkdir();
        File refs = Utils.join(logs, "refs");
        refs.mkdir();
        File heads = Utils.join(refs, "heads");
        heads.mkdir();
        File remotes = Utils.join(refs, "remotes");
        remotes.mkdir();
    }
}
