package gitlet.commands;

import gitlet.objects.CommitData;
import gitlet.Main;

import java.io.IOException;

import static gitlet.Main.repo;

/** This class is the reset command class.
 *  @author ryan ma
 *  */

public class Reset extends Command {

    /** Constructor function with ARGS. */
    public Reset(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        commitId = operands[0];
    }

    @Override
    void checkOperands() {
        if (!repo.objectFolder.containsCommit(commitId)) {
            Main.exitWithError("No commit with that id exists.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();

        CommitData commit = repo.objectFolder.getCommit(commitId);
        assert commit != null;
        if (repo.workFolder.canNotCheckoutAllFiles(commit)) {
            Main.exitWithError("There is an untracked file in the "
                               + "way; delete it, or add and commit it first.");
        }

        repo.workFolder.checkoutAllFilesWithCommit(commit);
        repo.setCurrHeadUid(commit.getUID());
        repo.getStage().clean();
        repo.getStage().save();
    }

    /** Commit id. */
    private final String commitId;

}
