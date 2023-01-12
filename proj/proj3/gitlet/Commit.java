package gitlet;

import java.io.IOException;

/** This class is the commit command class.
 *  @author ryan ma
 *  */

class Commit extends Command {

    /** Constructor function with ARGS. */
    Commit(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _message = _operands[0];
        _secParent = null;
    }

    /** Constructor for merge command, with ARGS and SECPARENT. */
    Commit(String[] args, String secParent) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _message = _operands[0];
        _secParent = secParent;
    }

    @Override
    void checkOperands() {
        if (Repo.getStage().isAddEmpty() && Repo.getStage().isRmEmpty()) {
            Main.exitWithError("No changes added to the commit.");
        }
        if (_message.equals("")) {
            Main.exitWithError("Please enter a commit message.");
        }
    }

    @Override
    void run() throws IOException {
        checkOperands();
        CommitData commitData;
        if (_secParent != null) {
            commitData = new CommitData(
                _message, Repo.getCurrHead(), _secParent, Repo.getStage());
        } else {
            commitData = new CommitData(
                _message, Repo.getCurrHead(), Repo.getStage());
        }
        Repo.changeCurrCommit(commitData);
        String uid = commitData.getUID();
        Repo.changeCurrBranchHead(uid);
        Repo.getBranchLatestDir().writeLatestCommit(Repo.getCurrBranch(), uid);
        Repo.writeLogToCurrBranch();
        Repo.getStage().clean();
        Repo.getStage().save();
        Repo.getCurrCommit().save();
    }

    /** Commit message. */
    private final String _message;

    /** Second parent, only for merge command. */
    private final String _secParent;

}
