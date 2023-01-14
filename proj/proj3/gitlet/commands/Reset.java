package gitlet.commands;

import gitlet.objects.CommitData;
import gitlet.Main;
import gitlet.repo.Repo;

import java.io.IOException;

/** This class is the reset command class.
 *  @author ryan ma
 *  */

public class Reset extends Command {

    /** Constructor function with ARGS. */
    public Reset(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _id = _operands[0];
    }

    @Override
    void checkOperands() {
        if (!Repo.objectFolder.containsCommit(_id)) {
            Main.exitWithError("No commit with that id exists.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();
        CommitData commit = Repo.objectFolder.getCommit(_id);
        if (Repo.workFolder.canNotCheckoutAllFiles(commit)) {
            Main.exitWithError("There is an untracked file in the "
                               + "way; delete it, or add and commit it first.");
        }
        assert commit != null;
        Repo.workFolder.checkoutAllFilesWithCommit(commit);
        Repo.branchFolder.setHeadUid(Repo.getCurrBranch(),
                                             commit.getUID());
        Repo.getStage().clean();
        Repo.getStage().save();
    }

    /** Commit id. */
    private final String _id;

}
