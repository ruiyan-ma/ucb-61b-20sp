package gitlet.commands;

import gitlet.Main;
import gitlet.repo.Repo;

import java.io.IOException;

/** This class is the branch command class.
 *  @author ryan ma
 *  */

public class Branch extends Command {

    /** Constructor function with ARGS. */
    public Branch(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        newBranchName = _operands[0];
    }

    @Override
    void checkOperands() {
        if (Repo.branchFolder.hasBranch(newBranchName)) {
            Main.exitWithError("A branch with that name already exists.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();

        Repo.branchFolder.addFile(newBranchName);
        Repo.branchFolder.setHeadUid(newBranchName, Repo.getCurrHeadUid());

        Repo.logFolder.addFile(newBranchName);
        Repo.logFolder.writeLogToBranch(newBranchName, Repo.getCurrCommit());

        Repo.branchLatestFolder.addFile(newBranchName);
        Repo.branchLatestFolder.writeLatestCommit(newBranchName, Repo.getCurrHeadUid());
    }

    /** Branch name. */
    private final String newBranchName;
}
