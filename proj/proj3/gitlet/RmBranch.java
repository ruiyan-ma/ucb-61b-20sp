package gitlet;

/** This class is the rm branch command class.
 *  @author ryan ma
 *  */

class RmBranch extends Command {

    /** Constructor function with ARGS. */
    RmBranch(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _delBranch = _operands[0];
    }

    @Override
    void checkOperands() {
        if (!Repo.getBranchDir().containsBranch(_delBranch)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (Repo.getCurrBranch().equals(_delBranch)) {
            Main.exitWithError("Cannot remove the current branch.");
        }
    }

    @Override
    void run() {
        checkOperands();
        Repo.getBranchDir().deleteBranch(_delBranch);
    }

    /** Branch name. */
    private final String _delBranch;

}
