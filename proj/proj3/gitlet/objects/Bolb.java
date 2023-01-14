package gitlet.objects;

import gitlet.repo.Repo;
import gitlet.Utils;

/**
 * This class stores the content of a file. The content of a file
 * at a particular time is called bolb. Bolb objects are stored
 * in .gitlet/objects directory.
 * A new bolb should only be created when we run the add command.
 *
 * @author ryan ma
 */

public class Bolb extends GitletObject {

    /**
     * Constructor function with FILENAME.
     */
    public Bolb(String content) {
        this.content = content;
    }

    /**
     * Return the content of this bolb.
     */
    public String getContent() {
        return content;
    }

    @Override
    public String getUID() {
        return Utils.sha1(content);
    }

    /**
     * The contents of this file.
     */
    private final String content;
}
