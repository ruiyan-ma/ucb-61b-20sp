package gitlet;

import java.io.IOException;

/** This class is the reset command class.
 *  @author ryan ma
 *  */

class Reset extends Command {

    /** Constructor function with ARGS. */
    Reset(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _id = _operands[0];
    }

    @Override
    void checkOperands() {
        if (!Repo.getObjectDir().containsCommit(_id)) {
            Main.exitWithError("No commit with that id exists.");
        }
    }

    @Override
    void run() throws IOException {
        checkOperands();
        CommitData commit = Repo.getObjectDir().getCommit(_id);
        if (Repo.getWorkDir().conditionForCheckoutAllFiles(commit)) {
            Main.exitWithError("There is an untracked file in the "
                               + "way; delete it, or add and commit it first.");
        }
        assert commit != null;
        Repo.getWorkDir().checkoutAllFilesWithCommit(commit);
        Repo.getBranchDir().changeBranchHead(Repo.getCurrBranch(),
                                             commit.getUID());
        Repo.getStage().clean();
        Repo.getStage().save();
    }

    /** Commit id. */
    private final String _id;

}
