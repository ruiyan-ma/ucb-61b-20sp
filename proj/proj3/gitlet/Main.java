package gitlet;

import gitlet.commands.*;
import gitlet.repo.Repo;

import java.io.IOException;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author ryan ma
 * <p>
 * This class is the main process of gitlet program. It's responsible for
 * analyzing the command and execute the corresponding methond in each command
 * class.
 */

public class Main {

    /**
     * The main process of gitlet with ARGS.
     */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
        chooseCommand(args);
        if (Repo.getStage() != null) {
            Repo.getStage().save();
        }
    }

    /**
     * Create object and run command with ARGS.
     * This function is for silly style-checker.
     */
    static void chooseCommand(String[] args) throws IOException {
        Command cmd;
        switch (args[0]) {
            case "init" -> {
                cmd = new Init(args);
                cmd.run();
            }
            case "add" -> {
                cmd = new Add(args);
                cmd.run();
            }
            case "commit" -> {
                cmd = new Commit(args);
                cmd.run();
            }
            case "rm" -> {
                cmd = new Rm(args);
                cmd.run();
            }
            case "log" -> {
                cmd = new Log(args);
                cmd.run();
            }
            case "global-log" -> {
                cmd = new GLog(args);
                cmd.run();
            }
            case "find" -> {
                cmd = new Find(args);
                cmd.run();
            }
            case "status" -> {
                cmd = new Status(args);
                cmd.run();
            }
            case "checkout" -> {
                cmd = new Checkout(args);
                cmd.run();
            }
            case "branch" -> {
                cmd = new Branch(args);
                cmd.run();
            }
            case "rm-branch" -> {
                cmd = new RmBranch(args);
                cmd.run();
            }
            case "reset" -> {
                cmd = new Reset(args);
                cmd.run();
            }
            case "merge" -> {
                cmd = new Merge(args);
                cmd.run();
            }
            default -> exitWithError("No command with that name exists.");
        }
    }

    /**
     * Prints out MESSAGE and exits with code 0.
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }


}
