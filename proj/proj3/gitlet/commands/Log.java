package gitlet.commands;

import gitlet.objects.CommitData;
import gitlet.repo.Repo;

/** This class is the log command class.
 *  @author ryan ma
 *  */

public class Log extends Command {

    /** Constructor function with ARGS. */
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
        System.out.println(Repo.logFolder.readLogOfBranch(Repo.getCurrBranch()));
    }
}
