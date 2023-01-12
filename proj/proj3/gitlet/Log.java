package gitlet;

/** This class is the log command class.
 *  @author ryan ma
 *  */

class Log extends Command {

    /** Constructor function with ARGS. */
    Log(String[] args) {
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
        CommitData commitData = Repo.getCurrCommit();
        while (commitData != null) {
            String log = commitData.getLog();
            System.out.print(log);
            commitData = commitData.getParent();
        }
    }

}
