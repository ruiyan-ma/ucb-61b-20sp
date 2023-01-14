package gitlet.commands;

import gitlet.objects.CommitData;
import gitlet.Main;
import gitlet.repo.Repo;

import java.io.IOException;

/** This class is the commit command class.
 *  @author ryan ma
 *  */

public class Commit extends Command {

    /** Constructor function with ARGS. */
    public Commit(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        message = _operands[0];
        secParent = null;
    }

    /** Constructor for merge command, with ARGS and SECPARENT. */
    Commit(String[] args, String secParent) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        message = _operands[0];
        this.secParent = secParent;
    }

    @Override
    void checkOperands() {
        if (Repo.getStage().additionMap.isEmpty() && Repo.getStage().removalSet.isEmpty()) {
            Main.exitWithError("No changes added to the commit.");
        }
        if (message.equals("")) {
            Main.exitWithError("Please enter a commit message.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();

        CommitData commitData;
        if (secParent != null) {
            commitData = new CommitData(message, Repo.getCurrHeadUid(), secParent, Repo.getStage());
        } else {
            commitData = new CommitData(message, Repo.getCurrHeadUid(), Repo.getStage());
        }

        Repo.setCurrCommit(commitData);
        String uid = commitData.getUID();
        Repo.setCurrHeadUid(uid);
        Repo.branchLatestFolder.writeLatestCommit(Repo.getCurrBranch(), uid);
        Repo.writeLogToCurrBranch();
        Repo.getStage().clean();
        Repo.getStage().save();
        Repo.objectFolder.save(commitData);
    }

    /** Commit message. */
    private final String message;

    /** Second parent, only for merge command. */
    private final String secParent;

}
