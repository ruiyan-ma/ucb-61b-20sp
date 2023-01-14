package gitlet.commands;

import gitlet.repo.Repo;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** This class is the status command class.
 *  @author ryan ma
 *  */

public class Status extends Command {

    /** Constructor function with ARGS. */
    public Status(String[] args) {
        super(args, 0);
        checkInitial();
        checkOperandsNum();
    }

    @Override
    void checkOperands() {
    }

    @Override
    public void run() {
        checkOperands();
        String status = getStatus();
        System.out.print(status);
    }

    /** Return the status information now. */
    String getStatus() {
        String status = branchStatus();
        status += stagedFileStatus();
        status += removedFileStatus();
        status += notStagedStatus();
        status += untrackedFileStatus();
        return status;
    }

    /** Return branch status. */
    private String branchStatus() {
        StringBuilder status = new StringBuilder("=== Branches ===\n");
        List<String> branches = Repo.branchFolder.getAllBranches();
        String currBranch = Repo.getCurrBranch();
        for (String branch : branches) {
            if (!branch.contentEquals(currBranch)) {
                status.append(branch).append("\n");
            } else {
                status.append("*").append(branch).append("\n");
            }
        }
        status.append("\n");
        return status.toString();
    }

    /** Return staged files status. */
    private String stagedFileStatus() {
        StringBuilder status = new StringBuilder("=== Staged Files ===\n");
        Set<String> set = Repo.getStage().additionMap.keySet();
        for (String file : set) {
            status.append(file).append("\n");
        }
        status.append("\n");
        return status.toString();
    }

    /** Return removed files status. */
    private String removedFileStatus() {
        StringBuilder status = new StringBuilder("=== Removed Files ===\n");
        Set<String> set = Repo.getStage().removalSet;
        for (String file : set) {
            status.append(file).append("\n");
        }
        status.append("\n");
        return status.toString();
    }

    /** Return modifications not staged for commit status. */
    private String notStagedStatus() {
        StringBuilder status = new StringBuilder(
            "=== Modifications Not Staged For Commit ===\n");
        TreeSet<String> set = new TreeSet<>();

        for (String fileName: Repo.getCurrCommit().getAllFileName()) {
            if (!Repo.workFolder.checkExist(fileName)) {
                if (!Repo.getStage().removalSet.contains(fileName)) {
                    set.add(fileName + " (deleted)\n");
                }
            } else if (!Repo.currCommitSameFile(fileName)
                       && !Repo.getStage().additionMap.containsKey(fileName)) {
                set.add(fileName + " (modified)\n");
            }
        }

        Set<String> stageSet = Repo.getStage().additionMap.keySet();
        for (String file : stageSet) {
            if (!Repo.workFolder.checkExist(file)) {
                set.add(file + " (deleted)\n");
            } else if (Repo.stageNotContainSameFile(file)) {
                set.add(file + " (modified)\n");
            }
        }

        for (String file : set) {
            status.append(file);
        }

        status.append("\n");
        return status.toString();
    }

    /** Return untracked files status. */
    private String untrackedFileStatus() {
        StringBuilder status = new StringBuilder("=== Untracked Files ===\n");
        List<String> files = Repo.workFolder.getAllFileName();
        assert files != null;
        for (String file : files) {
            if ((!Repo.currCommitContainsFile(file)
                    && !Repo.getStage().additionMap.containsKey(file))
                    || Repo.getStage().removalSet.contains(file)) {
                status.append(file).append("\n");
            }
        }
        return status.toString();
    }

}
