package gitlet;

import java.util.List;

/** This class is the global-log command class.
 *  @author ryan ma
 *  */

class GLog extends Command {

    /** Constructor function with ARGS. */
    GLog(String[] args) {
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
        List<String> files = Utils.plainFilenamesIn(LogDir.LOG_FOLDER);
        assert files != null;
        for (String file : files) {
            String log = Repo.getLogDir().readLogOfBranch(file);
            System.out.print(log);
        }
    }

}
