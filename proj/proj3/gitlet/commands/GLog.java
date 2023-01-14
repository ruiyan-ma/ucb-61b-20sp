package gitlet.commands;


import java.util.List;

import static gitlet.Main.repo;

/**
 * This class is the global-log command class.
 *
 * @author ryan ma
 */

public class GLog extends Command {

    /**
     * Constructor function with ARGS.
     */
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
        List<String> files = repo.logFolder.getAllFileName();
        assert files != null;
        for (String file : files) {
            String log = repo.logFolder.readLogOfBranch(file);
            System.out.print(log);
        }
    }
}
