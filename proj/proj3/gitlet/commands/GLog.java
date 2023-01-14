package gitlet.commands;

import gitlet.repo.Repo;

import java.util.List;

/** This class is the global-log command class.
 *  @author ryan ma
 *  */

public class GLog extends Command {

    /** Constructor function with ARGS. */
    public GLog(String[] args) {
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
        List<String> files = Repo.logFolder.getAllFileName();
        assert files != null;
        for (String file : files) {
            String log = Repo.logFolder.readLogOfBranch(file);
            System.out.print(log);
        }
    }
}
