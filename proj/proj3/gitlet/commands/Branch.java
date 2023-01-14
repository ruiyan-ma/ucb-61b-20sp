package gitlet.commands;

import gitlet.Main;

import java.io.IOException;

import static gitlet.Main.repo;

/**
 * This class is the branch command class.
 *
 * @author ryan ma
 */

public class Branch extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Branch(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        newBranchName = operands[0];
    }

    @Override
    void checkOperands() {
        if (repo.branchFolder.hasBranch(newBranchName)) {
            Main.exitWithError("A branch with that name already exists.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();

        // add new branch and set its head uid.
        repo.branchFolder.addFile(newBranchName);
        repo.branchFolder.setHeadUid(newBranchName, repo.getCurrHeadUid());

        // add new branch and set its latest commit uid.
        repo.latestFolder.addFile(newBranchName);
        repo.latestFolder.setLatestUid(newBranchName, repo.getCurrHeadUid());

        repo.logFolder.addFile(newBranchName);
        repo.logFolder.writeLogToBranch(newBranchName, repo.getCurrCommit());
    }

    /**
     * Branch name.
     */
    private final String newBranchName;
}
