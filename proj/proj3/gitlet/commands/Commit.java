package gitlet.commands;

import gitlet.objects.CommitData;
import gitlet.Main;

import java.io.IOException;

import static gitlet.Main.repo;

/** This class is the commit command class.
 *  @author ryan ma
 *  */

public class Commit extends Command {

    /** Constructor function with ARGS. */
    public Commit(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        message = operands[0];
        secParent = null;
    }

    /** Constructor for merge command, with ARGS and SECPARENT. */
    Commit(String[] args, String secParent) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        message = operands[0];
        this.secParent = secParent;
    }

    @Override
    void checkOperands() {
        if (repo.getStage().additionMap.isEmpty() && repo.getStage().removalSet.isEmpty()) {
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
            commitData = new CommitData(message, repo.getCurrHeadUid(), secParent, repo.getStage());
        } else {
            commitData = new CommitData(message, repo.getCurrHeadUid(), repo.getStage());
        }

        // set current commit
        repo.setCurrCommit(commitData);

        // set head uid for current branch
        String uid = commitData.getUID();
        repo.setCurrHeadUid(uid);

        // update the latest commit uid for current branch
        repo.latestFolder.setLatestUid(repo.getCurrBranch(), uid);

        // log commit
        repo.logFolder.writeLogToBranch(repo.getCurrBranch(), repo.getCurrCommit());

        repo.getStage().clean();
        repo.getStage().save();
        repo.objectFolder.save(commitData);
    }

    /** Commit message. */
    private final String message;

    /** Second parent, only for merge command. */
    private final String secParent;

}
