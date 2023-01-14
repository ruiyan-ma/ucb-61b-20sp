package gitlet.commands;

import gitlet.Main;
import gitlet.objects.Bolb;

import java.io.IOException;

import static gitlet.Main.repo;

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
        fileName = operands[0];
    }

    @Override
    void checkOperands() {
        if (!repo.workFolder.checkExist(operands[0])) {
            Main.exitWithError("File does not exist.");
        }
    }

    @Override
    public void run() throws IOException {
        checkOperands();

        // if this file has been removed before, then remove it from addition map and removal set.
        if (repo.getStage().removalSet.contains(fileName)) {
            repo.getStage().additionMap.remove(fileName);
            repo.getStage().removalSet.remove(fileName);
        }

        // if the current commit has the same file with the working directory, remove it from addition map.
        if (repo.workFolder.compareFile(fileName, repo.getCurrCommit().getBolbUID(fileName))) {
            repo.getStage().additionMap.remove(fileName);
        } else {
            // if the stage doesn't contain the same file with the working directory, add it.
            if (!repo.workFolder.compareFile(fileName, repo.getStage().getBolbUid(fileName))) {
                // create a new bolb and save it.
                Bolb bolb = new Bolb(repo.workFolder.readFromFile(fileName));
                String uid = bolb.getUID();
                repo.objectFolder.save(bolb);
                repo.getStage().additionMap.put(fileName, uid);
            }
        }

        repo.getStage().save();
    }

    /**
     * The file need to be added.
     */
    private final String fileName;

}
