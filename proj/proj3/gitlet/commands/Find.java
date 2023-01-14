package gitlet.commands;

import gitlet.Main;
import gitlet.objects.CommitData;
import gitlet.repo.Repo;

import java.util.List;

/** This class is the find command class.
 *  @author ryan ma
 *  */

public class Find extends Command {

    /** Constructor function with ARGS. */
    public Find(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _message = _operands[0];
    }

    @Override
    void checkOperands() {

    }

    @Override
    public void run() {
        checkOperands();
        List<String> branches = Repo.branchFolder.getAllBranches();
        StringBuilder find = new StringBuilder();
        assert branches != null;
        for (String branch : branches) {
            CommitData commit = Repo.branchLatestFolder.getLatestCommit(branch);
            while (commit != null) {
                if (commit.getMessage().equals(_message)) {
                    find.append(commit.getUID()).append("\n");
                }
                commit = commit.getParent();
            }
        }
        if (find.toString().equals("")) {
            Main.exitWithError("Found no commit with that message.");
        } else {
            System.out.print(find);
        }
    }

    /** Commit message. */
    private final String _message;

}
