package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author ryan ma
 *  Who is the author of this stupid git? Me! :)
 *  While actually I even don't know how to design it, though it's stupid.
 *  I will get an F on 61b. Wonderful! :)
 *
 *  This class is the main process of gitlet program. It's responsible for
 *  analyzing the command and execute the corresponding methond in each command
 *  class.
 */

public class Main {

    /** The main process of gitlet with ARGS. */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
        chooseCommand(args);
        if (Repo.getStage() != null) {
            Repo.getStage().save();
        }
    }

    /** Create object and run command with ARGS.
     *  This function is for silly style-checker. */
    static void chooseCommand(String[] args) throws IOException {
        Command cmd;
        switch (args[0]) {
        case "init":
            cmd = new Init(args);
            cmd.run();
            break;
        case "add":
            cmd = new Add(args);
            cmd.run();
            break;
        case "commit":
            cmd = new Commit(args);
            cmd.run();
            break;
        case "rm":
            cmd = new Rm(args);
            cmd.run();
            break;
        case "log":
            cmd = new Log(args);
            cmd.run();
            break;
        case "global-log":
            cmd = new GLog(args);
            cmd.run();
            break;
        case "find":
            cmd = new Find(args);
            cmd.run();
            break;
        case "status":
            cmd = new Status(args);
            cmd.run();
            break;
        case "checkout":
            cmd = new Checkout(args);
            cmd.run();
            break;
        case "branch":
            cmd = new Branch(args);
            cmd.run();
            break;
        case "rm-branch":
            cmd = new RmBranch(args);
            cmd.run();
            break;
        case "reset":
            cmd = new Reset(args);
            cmd.run();
            break;
        case "merge":
            cmd = new Merge(args);
            cmd.run();
            break;
        default:
            exitWithError("No command with that name exists.");
        }
    }

    /** Prints out MESSAGE and exits with code 0. */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }


}
