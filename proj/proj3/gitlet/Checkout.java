package gitlet;

import java.io.IOException;

/** This class is the checkout command class.
 *  @author ryan ma
 *  */

class Checkout extends Command {

    /** Constructor function with ARGS. */
    Checkout(String[] args) {
        super(args);
        checkInitial();
        if (_operands.length == 1) {
            _branch = _operands[0];
        } else if (_operands.length == 2) {
            _file = _operands[1];
        } else if (_operands.length == 3) {
            _id = _operands[0];
            _file = _operands[2];
        } else {
            Main.exitWithError("Incorrect operands.");
        }
    }

    @Override
    void checkOperands() {
        checkInitial();
        if (_operands.length == 1) {
            if (!Repo.getBranchDir().containsBranch(_branch)) {
                Main.exitWithError("No such branch exist.");
            }
            if (Repo.getCurrBranch().equals(_branch)) {
                Main.exitWithError("No need to checkout the current branch.");
            }
        } else if (_operands.length == 2) {
            if (!_operands[0].equals("--")) {
                Main.exitWithError("Incorrect operands.");
            }
            if (!Repo.currCommitContainsFile(_file)) {
                Main.exitWithError("File does not exist in that commit.");
            }
        } else if (_operands.length == 3) {
            if (!_operands[1].equals("--")) {
                Main.exitWithError("Incorrect operands.");
            }
            if (!Repo.getObjectDir().containsCommit(_id)) {
                Main.exitWithError("No commit with that id exists.");
            } else if (!Repo.getObjectDir().commitContainsFile(_id, _file)) {
                Main.exitWithError("File does not exist in that commit.");
            }
        }
    }

    @Override
    void run() throws IOException {
        checkOperands();
        if (_operands.length == 1) {
            String branchHeadUID = Repo.getBranchDir().getHeadOfBranch(_branch);
            CommitData branchHead = Repo.getObjectDir().getCommit(
                                        branchHeadUID);
            if (Repo.getWorkDir().conditionForCheckoutAllFiles(branchHead)) {
                Main.exitWithError("There is an untracked file in the "
                                   + "way; delete it, or add and "
                                   + "commit it first.");
            }
            Repo.getWorkDir().checkoutAllFilesWithCommit(
                Repo.getObjectDir().getCommit(branchHeadUID));
            Repo.changeCurrBranch(_branch);
            Repo.getStage().clean();
            Repo.getStage().save();
        } else if (_operands.length == 2) {
            Repo.checkoutFileInCurrCommit(_file);
        } else if (_operands.length == 3) {
            Repo.getWorkDir().checkoutFileInCommit(
                Repo.getObjectDir().getCommit(_id), _file);
        }
    }

    /** File name. */
    private String _file;

    /** Commit id. */
    private String _id;

    /** Branch name. */
    private String _branch;

}
