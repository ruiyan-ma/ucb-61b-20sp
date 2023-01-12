package gitlet;

import java.io.IOException;

/** This class is the add command class.
 *  @author ryan ma
 *  */

class Add extends Command {

    /** Constructor function with ARGS. */
    Add(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _file = _operands[0];
    }

    @Override
    void checkOperands() {
        if (!Repo.getWorkDir().checkExist(_operands[0])) {
            Main.exitWithError("File does not exist.");
        }
    }

    @Override
    void run() throws IOException {
        checkOperands();
        if (Repo.getStage().rmContains(_file)) {
            if (Repo.getStage().addContains(_file)) {
                Repo.getStage().rmEntryInAdd(_file);
            }
            Repo.getStage().rmEntryInRm(_file);
        }
        if (Repo.currCommitSameFile(_file)) {
            if (Repo.getStage().addContains(_file)) {
                Repo.getStage().rmEntryInAdd(_file);
            }
        }
        if (!Repo.currCommitSameFile(_file)) {
            if (Repo.stageNotContainSameFile(_file)) {
                Bolb bolb = new Bolb(_file);
                String uid = bolb.getUID();
                bolb.save();
                Repo.getStage().addToAdd(_file, uid);
            }
        }
        Repo.getStage().save();
    }

    /** The file need to be added. */
    private final String _file;

}
