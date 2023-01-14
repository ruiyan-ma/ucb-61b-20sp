package gitlet.objects;

import gitlet.Utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents staging area in gitlet.
 * It contains an addition area and a removal area.
 * This class object will store in .gitlet/index file.
 *
 * @author ryan ma
 */

public class Stage implements Serializable {

    /**
     * File for INDEX.
     */
    public static final File INDEX_FILE = new File(".gitlet/index");

    /**
     * The constructor function.
     */
    public Stage() {
        additionMap = new TreeMap<>();
        removalSet = new TreeSet<>();
    }

    public String getBolbUid(String fileName) {
        return additionMap.get(fileName);
    }

    /**
     * Clean the staging area.
     */
    public void clean() {
        additionMap.clear();
        removalSet.clear();
    }

    /**
     * Return the object. Reads in and deserializes the index file.
     */
    public static Stage readFromFile() {
        return Utils.readObject(INDEX_FILE, Stage.class);
    }

    /**
     * Write Stage object into file index.
     */
    public void save() throws IOException {
        if (!INDEX_FILE.exists()) {
            INDEX_FILE.createNewFile();
        }
        Utils.writeObject(INDEX_FILE, this);
    }

    /**
     * Staged for addition, map of file name to the corresponding bolb's UID.
     */
    public final TreeMap<String, String> additionMap;

    /**
     * Staged for removal, store the file name in this set.
     */
    public final TreeSet<String> removalSet;
}
