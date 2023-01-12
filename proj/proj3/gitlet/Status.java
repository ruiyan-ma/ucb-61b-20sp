package gitlet;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

/** This class is the status command class.
 *  @author ryan ma
 *  */

class Status extends Command {

    /** Constructor function with ARGS. */
    Status(String[] args) {
        super(args, 0);
        checkInitial();
        checkOperandsNum();
    }

    @Override
    void checkOperands() {
    }

    @Override
    void run() {
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
        List<String> branches = Repo.getBranchDir().getAllBranches();
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
        Set<String> set = Repo.getStage().getFilesInAdd();
        for (String file : set) {
            status.append(file).append("\n");
        }
        status.append("\n");
        return status.toString();
    }

    /** Return removed files status. */
    private String removedFileStatus() {
        StringBuilder status = new StringBuilder("=== Removed Files ===\n");
        Set<String> set = Repo.getStage().getRemoval();
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
        TreeMap<String, String> trackMap = Repo.getCurrCommitMap();
        for (String file : trackMap.keySet()) {
            if (!Repo.getWorkDir().checkExist(file)) {
                if (!Repo.getStage().rmContains(file)) {
                    set.add(file + " (deleted)\n");
                }
            } else if (!Repo.currCommitSameFile(file)
                       && !Repo.getStage().addContains(file)) {
                set.add(file + " (modified)\n");
            }
        }
        Set<String> stageSet = Repo.getStage().getFilesInAdd();
        for (String file : stageSet) {
            if (!Repo.getWorkDir().checkExist(file)) {
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
        List<String> files = Repo.getWorkDir().getAllFiles();
        assert files != null;
        for (String file : files) {
            if ((!Repo.currCommitContainsFile(file)
                    && !Repo.getStage().addContains(file))
                    || Repo.getStage().rmContains(file)) {
                status.append(file).append("\n");
            }
        }
        return status.toString();
    }

}
