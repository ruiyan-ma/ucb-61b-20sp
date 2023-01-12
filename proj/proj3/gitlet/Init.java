package gitlet;

import java.io.File;
import java.io.IOException;

/**
 * This class is the init command class.
 * This command is responsible for creating all necessary folders
 * and files, creating an root commit, create an empty stage,
 * and creating master branch.
 *
 * @author ryan ma
 */

class Init extends Command {

	/**
	 * Constructor function with ARGS.
	 */
	Init(String[] args) {
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
	void run() throws IOException {
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
		root.save();
		Repo.getBranchDir().changeBranchHead("master", rootUID);
		Repo.changeCurrBranch("master");
		Repo.getLogDir().writeLogToBranch(root, "master");
		Repo.getBranchLatestDir().writeLatestCommit("master", rootUID);
	}

	/**
	 * Create branch master.
	 */
	private void createMaster() throws IOException {
		File master = Utils.join(BranchDir.BRANCH_HEAD_FOLDER, "master");
		master.createNewFile();
		File masterLog = Utils.join(LogDir.LOG_FOLDER, "master");
		masterLog.createNewFile();
		File masterLatest = Utils.join(BranchLatestDir.BRANCH_LATEST_COMMIT,
				"master");
		masterLatest.createNewFile();
	}

	/**
	 * Create all.
	 */
	private void createAll() throws IOException {
		createGitletFolder();
		createHeadFile();
		createIndexFile();
		createRefsFolder();
		createObjectsFolder();
		createLogFolder();
		createBranchLatestFolder();
	}

	/**
	 * Create gitlet folder.
	 */
	private void createGitletFolder() {
		Repo.GITLET_FOLDER.mkdir();
	}

	/**
	 * Create HEAD file.
	 */
	private void createHeadFile() throws IOException {
		Repo.HEAD_FILE.createNewFile();
	}

	/**
	 * Create branches folder.
	 */
	private void createBranchLatestFolder() {
		BranchLatestDir.BRANCH_LATEST_COMMIT.mkdir();
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
		ObjectDir.OBJECTS_FOLDER.mkdir();
		File info = Utils.join(ObjectDir.OBJECTS_FOLDER, "info");
		info.mkdir();
		File pack = Utils.join(ObjectDir.OBJECTS_FOLDER, "pack");
		pack.mkdir();
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
