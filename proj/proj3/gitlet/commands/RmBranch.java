package gitlet.commands;

import gitlet.Main;
import gitlet.repo.Repo;

/** This class is the rm branch command class.
 *  @author ryan ma
 *  */

public class RmBranch extends Command {

    /** Constructor function with ARGS. */
    public RmBranch(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        delBranch = _operands[0];
    }

    @Override
    void checkOperands() {
        if (!Repo.branchFolder.hasBranch(delBranch)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (Repo.getCurrBranch().equals(delBranch)) {
            Main.exitWithError("Cannot remove the current branch.");
        }
    }

    @Override
    public void run() {
        checkOperands();
        Repo.branchFolder.deleteBranch(delBranch);
    }

    /** Branch name. */
    private final String delBranch;

}
