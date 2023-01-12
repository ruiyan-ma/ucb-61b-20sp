package gitlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/** This class is the merge command class.
 *  @author ryan ma */

class Merge extends Command {

    /** Constructor function with ARGS. */
    Merge(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _other = _operands[0];
        _message = "Merged " + _other + " into " + Repo.getCurrBranch() + ".";
    }

    @Override
    void checkOperands() {
        if (!Repo.getStage().isAddEmpty() || !Repo.getStage().isRmEmpty()) {
            Main.exitWithError("You have uncommited changes.");
        }
        if (!Repo.getBranchDir().containsBranch(_other)) {
            Main.exitWithError("A branch with that name does not exist.");
        }
        if (Repo.getCurrBranch().equals(_other)) {
            Main.exitWithError("Cannot merge a branch with itself.");
        }
    }

    @Override
    void run() throws IOException {
        checkOperands();
        String splitPointUid = findSplitUid();
        assert splitPointUid != null;
        if (splitPointUid.equals(
                    Repo.getBranchDir().getHeadOfBranch(_other))) {
            System.out.println("Given branch is an "
                               + "ancestor of the current branch.");
            return;
        }
        if (splitPointUid.equals(Repo.getCurrHead())) {
            Command checkout = new Checkout(createArgs("checkout " + _other));
            checkout.run();
            System.out.println("Current branch fast-forward.");
            return;
        }
        _otherHeadUid = Repo.getBranchDir().getHeadOfBranch(_other);
        _otherBranchCommit = Repo.getBranchDir().getCommitOfBranch(_other);
        _splitPoint = Repo.getObjectDir().getCommit(splitPointUid);
        mergeNotConflict();
        boolean existConflict = mergeConflict();
        String[] args = new String[2];
        args[0] = "commit";
        args[1] = _message;
        Command commit = new Commit(args, _otherHeadUid);
        commit.run();
        if (existConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Merge all files not in intersection. */
    private void mergeNotConflict() throws IOException {
        Set<String> modifiedInOther =
            _otherBranchCommit.getInterDiffFiles(_splitPoint);
        Set<String> modifiedInCurr =
            Repo.getCurrCommit().getInterDiffFiles(_splitPoint);
        Set<String> modifiedOnlyInOther = new HashSet<>(modifiedInOther);
        modifiedOnlyInOther.removeAll(modifiedInCurr);
        for (String fileName : modifiedOnlyInOther) {
            checkoutAndAdd(fileName);
        }

        Set<String> notInSplitButInOther =
            _otherBranchCommit.getDiffSet(_splitPoint);
        Set<String> notInSplitButInCurr =
            Repo.getCurrCommit().getDiffFiles(_splitPoint);
        Set<String> notInSplitButOnlyInOther =
            new HashSet<>(notInSplitButInOther);
        notInSplitButOnlyInOther.removeAll(notInSplitButInCurr);
        for (String fileName : notInSplitButOnlyInOther) {
            checkoutAndAdd(fileName);
        }

        Set<String> inSplitButNotInOther =
            _splitPoint.getDiffSet(_otherBranchCommit);
        inSplitButNotInOther.removeAll(
            Repo.getCurrCommit().getDiffFiles(_splitPoint));

        for (String fileName : inSplitButNotInOther) {
            checkUntrackedFile(fileName);
            Command remove = new Rm(createArgs("rm " + fileName));
            remove.run();
            Repo.update();
        }
    }

    /** Return true if there is a conflict. Merge all conflict files. */
    private boolean mergeConflict() throws IOException {
        Set<String> modifiedInOther =
            _otherBranchCommit.getInterDiffFiles(_splitPoint);
        Set<String> modifiedInCurr =
            Repo.getCurrCommit().getInterDiffFiles(_splitPoint);
        Set<String> bothModified = new HashSet<>(modifiedInCurr);
        bothModified.retainAll(modifiedInOther);

        Set<String> notInSplitButInBoth =
            Repo.getCurrCommit().getInterSet(_otherBranchCommit);
        notInSplitButInBoth.removeAll(_splitPoint.getAllFiles());

        Set<String> diffModifiedFiles =
            Repo.getCurrCommit().getInterDiffFiles(_otherBranchCommit);
        Set<String> allFilesLeft = new HashSet<>(bothModified);
        allFilesLeft.addAll(notInSplitButInBoth);
        diffModifiedFiles.retainAll(allFilesLeft);

        Set<String> modifiedInCurrDelInOther =
            Repo.getCurrCommit().getInterDiffFiles(_splitPoint);
        modifiedInCurrDelInOther.removeAll(_otherBranchCommit.getAllFiles());
        diffModifiedFiles.addAll(modifiedInCurrDelInOther);

        Set<String> modifiedInOtherDelInCurr =
            _otherBranchCommit.getInterDiffFiles(_splitPoint);
        modifiedInOtherDelInCurr.removeAll(
            Repo.getCurrCommit().getAllFiles());
        diffModifiedFiles.addAll(modifiedInOtherDelInCurr);

        if (diffModifiedFiles.isEmpty()) {
            return false;
        }
        for (String fileName : diffModifiedFiles) {
            writeConflictFile(fileName);
        }
        return true;
    }

    /** Handle conflict of file FILENAME. */
    private void writeConflictFile(String fileName) throws IOException {
        checkUntrackedFile(fileName);
        String content = "<<<<<<< HEAD\n";
        if (Repo.getCurrCommit().containFile(fileName)) {
            content += Repo.getObjectDir().getBolbInCommit(
                           Repo.getCurrCommit(), fileName).getContent();
        }
        content += "=======\n";
        if (_otherBranchCommit.containFile(fileName)) {
            content += Repo.getObjectDir().getBolbInCommit(
                           _otherBranchCommit, fileName).getContent();
        }
        content += ">>>>>>>\n";
        Repo.getWorkDir().writeToFile(fileName, content);
        Command add = new Add(createArgs("add " + fileName));
        add.run();
    }

    /** Check for untracked file FILENAME which will be overwritten
     *  or delete. */
    private void checkUntrackedFile(String fileName) {
        if (Repo.getWorkDir().checkExist(fileName)) {
            if ((!Repo.currCommitContainsFile(fileName)
                    && !Repo.getStage().addContains(fileName))
                    || Repo.getStage().rmContains(fileName)) {
                Main.exitWithError("There is an untracked file "
                                   + "in the way; delete it, "
                                   + "or add and commit it first.");
            }
        }
    }

    /** Checkout and staged file FILENAME. */
    private void checkoutAndAdd(String fileName) throws IOException {
        checkUntrackedFile(fileName);
        Command checkout = new Checkout(createArgs("checkout "
                                        + _otherHeadUid + " -- " + fileName));
        checkout.run();
        Command add = new Add(createArgs("add " + fileName));
        add.run();
        Repo.update();
    }

    /** Return string array args of ARG. This method is used to
     * create args for other command. */
    private String[] createArgs(String arg) {
        return arg.split(" ");
    }

    /** Return the uid of the split point. */
    private String findSplitUid() {
        Queue<CommitData> history = Repo.getHistoryOfCurrBranch();
        HashSet<String> historyOfCurrBranch = new HashSet<>();
        for (CommitData commitData : history) {
            historyOfCurrBranch.add(commitData.getUID());
        }
        Queue<CommitData> historyOfGivenBranch =
            Repo.getBranchDir().getHistoryOfBranch(_other);
        while (!historyOfGivenBranch.isEmpty()) {
            CommitData commitData = historyOfGivenBranch.remove();
            if (historyOfCurrBranch.contains(commitData.getUID())) {
                return commitData.getUID();
            }
        }
        return null;
    }

    /** Branch name. */
    private final String _other;

    /** Branch head uid. */
    private String _otherHeadUid;

    /** Other branch head commit. */
    private CommitData _otherBranchCommit;

    /** Split point commit. */
    private CommitData _splitPoint;


    /** Commit message. */
    private final String _message;

}