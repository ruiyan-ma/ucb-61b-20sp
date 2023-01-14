package gitlet.objects;

import java.io.Serializable;

/**
 * This class is the abstract class of bolbs and commits.
 * It's responsible for creating dirs and files, reading and writing
 * operations.
 *
 * @author ryan ma
 */

public abstract class GitletObject implements Serializable {

    /**
     * An empty constructor function.
     */
    GitletObject() {

    }

    /**
     * Return the sha1 code of this object.
     */
    public abstract String getUID();
}
