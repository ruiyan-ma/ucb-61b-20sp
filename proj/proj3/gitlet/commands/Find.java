package gitlet.commands;

import gitlet.Main;
import gitlet.objects.CommitData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gitlet.Main.repo;

/**
 * This class is the find command class.
 *
 * @author ryan ma
 */

public class Find extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Find(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        message = operands[0];
    }

    @Override
    void checkOperands() {

    }

    @Override
    public void run() {
        checkOperands();

        // get all branches
        List<String> branches = repo.latestFolder.getAllBranches();
        Set<String> uidSet = new HashSet<>();
        assert branches != null;

        for (String branch : branches) {
            // get the latest commit uid of this branch
            String latestUid = repo.latestFolder.getLatestUid(branch);
            CommitData latest = repo.objectFolder.getCommit(latestUid);
            for (CommitData commit : latest.getHistoryCommit()) {
                if (commit.getMessage().equals(message)) {
                    uidSet.add(commit.getUID());
                }
            }
        }

        if (uidSet.isEmpty()) {
            Main.exitWithError("Found no commit with that message.");
        } else {
            for (String uid : uidSet) {
                System.out.println(uid);
            }
        }
    }

    /**
     * Commit message.
     */
    private final String message;
}
