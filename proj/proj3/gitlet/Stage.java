package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** This class represents staging area in gitlet.
 *  It contains an addition area and a removal area.
 *  This class object will stored in .gitlet/index file.
 *  @author ryan ma
 *  */

class Stage implements Serializable {

    /** File for INDEX. */
    static final File INDEX_FILE = new File(".gitlet/index");

    /** The constructor function. */
    Stage() {
        _addition = new TreeMap<>();
        _removal = new TreeSet<>();
    }

    /** Add an addtion or a change in file FILENAME and its UID to _addition. */
    void addToAdd(String fileName, String uid) {
        _addition.put(fileName, uid);
    }

    /** Add an remove of file FILENAME to _removal. */
    void addToRm(String fileName) {
        _removal.add(fileName);
    }

    /** Return whether _addition contains FILENAME. */
    boolean addContains(String fileName) {
        return _addition.containsKey(fileName);
    }

    /** Return whether _removal contains FILENAME. */
    boolean rmContains(String fileName) {
        return _removal.contains(fileName);
    }

    /** Remove the entry FILENAME in _addition. */
    void rmEntryInAdd(String fileName) {
        _addition.remove(fileName);
    }

    /** Remove the entry FILENAME in _removal. */
    void rmEntryInRm(String fileName) {
        _removal.remove(fileName);
    }

    /** Return the UID of the file FILENAME. */
    String getUID(String fileName) {
        return _addition.get(fileName);
    }

    /** Return a sorted set of all files in _addition. */
    Set<String> getFilesInAdd() {
        return _addition.keySet();
    }

    /** Return true if the file FILENAME in staging area has the
     *  same UID with UID. */
    boolean compareFile(String fileName, String uid) {
        if (!addContains(fileName)) {
            return false;
        }
        return getUID(fileName).equals(uid);
    }

    /** Return _addition. */
    TreeMap<String, String> getAddition() {
        return _addition;
    }

    /** Return _removal. */
    TreeSet<String> getRemoval() {
        return _removal;
    }

    /** Return true if _addition is empty. */
    boolean isAddEmpty() {
        return _addition.isEmpty();
    }

    /** Return true if _removal is empty. */
    boolean isRmEmpty() {
        return _removal.isEmpty();
    }

    /** Clean the staging area. */
    void clean() {
        _addition.clear();
        _removal.clear();
    }

    /** Return the object. Reads in and deserializes the index file. */
    static Stage readFromFile() {
        return Utils.readObject(INDEX_FILE, Stage.class);
    }

    /** Write Stage object into file index. */
    void save() throws IOException {
        if (!INDEX_FILE.exists()) {
            INDEX_FILE.createNewFile();
        }
        Utils.writeObject(INDEX_FILE, this);
    }

    /** Staged for addition, map of file name to the corresponding
     *  bolb's UID. */
    private final TreeMap<String, String> _addition;

    /** Staged for removal, store the file name in this set. */
    private final TreeSet<String> _removal;

}
