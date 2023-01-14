package gitlet.commands;

import gitlet.Main;
import gitlet.objects.CommitData;
import gitlet.repo.Repo;

import java.io.IOException;

/**
 * This class is the checkout command class.
 *
 * @author ryan ma
 */

public class Checkout extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Checkout(String[] args) {
        super(args);
        checkInitial();
        if (_operands.length == 1) {
            branchName = _operands[0];
        } else if (_operands.length == 2) {
            fileName = _operands[1];
        } else if (_operands.length == 3) {
            commitId = _operands[0];
            fileName = _operands[2];
        } else {
            Main.exitWithError("Incorrect operands.");
        }
    }

    @Override
    void checkOperands() {
        checkInitial();
        if (_operands.length == 1) {
            if (!Repo.branchFolder.hasBranch(branchName)) {
                Main.exitWithError("No such branch exist.");
            }
            if (Repo.getCurrBranch().equals(branchName)) {
                Main.exitWithError("No need to checkout the current branch.");
            }
        } else if (_operands.length == 2) {
            if (!_operands[0].equals("--")) {
                Main.exitWithError("Incorrect operands.");
            }
            if (!Repo.currCommitContainsFile(fileName)) {
                Main.exitWithError("File does not exist in that commit.");
            }
        } else if (_operands.length == 3) {
            if (!_operands[1].equals("--")) {
                Main.exitWithError("Incorrect operands.");
            }
            if (!Repo.objectFolder.containsCommit(commitId)) {
                Main.exitWithError("No commit with that id exists.");
            } else if (!Repo.objectFolder.getCommit(commitId).containsFile(fileName)) {
                Main.exitWithError("File does not exist in that commit.");
            }
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();
        if (_operands.length == 1) {
            String branchHeadUID = Repo.branchFolder.getHeadUid(branchName);
            CommitData branchHead = Repo.objectFolder.getCommit(branchHeadUID);
            if (Repo.workFolder.canNotCheckoutAllFiles(branchHead)) {
                Main.exitWithError("There is an untracked file in the way; delete it, " +
                        "or add and commit it first.");
            }
            Repo.workFolder.checkoutAllFilesWithCommit(branchHead);
            Repo.setCurrBranch(branchName);
            Repo.getStage().clean();
            Repo.getStage().save();
        } else if (_operands.length == 2) {
            Repo.workFolder.checkoutFileWithCommit(Repo.getCurrCommit(), fileName);
        } else if (_operands.length == 3) {
            Repo.workFolder.checkoutFileWithCommit(Repo.objectFolder.getCommit(commitId), fileName);
        }
    }

    /**
     * File name.
     */
    private String fileName;

    /**
     * Commit id.
     */
    private String commitId;

    /**
     * Branch name.
     */
    private String branchName;

}
