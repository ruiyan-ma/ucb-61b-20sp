package capers;

import java.io.File;
import java.io.IOException;

/**
 * Canine Capers: A Gitlet Prelude.
 *
 * @author Sean Dooher
 */
public class Main {
    /**
     * Current Working Directory.
     */
    static final File CWD = new File(".");

    /**
     * Main metadata folder.
     */
    static final File CAPERS_FOLDER = new File(".capers");

    /**
     * Runs one of three commands:
     * <p>
     * story [text] -- Appends "text" + a newline to a story file in the
     * .capers directory. Additionally, prints out the current story.
     *
     * <p>dog [name] [breed] [age] -- Persistently creates a dog with the specified parameters; should
     * also print the dog's toString(). Assume dog names are unique.
     *
     * <p>birthday [name] -- Advances a dog's age persistently and prints out a celebratory message.
     *
     * <p>All persistent data should be stored in a ".capers" directory in the current working
     * directory.
     *
     * <p>Recommended structure (you do not have to follow):
     *
     * <p>*YOU SHOULD NOT CREATE THESE MANUALLY, YOUR PROGRAM SHOULD CREATE THESE FOLDERS/FILES*
     *
     * <p>.capers/ -- top level folder for all persistent data in your lab12 folder - dogs/ -- folder
     * containing all of the persistent data for dogs - story -- file containing the current story
     *
     * @param args arguments from the command line
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            exitWithError("Must have at least one argument");
        }
        setupPersistence();
        switch (args[0]) {
        case "story":
            writeStory(args);
            break;
        case "dog":
            makeDog(args);
            break;
        case "birthday":
            celebrateBirthday(args);
            break;
        default:
            exitWithError(String.format("Unknown command: %s", args[0]));
        }
    }

    /**
     * Does require filesystem operations to allow for persistence. (creates any necessary folders or
     * files) Remember: recommended structure (you do not have to follow):
     *
     * <p>.capers/ -- top level folder for all persistent data in your lab12 folder - dogs/ -- folder
     * containing all the persistent data for dogs - story -- file containing the current story
     */
    public static void setupPersistence() {
        CAPERS_FOLDER.mkdir();
        Dog.DOG_FOLDER.mkdir();
    }

    /**
     * Appends the first non-command argument in args to a file called `story` in the .capers
     * directory.
     *
     * @param args Array in format: {'story', text}
     */
    public static void writeStory(String[] args) throws IOException {
        validateNumArgs("story", args, 2);
        File story = Utils.join(CAPERS_FOLDER, args[0]);
        if (!story.exists()) {
            story.createNewFile();
        }
        String str = Utils.readContentsAsString(story) + args[1] + "\n";
        Utils.writeContents(story, str);
        System.out.println(str);
    }

    /**
     * Creates and persistently saves a dog using the first three non-command arguments of args (name,
     * breed, age). Also prints out the dog's information using toString().
     *
     * @param args Array in format: {'story', name, breed, age}
     */
    public static void makeDog(String[] args) throws IOException {
        validateNumArgs("dog", args, 4);
        File dogFile = Utils.join(Dog.DOG_FOLDER, args[1]);
        dogFile.createNewFile();
        Dog dog = new Dog(args[1], args[2], Integer.parseInt(args[3]));
        Utils.writeObject(dogFile, dog);
        System.out.println(dog.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message. Also prints out the
     * dog's information using toString(). Chooses dog to advance based on the first non-command
     * argument of args.
     *
     * @param args Array in format: {'birthday', name}
     */
    public static void celebrateBirthday(String[] args) {
        validateNumArgs("birthday", args, 2);
        Dog dog = Dog.fromFile(args[1]);
        dog.haveBirthday();
        dog.saveDog();
    }

    /**
     * Prints out MESSAGE and exits with error code -1. Note: The functionality for erroring/exit
     * codes is different within Gitlet so DO NOT use this as a reference. Refer to the spec for more
     * information.
     *
     * @param message message to print
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(-1);
    }

    /**
     * Checks the number of arguments versus the expected number, throws a RuntimeException if they do
     * not match.
     *
     * @param cmd  Name of command you are validating
     * @param args Argument array from command line
     * @param n    Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
