package gitlet.commands;


import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static gitlet.Main.repo;

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
        List<String> branches = repo.branchFolder.getAllBranches();
        String currBranch = repo.getCurrBranch();
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
        Set<String> set = repo.getStage().additionMap.keySet();
        for (String file : set) {
            status.append(file).append("\n");
        }
        status.append("\n");
        return status.toString();
    }

    /** Return removed files status. */
    private String removedFileStatus() {
        StringBuilder status = new StringBuilder("=== Removed Files ===\n");
        Set<String> set = repo.getStage().removalSet;
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

        for (String fileName: repo.getCurrCommit().getAllFileName()) {
            if (!repo.workFolder.checkExist(fileName)) {
                if (!repo.getStage().removalSet.contains(fileName)) {
                    set.add(fileName + " (deleted)\n");
                }
            } else if (!repo.workFolder.compareFile(fileName, repo.getCurrCommit().getBolbUID(fileName))
                       && !repo.getStage().additionMap.containsKey(fileName)) {
                set.add(fileName + " (modified)\n");
            }
        }

        Set<String> stageSet = repo.getStage().additionMap.keySet();
        for (String file : stageSet) {
            if (!repo.workFolder.checkExist(file)) {
                set.add(file + " (deleted)\n");
            } else if (!repo.workFolder.compareFile(file, repo.getStage().getBolbUid(file))) {
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
        List<String> files = repo.workFolder.getAllFileName();
        assert files != null;
        for (String file : files) {
            if ((!repo.getCurrCommit().containsFile(file)
                    && !repo.getStage().additionMap.containsKey(file))
                    || repo.getStage().removalSet.contains(file)) {
                status.append(file).append("\n");
            }
        }
        return status.toString();
    }
}
