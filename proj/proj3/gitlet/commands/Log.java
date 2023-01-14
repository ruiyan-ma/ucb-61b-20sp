package gitlet.commands;

import gitlet.objects.CommitData;

import static gitlet.Main.repo;

/**
 * This class is the log command class.
 *
 * @author ryan ma
 */

public class Log extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Log(String[] args) {
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
        CommitData commitData = repo.getCurrCommit();
        while (commitData != null) {
            String log = commitData.getLog();
            System.out.print(log);
            commitData = commitData.getParent();
        }
    }
}
