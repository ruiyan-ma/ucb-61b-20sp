package gitlet.commands;

import gitlet.Main;
import gitlet.objects.CommitData;

import java.io.IOException;

import static gitlet.Main.repo;

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
        if (operands.length == 1) {
            branchName = operands[0];
        } else if (operands.length == 2) {
            fileName = operands[1];
        } else if (operands.length == 3) {
            commitId = operands[0];
            fileName = operands[2];
        } else {
            Main.exitWithError("Incorrect operands.");
        }
    }

    @Override
    void checkOperands() {
        checkInitial();
        if (operands.length == 1) {
            if (!repo.branchFolder.hasBranch(branchName)) {
                Main.exitWithError("No such branch exist.");
            }
            if (repo.getCurrBranch().equals(branchName)) {
                Main.exitWithError("No need to checkout the current branch.");
            }
        } else if (operands.length == 2) {
            if (!operands[0].equals("--")) {
                Main.exitWithError("Incorrect operands.");
            }
            if (!repo.getCurrCommit().containsFile(fileName)) {
                Main.exitWithError("File does not exist in that commit.");
            }
        } else if (operands.length == 3) {
            if (!operands[1].equals("--")) {
                Main.exitWithError("Incorrect operands.");
            }
            if (!repo.objectFolder.containsCommit(commitId)) {
                Main.exitWithError("No commit with that id exists.");
            } else if (!repo.objectFolder.getCommit(commitId).containsFile(fileName)) {
                Main.exitWithError("File does not exist in that commit.");
            }
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();
        if (operands.length == 1) {
            String branchHeadUID = repo.branchFolder.getHeadUid(branchName);
            CommitData branchHead = repo.objectFolder.getCommit(branchHeadUID);

            if (repo.workFolder.canNotCheckoutAllFiles(branchHead)) {
                Main.exitWithError("There is an untracked file in the way; delete it, " +
                        "or add and commit it first.");
            }

            repo.workFolder.checkoutAllFilesWithCommit(branchHead);
            repo.setCurrBranch(branchName);
            repo.setCurrCommit(branchHead);
            repo.getStage().clean();
            repo.getStage().save();
        } else if (operands.length == 2) {
            repo.workFolder.checkoutFileWithCommit(repo.getCurrCommit(), fileName);
        } else if (operands.length == 3) {
            repo.workFolder.checkoutFileWithCommit(repo.objectFolder.getCommit(commitId), fileName);
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
