package gitlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class defines the components of a commit.
 * All pointers are instead of UID.
 *
 * @author ryan ma
 */

class CommitData extends GitletObject {

    /**
     * Init constructor function with LOGMESSAGE.
     * This constructor has no parent, means that its the root of
     * commits tree, and its timestamp needs to be initial time.
     */
    CommitData(String logMessage) {
        _timestamp = new Date(0);
        _logMessage = logMessage;
        _map = new TreeMap<>();
    }

    /**
     * Constructor function with LOGMESSAGE, PARENTUID and STAGE.
     */
    CommitData(String logMessage, String parentUID, Stage stage) {
        _timestamp = new Date();
        _logMessage = logMessage;
        _parentUID = parentUID;
        CommitData parentCommit = Repo.getObjectDir().getCommit(parentUID);
        assert parentCommit != null;
        _map = new TreeMap<>(parentCommit.getMap());
        TreeMap<String, String> addition = stage.getAddition();
        for (String file : addition.keySet()) {
            _map.put(file, addition.get(file));
        }
        for (String file : stage.getRemoval()) {
            _map.remove(file);
        }
    }

    /**
     * Constructor function with LOGMESSAGE, PARENTUID, SECPARENTUID
     * and STAGE.
     */
    CommitData(String logMessage, String parentUID,
               String secParentUID, Stage stage) {
        _timestamp = new Date();
        _logMessage = logMessage;
        _parentUID = parentUID;
        _secParentUID = secParentUID;
        CommitData parentCommit = Repo.getObjectDir().getCommit(parentUID);
        assert parentCommit != null;
        _map = new TreeMap<>(parentCommit.getMap());
        TreeMap<String, String> addition = stage.getAddition();
        for (String file : addition.keySet()) {
            _map.put(file, addition.get(file));
        }
        for (String file : stage.getRemoval()) {
            _map.remove(file);
        }
    }

    /**
     * Return true if this commit contains file FILENAME.
     */
    boolean containFile(String fileName) {
        return _map.containsKey(fileName);
    }

    /**
     * Return the UID of the file FILENAME.
     */
    String getBolbUID(String fileName) {
        return _map.get(fileName);
    }

    /**
     * Return true if this commit has parent.
     */
    boolean hasParent() {
        return _parentUID != null;
    }

    /**
     * Return parent.
     */
    CommitData getParent() {
        if (hasParent()) {
            return Repo.getObjectDir().getCommit(_parentUID);
        }
        return null;
    }

    /**
     * Return true if this commit has a second parent.
     */
    boolean hasSecParent() {
        return _secParentUID != null;
    }

    /**
     * Return second parent.
     */
    CommitData getSecParent() {
        if (hasSecParent()) {
            return Repo.getObjectDir().getCommit(_secParentUID);
        }
        return null;
    }

    /**
     * Return the log message of this commit.
     */
    String getMessage() {
        return _logMessage;
    }

    /**
     * Return the timestamp of this commit.
     */
    String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "E MMM dd hh:mm:ss yyyy Z");
        return dateFormat.format(_timestamp);
    }

    /**
     * Return a shallow copy of the TreeMap in this commit.
     */
    TreeMap<String, String> getMap() {
        return _map;
    }

    /**
     * Return a sorted set of all files in this commit.
     */
    Set<String> getAllFiles() {
        return _map.keySet();
    }

    /**
     * Return true if this commit contains the same Bolb UID for file
     * FILENAME.
     */
    boolean compareFile(String fileName, String uid) {
        if (containFile(fileName)) {
            return _map.get(fileName).equals(uid);
        }
        return false;
    }

    /**
     * Return false if this commit contains the same file FILENAME UID
     * with COMMIT.
     * If they both don't contain fileName, then return false.
     */
    boolean notContainSameFile(CommitData commit, String fileName) {
        if (commit.containFile(fileName) && containFile(fileName)) {
            return !compareFile(fileName, commit.getBolbUID(fileName));
        } else {
            return (commit.containFile(fileName) || containFile(fileName));
        }
    }

    /**
     * Return the intersection set of this commit and commit COMMIT.
     */
    Set<String> getInterSet(CommitData commit) {
        Set<String> intersection = new HashSet<>(_map.keySet());
        intersection.retainAll(commit.getAllFiles());
        return intersection;
    }

    /**
     * Return the asymmetric difference set of this commit and commit
     * COMMIT.
     */
    Set<String> getDiffSet(CommitData commit) {
        Set<String> difference = new HashSet<>(_map.keySet());
        difference.removeAll(commit.getAllFiles());
        return difference;
    }

    /**
     * Return the different files set in intersection of this commit and
     * commit COMMIT.
     */
    Set<String> getInterDiffFiles(CommitData commit) {
        Set<String> intersection = getInterSet(commit);
        Set<String> interDiffFiles = new HashSet<>();
        for (String file : intersection) {
            if (notContainSameFile(commit, file)) {
                interDiffFiles.add(file);
            }
        }
        return interDiffFiles;
    }

    /**
     * Return a asymmetric different files set between this commit and commit
     * COMMIT, including files that only exist in COMMIT but not in this
     * commit.
     */
    Set<String> getDiffFiles(CommitData commit) {
        Set<String> diffFiles = new HashSet<>();
        Set<String> other = commit.getAllFiles();
        for (String file : other) {
            if (notContainSameFile(commit, file)) {
                diffFiles.add(file);
            }
        }
        return diffFiles;
    }

    @Override
    String getUID() {
        StringBuilder content = new StringBuilder(_timestamp.toString());
        content.append(_logMessage);
        if (_parentUID != null) {
            content.append(_parentUID);
        }
        if (_secParentUID != null) {
            content.append(_secParentUID);
        }
        for (String file : _map.keySet()) {
            content.append(file).append(_map.get(file));
        }
        return Utils.sha1(content.toString());
    }

    /**
     * Return log of this commit.
     */
    String getLog() {
        StringBuilder log = new StringBuilder();
        log.append("===\n");
        log.append("commit ").append(getUID()).append("\n");
        if (_secParentUID != null) {
            log.append("Merge: ").append(_parentUID, 0, 7);
            log.append(" ").append(_secParentUID, 0, 7).append("\n");
        }
        log.append("Date: ").append(getTime()).append("\n");
        log.append(getMessage()).append("\n\n");
        return log.toString();
    }


    /**
     * The timestamp of this commit.
     */
    private final Date _timestamp;

    /**
     * The log message of this commit.
     */
    private final String _logMessage;

    /**
     * The UID to the primary parent of this commit.
     */
    private String _parentUID = null;

    /**
     * The UID to the second parent of this commit.
     */
    private String _secParentUID = null;

    /**
     * The map from file names to bolb UID.
     */
    private final TreeMap<String, String> _map;

}
