package gitlet.commands;

import gitlet.Main;
import gitlet.objects.CommitData;
import gitlet.repo.Repo;

import java.io.IOException;

/** This class is the checkout command class.
 *  @author ryan ma
 *  */

public class Checkout extends Command {

    /** Constructor function with ARGS. */
    public Checkout(String[] args) {
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
            if (!Repo.branchFolder.hasBranch(_branch)) {
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
            if (!Repo.objectFolder.containsCommit(_id)) {
                Main.exitWithError("No commit with that id exists.");
            } else if (!Repo.objectFolder.getCommit(_id).containsFile(_file)) {
                Main.exitWithError("File does not exist in that commit.");
            }
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();
        if (_operands.length == 1) {
            String branchHeadUID = Repo.branchFolder.getHeadUid(_branch);
            CommitData branchHead = Repo.objectFolder.getCommit(branchHeadUID);
            if (Repo.workFolder.canNotCheckoutAllFiles(branchHead)) {
                Main.exitWithError("There is an untracked file in the "
                                   + "way; delete it, or add and "
                                   + "commit it first.");
            }
            Repo.workFolder.checkoutAllFilesWithCommit(Repo.objectFolder.getCommit(branchHeadUID));
            Repo.setCurrBranch(_branch);
            Repo.getStage().clean();
            Repo.getStage().save();
        } else if (_operands.length == 2) {
            Repo.workFolder.checkoutFileWithCommit(Repo.getCurrCommit(), _file);
        } else if (_operands.length == 3) {
            Repo.workFolder.checkoutFileWithCommit(Repo.objectFolder.getCommit(_id), _file);
        }
    }

    /** File name. */
    private String _file;

    /** Commit id. */
    private String _id;

    /** Branch name. */
    private String _branch;

}
