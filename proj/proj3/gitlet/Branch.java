package gitlet;

import java.io.IOException;

/** This class is the branch command class.
 *  @author ryan ma
 *  */

class Branch extends Command {

    /** Constructor function with ARGS. */
    Branch(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _newBranch = _operands[0];
    }

    @Override
    void checkOperands() {
        if (Repo.getBranchDir().containsBranch(_newBranch)) {
            Main.exitWithError("A branch with that name already exists.");
        }
    }

    @Override
    void run() throws IOException {
        checkOperands();

        Repo.getBranchDir().getPathOfFile(_newBranch).createNewFile();
        Repo.getBranchDir().changeBranchHead(_newBranch, Repo.getCurrHead());

        Repo.getLogDir().getPathOfFile(_newBranch).createNewFile();
        Repo.getLogDir().writeLogToBranch(Repo.getCurrCommit(), _newBranch);

        Repo.getBranchLatestDir().getPathOfFile(_newBranch).createNewFile();
        Repo.getBranchLatestDir().writeLatestCommit(_newBranch,
                Repo.getCurrHead());

    }

    /** Branch name. */
    private final String _newBranch;

}
