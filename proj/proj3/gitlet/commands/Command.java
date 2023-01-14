package gitlet.commands;

import gitlet.Main;
import gitlet.repo.Repo;

import java.io.IOException;
import java.util.Arrays;

/**
 * This class is the abstract class of all commands, defines some
 * abstract function for all commands to implement.
 *
 * @author ryan ma
 */

public abstract class Command {

    /**
     * Constructor function with args and operands number.
     */
    Command(String[] args, int operandsNum) {
        _name = args[0];
        if (args.length > 1) {
            _operands = Arrays.copyOfRange(args, 1, args.length);
        }
        _operandsNum = operandsNum;
    }

    /**
     * Constructor function for checkout command with ARGS.
     */
    Command(String[] args) {
        _name = args[0];
        if (args.length > 1) {
            _operands = Arrays.copyOfRange(args, 1, args.length);
        }
    }

    /**
     * Check the number of operands correct or not.
     */
    void checkOperandsNum() {
        if (_operands == null) {
            if (_operandsNum != 0) {
                Main.exitWithError("Incorrect operands.");
            }
        } else {
            if (_operandsNum != _operands.length) {
                Main.exitWithError("Incorrect operands.");
            }
        }
    }

    /**
     * Check whether initialized.
     */
    void checkInitial() {
        if (!Repo.GITLET_FOLDER.exists()) {
            Main.exitWithError("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * Check operands correct or not.
     */
    abstract void checkOperands();

    /**
     * Run this command.
     */
    public abstract void run() throws IOException;

    /**
     * The name of this command.
     */
    protected String _name;

    /**
     * Operands of this command, got from args.
     */
    protected String[] _operands = null;

    /**
     * Expected number of operands of this command.
     */
    protected int _operandsNum;

}
