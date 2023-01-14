package gitlet.repo;

import gitlet.Utils;
import gitlet.objects.Bolb;
import gitlet.objects.CommitData;
import gitlet.objects.GitletException;
import gitlet.objects.GitletObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class represents the object dir in .gitlet.
 *
 * @author ryan ma
 */

public class ObjectFolder extends Folder {

    /**
     * Folder for bolbs and commits.
     */
    public static final String FOLDER_NAME = ".gitlet/objects";

    /**
     * The length of full UID.
     */
    static final int LENGTH = 40;

    public ObjectFolder() {
        super(FOLDER_NAME);
    }

    /**
     * Get an object file by its UID.
     *
     * @param uid: the uid of this object file.
     * @return the object file.
     */
    private File getObjectFile(String uid) {
        if (uid.length() == LENGTH) {
            return getFileWithFullUID(uid);
        } else if (uid.length() < LENGTH) {
            return getFileWithPrefix(uid);
        } else {
            throw new GitletException("Wrong UID.");
        }
    }

    /**
     * Return path to a GitletObject with full UID.
     */
    private File getFileWithFullUID(String uid) {
        File dir = Utils.join(FOLDER_NAME, uid.substring(0, 2));
        return Utils.join(dir, uid.substring(2));
    }

    /**
     * Return path to a GitletObject with prefix UID.
     */
    private File getFileWithPrefix(String uid) {
        if (uid.length() < 3) {
            throw new GitletException("UID too short.");
        }

        String filePrefix = uid.substring(2);
        String fileName = filePrefix;

        File dir = Utils.join(FOLDER_NAME, uid.substring(0, 2));
        List<String> fileNames = Utils.plainFilenamesIn(dir);
        assert fileNames != null;

        for (String name : fileNames) {
            if (name.substring(0, uid.length() - 2).equals(filePrefix)) {
                fileName = name;
                break;
            }
        }

        return Utils.join(dir, fileName);
    }

    /**
     * Return the bolb in the given commit.
     */
    public Bolb getBolb(CommitData commit, String fileName) {
        String uid = commit.getBolbUID(fileName);
        File path = getObjectFile(uid);
        return Utils.readObject(path, Bolb.class);
    }

    /**
     * Return the commit with UID.
     */
    public CommitData getCommit(String uid) {
        if (containsCommit(uid)) {
            File path = getObjectFile(uid);
            return Utils.readObject(path, CommitData.class);
        }
        return null;
    }

    /**
     * Return true if commit with sha1 code UID exists in this repository.
     */
    public boolean containsCommit(String uid) {
        return getObjectFile(uid).exists();
    }

    /**
     * Write file for bolb and commit objects.
     * Since bolb and commit only create new files and write
     * objects into files, they don't change any file, so if
     * this UID has already exists, do nothing. Only write when
     * this UID doesn't exist.
     */
    public void save(GitletObject obj) throws IOException {
        String uid = obj.getUID();
        File dir = Utils.join(FOLDER_NAME, uid.substring(0, 2));
        File file = Utils.join(dir, uid.substring(2));

        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!file.exists()) {
            file.createNewFile();
            Utils.writeObject(file, obj);
        }
    }
}
