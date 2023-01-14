package gitlet.commands;

import gitlet.Main;

import java.io.IOException;

import static gitlet.Main.repo;

/**
 * This class is the rm command class.
 *
 * @author ryan ma
 */

public class Rm extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Rm(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        _file = operands[0];
    }

    @Override
    void checkOperands() {

    }

    @Override
    public void run() throws IOException {
        checkOperands();
        boolean addContain = repo.getStage().additionMap.containsKey(_file);
        boolean commitContain = repo.getCurrCommit().containsFile(_file);

        if (addContain) {
            repo.getStage().additionMap.remove(_file);
        }

        if (commitContain) {
            repo.getStage().removalSet.add(_file);
            repo.workFolder.deleteFile(_file);
        }

        if (!addContain && !commitContain) {
            Main.exitWithError("No reason to remove the file.");
        }

        repo.getStage().save();
    }

    /**
     * Removed file.
     */
    private final String _file;

}
