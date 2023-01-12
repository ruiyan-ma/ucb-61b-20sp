package gitlet;

import java.io.IOException;

/** This class is the rm command class.
 *  @author ryan ma
 *  */

class Rm extends Command {

    /** Constructor function with ARGS. */
    Rm(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _file = _operands[0];
    }

    @Override
    void checkOperands() {

    }

    @Override
    void run() throws IOException {
        checkOperands();
        boolean addContain = Repo.getStage().addContains(_file);
        boolean commitContain = Repo.currCommitContainsFile(_file);
        if (addContain) {
            Repo.getStage().rmEntryInAdd(_file);
        }
        if (commitContain) {
            Repo.getStage().addToRm(_file);
            Repo.getWorkDir().deleteFile(_file);
        }
        if (!addContain && !commitContain) {
            Main.exitWithError("No reason to remove the file.");
        }
        Repo.getStage().save();
    }

    /** Removed file. */
    private final String _file;

}
