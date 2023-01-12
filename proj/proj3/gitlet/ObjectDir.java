package gitlet;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

/** This class represents the object dir in .gitlet.
 *  @author ryan ma */

class ObjectDir extends Dir {

    /** Folder for bolbs and commits. */
    static final File OBJECTS_FOLDER = new File(".gitlet/objects");

    /** The length of full UID. */
    static final int LENGTH = 40;

    @Override
    File getFolder() {
        return OBJECTS_FOLDER;
    }

    @Override
    protected File getPathOfFile(String uid) {
        if (uid.length() == LENGTH) {
            return getPathWithFullUID(uid);
        } else if (uid.length() < LENGTH) {
            return getPathWithPrefix(uid);
        } else {
            throw new GitletException("Wrong UID.");
        }
    }

    /** Return path to a GitletObject with full UID. */
    private File getPathWithFullUID(String uid) {
        File dir = Utils.join(OBJECTS_FOLDER, uid.substring(0, 2));
        return Utils.join(dir, uid.substring(2));
    }

    /** Return path to a GitletObject with prefix UID. */
    private File getPathWithPrefix(String uid) {
        if (uid.length() < 3) {
            throw new GitletException("UID too short.");
        }
        String fileName = uid.substring(2);
        File dir = Utils.join(OBJECTS_FOLDER, uid.substring(0, 2));
        String name = uid.substring(2);
        List<String> objects = Utils.plainFilenamesIn(dir);
        assert objects != null;
        for (String object : objects) {
            if (object.substring(0, uid.length() - 2).equals(fileName)) {
                name = object;
                break;
            }
        }
        return Utils.join(dir, name);
    }

    /** Return the bolb with UID. */
    Bolb getBolb(String uid) {
        return (Bolb) Bolb.readFromFile(uid);
    }

    /** Return the bolb of file FILENAME in commit COMMIT. */
    Bolb getBolbInCommit(CommitData commit, String fileName) {
        return getBolb(commit.getBolbUID(fileName));
    }

    /** Return the commit with UID. */
    CommitData getCommit(String uid) {
        if (containsCommit(uid)) {
            return (CommitData) CommitData.readFromFile(uid);
        }
        return null;
    }

    /** Return true if commit with sha1 code UID exists in this repository. */
    boolean containsCommit(String uid) {
        return checkExist(uid);
    }

    /** Return true if commit COMMIT contains file FILENAME. */
    boolean commitContainsFile(CommitData commit, String fileName) {
        return commit.containFile(fileName);
    }

    /** Return true if commit with sha1 code COMMITUID contains file
     *  FILENAME. */
    boolean commitContainsFile(String commitUID, String fileName) {
        CommitData commit = getCommit(commitUID);
        assert commit != null;
        return commitContainsFile(commit, fileName);
    }

    /** Return a queue of all history commits of commit COMMITDATA. */
    Queue<CommitData> getHistoryOfCommit(CommitData commitData) {
        /* Use BFS algorithm to retrieve commit tree. */
        Queue<CommitData> queue = new LinkedList<>();
        Queue<CommitData> history = new LinkedList<>();
        queue.add(commitData);
        while (!queue.isEmpty()) {
            CommitData commit = queue.remove();
            history.add(commit);
            if (commit.hasParent()) {
                queue.add(commit.getParent());
            }
            if (commit.hasSecParent()) {
                queue.add(commit.getSecParent());
            }
        }
        return history;
    }

    /** Return a queue of all history commits of commit with UID. */
    Queue<CommitData> getHistoryOfCommit(String uid) {
        return getHistoryOfCommit(getCommit(uid));
    }

}
