package gitlet.commands;

import gitlet.Main;
import gitlet.objects.Bolb;
import gitlet.repo.Repo;

import java.io.IOException;

/**
 * This class is the add command class.
 *
 * @author ryan ma
 */

public class Add extends Command {

    /**
     * Constructor function with ARGS.
     */
    public Add(String[] args) {
        super(args, 1);
        checkInitial();
        checkOperandsNum();
        fileName = _operands[0];
    }

    @Override
    void checkOperands() {
        if (!Repo.workFolder.checkExist(_operands[0])) {
            Main.exitWithError("File does not exist.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();

        // if this file has been removed before, then remove it from addition map and removal set.
        if (Repo.getStage().removalSet.contains(fileName)) {
            Repo.getStage().additionMap.remove(fileName);
            Repo.getStage().removalSet.remove(fileName);
        }

        // if the current commit has the same file with the working directory, remove it from addition map.
        if (Repo.currCommitSameFile(fileName)) {
            Repo.getStage().additionMap.remove(fileName);
        } else {
            // if the stage doesn't contain the same file with the working directory, add it.
            if (Repo.stageNotContainSameFile(fileName)) {
                // create a new bolb and save it.
                Bolb bolb = new Bolb(Repo.workFolder.readFromFile(fileName));
                String uid = bolb.getUID();
                Repo.objectFolder.save(bolb);
                Repo.getStage().additionMap.put(fileName, uid);
            }
        }

        Repo.getStage().save();
    }

    /**
     * The file need to be added.
     */
    private final String fileName;

}
