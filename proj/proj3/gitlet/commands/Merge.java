package gitlet.commands;

import gitlet.Main;
import gitlet.objects.CommitData;
import gitlet.repo.Repo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

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
        otherBranch = _operands[0];
        message = "Merged " + otherBranch + " into " + Repo.getCurrBranch() + ".";
    }

    @Override
    void checkOperands() {
        if (!Repo.getStage().additionMap.isEmpty() || !Repo.getStage().removalSet.isEmpty()) {
            Main.exitWithError("You have uncommitted changes.");
        }
        if (!Repo.branchFolder.hasBranch(otherBranch)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (Repo.getCurrBranch().equals(otherBranch)) {
            Main.exitWithError("Cannot merge a branch with itself.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();
        String splitPointUid = findSplitUid();
        assert splitPointUid != null;

        if (splitPointUid.equals(Repo.branchFolder.getHeadUid(otherBranch))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (splitPointUid.equals(Repo.getCurrHeadUid())) {
            Command checkout = new Checkout(createArgs("checkout " + otherBranch));
            checkout.run();
            System.out.println("Current branch fast-forward.");
            return;
        }

        otherHeadUid = Repo.branchFolder.getHeadUid(otherBranch);
        otherBranchCommit = Repo.objectFolder.getCommit(otherHeadUid);
        splitPoint = Repo.objectFolder.getCommit(splitPointUid);
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
        Set<String> modifiedInCurr = Repo.getCurrCommit().getInterDiffFiles(splitPoint);
        Set<String> modifiedOnlyInOther = new HashSet<>(modifiedInOther);
        modifiedOnlyInOther.removeAll(modifiedInCurr);
        for (String fileName : modifiedOnlyInOther) {
            checkoutAndAdd(fileName);
        }

        Set<String> notInSplitButInOther = otherBranchCommit.getDiffSet(splitPoint);
        Set<String> notInSplitButInCurr = Repo.getCurrCommit().getDiffFiles(splitPoint);
        Set<String> notInSplitButOnlyInOther = new HashSet<>(notInSplitButInOther);
        notInSplitButOnlyInOther.removeAll(notInSplitButInCurr);
        for (String fileName : notInSplitButOnlyInOther) {
            checkoutAndAdd(fileName);
        }

        Set<String> inSplitButNotInOther = splitPoint.getDiffSet(otherBranchCommit);
        inSplitButNotInOther.removeAll(Repo.getCurrCommit().getDiffFiles(splitPoint));

        for (String fileName : inSplitButNotInOther) {
            checkUntrackedFile(fileName);
            Command remove = new Rm(createArgs("rm " + fileName));
            remove.run();
            Repo.update();
        }
    }

    /**
     * Return true if there is a conflict. Merge all conflict files.
     */
    private boolean mergeConflict() throws IOException {
        Set<String> modifiedInOther = otherBranchCommit.getInterDiffFiles(splitPoint);
        Set<String> modifiedInCurr = Repo.getCurrCommit().getInterDiffFiles(splitPoint);
        Set<String> bothModified = new HashSet<>(modifiedInCurr);
        bothModified.retainAll(modifiedInOther);

        Set<String> notInSplitButInBoth = Repo.getCurrCommit().getInterSet(otherBranchCommit);
        notInSplitButInBoth.removeAll(splitPoint.getAllFileName());

        Set<String> diffModifiedFiles = Repo.getCurrCommit().getInterDiffFiles(otherBranchCommit);
        Set<String> allFilesLeft = new HashSet<>(bothModified);
        allFilesLeft.addAll(notInSplitButInBoth);
        diffModifiedFiles.retainAll(allFilesLeft);

        Set<String> modifiedInCurrDelInOther = Repo.getCurrCommit().getInterDiffFiles(splitPoint);
        modifiedInCurrDelInOther.removeAll(otherBranchCommit.getAllFileName());
        diffModifiedFiles.addAll(modifiedInCurrDelInOther);

        Set<String> modifiedInOtherDelInCurr = otherBranchCommit.getInterDiffFiles(splitPoint);
        modifiedInOtherDelInCurr.removeAll(Repo.getCurrCommit().getAllFileName());
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
        if (Repo.getCurrCommit().containsFile(fileName)) {
            content += Repo.objectFolder.getBolb(Repo.getCurrCommit(), fileName).getContent();
        }

        content += "=======\n";
        if (otherBranchCommit.containsFile(fileName)) {
            content += Repo.objectFolder.getBolb(otherBranchCommit, fileName).getContent();
        }

        content += ">>>>>>>\n";
        Repo.workFolder.writeToFile(fileName, content);
        Command add = new Add(createArgs("add " + fileName));
        add.run();
    }

    /**
     * Check for untracked file FILENAME which will be overwritten
     * or delete.
     */
    private void checkUntrackedFile(String fileName) {
        if (Repo.workFolder.checkExist(fileName)) {
            if ((!Repo.currCommitContainsFile(fileName)
                    && !Repo.getStage().additionMap.containsKey(fileName))
                    || Repo.getStage().removalSet.contains(fileName)) {
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
        Repo.update();
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
        Queue<CommitData> history = Repo.getHistoryOfCurrCommit();
        HashSet<String> historyOfCurrBranch = new HashSet<>();

        for (CommitData commitData : history) {
            historyOfCurrBranch.add(commitData.getUID());
        }

        Queue<CommitData> historyOfGivenBranch = Repo.branchFolder.getHistoryOfBranch(otherBranch);
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
