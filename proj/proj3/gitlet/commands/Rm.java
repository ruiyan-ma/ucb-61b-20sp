package gitlet.commands;

import gitlet.Main;
import gitlet.repo.Repo;

import java.io.IOException;

/** This class is the rm command class.
 *  @author ryan ma
 *  */

public class Rm extends Command {

    /** Constructor function with ARGS. */
    public Rm(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _file = _operands[0];
    }

    @Override
    void checkOperands() {

    }

    @Override
    public void run() throws IOException {
        checkOperands();
        boolean addContain = Repo.getStage().additionMap.containsKey(_file);
        boolean commitContain = Repo.currCommitContainsFile(_file);

        if (addContain) {
            Repo.getStage().additionMap.remove(_file);
        }

        if (commitContain) {
            Repo.getStage().removalSet.add(_file);
            Repo.workFolder.deleteFile(_file);
        }

        if (!addContain && !commitContain) {
            Main.exitWithError("No reason to remove the file.");
        }

        Repo.getStage().save();
    }

    /** Removed file. */
    private final String _file;

}
