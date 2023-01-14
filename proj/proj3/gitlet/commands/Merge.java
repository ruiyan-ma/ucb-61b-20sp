package gitlet.commands;

import gitlet.Main;
import gitlet.objects.CommitData;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static gitlet.Main.repo;

/**
 * This class is the merge command class.
 *
 * @author ryan ma
 */

public class Merge extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Merge(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        otherBranch = operands[0];
        message = "Merged " + otherBranch + " into " + repo.getCurrBranch() + ".";
    }

    @Override
    void checkOperands() {
        if (!repo.getStage().additionMap.isEmpty() || !repo.getStage().removalSet.isEmpty()) {
            Main.exitWithError("You have uncommitted changes.");
        }
        if (!repo.branchFolder.hasBranch(otherBranch)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (repo.getCurrBranch().equals(otherBranch)) {
            Main.exitWithError("Cannot merge a branch with itself.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();
        String splitPointUid = findSplitUid();
        assert splitPointUid != null;

        if (splitPointUid.equals(repo.branchFolder.getHeadUid(otherBranch))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (splitPointUid.equals(repo.getCurrHeadUid())) {
            Command checkout = new Checkout(createArgs("checkout " + otherBranch));
            checkout.run();
            System.out.println("Current branch fast-forward.");
            return;
        }

        otherHeadUid = repo.branchFolder.getHeadUid(otherBranch);
        otherBranchCommit = repo.objectFolder.getCommit(otherHeadUid);
        splitPoint = repo.objectFolder.getCommit(splitPointUid);
        mergeNotConflict();

        boolean existConflict = mergeConflict();
        String[] args = new String[2];
        args[0] = "commit";
        args[1] = message;
        Command commit = new Commit(args, otherHeadUid);
        commit.run();

        if (existConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Merge all files not in intersection.
     */
    private void mergeNotConflict() throws IOException {
        Set<String> modifiedInOther = otherBranchCommit.getInterDiffFiles(splitPoint);
        Set<String> modifiedInCurr = repo.getCurrCommit().getInterDiffFiles(splitPoint);
        Set<String> modifiedOnlyInOther = new HashSet<>(modifiedInOther);
        modifiedOnlyInOther.removeAll(modifiedInCurr);
        for (String fileName : modifiedOnlyInOther) {
            checkoutAndAdd(fileName);
        }

        Set<String> notInSplitButInOther = otherBranchCommit.getDiffSet(splitPoint);
        Set<String> notInSplitButInCurr = repo.getCurrCommit().getDiffFiles(splitPoint);
        Set<String> notInSplitButOnlyInOther = new HashSet<>(notInSplitButInOther);
        notInSplitButOnlyInOther.removeAll(notInSplitButInCurr);
        for (String fileName : notInSplitButOnlyInOther) {
            checkoutAndAdd(fileName);
        }

        Set<String> inSplitButNotInOther = splitPoint.getDiffSet(otherBranchCommit);
        inSplitButNotInOther.removeAll(repo.getCurrCommit().getDiffFiles(splitPoint));

        for (String fileName : inSplitButNotInOther) {
            checkUntrackedFile(fileName);
            Command remove = new Rm(createArgs("rm " + fileName));
            remove.run();
            repo.update();
        }
    }

    /**
     * Return true if there is a conflict. Merge all conflict files.
     */
    private boolean mergeConflict() throws IOException {
        Set<String> modifiedInOther = otherBranchCommit.getInterDiffFiles(splitPoint);
        Set<String> modifiedInCurr = repo.getCurrCommit().getInterDiffFiles(splitPoint);
        Set<String> bothModified = new HashSet<>(modifiedInCurr);
        bothModified.retainAll(modifiedInOther);

        Set<String> notInSplitButInBoth = repo.getCurrCommit().getInterSet(otherBranchCommit);
        notInSplitButInBoth.removeAll(splitPoint.getAllFileName());

        Set<String> diffModifiedFiles = repo.getCurrCommit().getInterDiffFiles(otherBranchCommit);
        Set<String> allFilesLeft = new HashSet<>(bothModified);
        allFilesLeft.addAll(notInSplitButInBoth);
        diffModifiedFiles.retainAll(allFilesLeft);

        Set<String> modifiedInCurrDelInOther = repo.getCurrCommit().getInterDiffFiles(splitPoint);
        modifiedInCurrDelInOther.removeAll(otherBranchCommit.getAllFileName());
        diffModifiedFiles.addAll(modifiedInCurrDelInOther);

        Set<String> modifiedInOtherDelInCurr = otherBranchCommit.getInterDiffFiles(splitPoint);
        modifiedInOtherDelInCurr.removeAll(repo.getCurrCommit().getAllFileName());
        diffModifiedFiles.addAll(modifiedInOtherDelInCurr);

        if (diffModifiedFiles.isEmpty()) {
            return false;
        }

        for (String fileName : diffModifiedFiles) {
            writeConflictFile(fileName);
        }

        return true;
    }

    /**
     * Handle conflict of file FILENAME.
     */
    private void writeConflictFile(String fileName) throws IOException {
        checkUntrackedFile(fileName);
        String content = "<<<<<<< HEAD\n";
        if (repo.getCurrCommit().containsFile(fileName)) {
            content += repo.objectFolder.getBolb(repo.getCurrCommit(), fileName).getContent();
        }

        content += "=======\n";
        if (otherBranchCommit.containsFile(fileName)) {
            content += repo.objectFolder.getBolb(otherBranchCommit, fileName).getContent();
        }

        content += ">>>>>>>\n";
        repo.workFolder.writeToFile(fileName, content);
        Command add = new Add(createArgs("add " + fileName));
        add.run();
    }

    /**
     * Check for untracked file FILENAME which will be overwritten
     * or delete.
     */
    private void checkUntrackedFile(String fileName) {
        if (repo.workFolder.checkExist(fileName)) {
            if ((!repo.getCurrCommit().containsFile(fileName)
                    && !repo.getStage().additionMap.containsKey(fileName))
                    || repo.getStage().removalSet.contains(fileName)) {
                Main.exitWithError("There is an untracked file in the way; delete it, " +
                        "or add and commit it first.");
            }
        }
    }

    /**
     * Checkout and staged file FILENAME.
     */
    private void checkoutAndAdd(String fileName) throws IOException {
        checkUntrackedFile(fileName);
        Command checkout = new Checkout(createArgs("checkout " + otherHeadUid + " -- " + fileName));
        checkout.run();
        Command add = new Add(createArgs("add " + fileName));
        add.run();
        repo.update();
    }

    /**
     * Return string array args of ARG. This method is used to
     * create args for other command.
     */
    private String[] createArgs(String arg) {
        return arg.split(" ");
    }

    /**
     * Return the uid of the split point.
     */
    private String findSplitUid() {
//        Queue<CommitData> history = repo.objectFolder.getHistoryOfCommit(repo.getCurrCommit());
        Queue<CommitData> history = repo.getCurrCommit().getHistoryCommit();
        HashSet<String> historyOfCurrBranch = new HashSet<>();

        for (CommitData commitData : history) {
            historyOfCurrBranch.add(commitData.getUID());
        }

        Queue<CommitData> historyOfGivenBranch = repo.branchFolder.getHistoryOfBranch(otherBranch);
        while (!historyOfGivenBranch.isEmpty()) {
            CommitData commitData = historyOfGivenBranch.remove();
            if (historyOfCurrBranch.contains(commitData.getUID())) {
                return commitData.getUID();
            }
        }

        return null;
    }

    /**
     * Branch name.
     */
    private final String otherBranch;

    /**
     * Branch head uid.
     */
    private String otherHeadUid;

    /**
     * Other branch head commit.
     */
    private CommitData otherBranchCommit;

    /**
     * Split point commit.
     */
    private CommitData splitPoint;


    /**
     * Commit message.
     */
    private final String message;

}
