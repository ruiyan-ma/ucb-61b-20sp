package gitlet.objects;

import gitlet.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Main.repo;

/**
 * This class defines the components of a commit.
 * All pointers are instead of UID.
 *
 * @author ryan ma
 */

public class CommitData extends GitletObject {

    /**
     * Init constructor function with log message.
     * This constructor has no parent, means that it's the root of
     * commits tree, and its timestamp needs to be initial time.
     */
    public CommitData(String logMessage) {
        timestamp = new Date(0);
        this.logMessage = logMessage;
        fileMap = new TreeMap<>();
    }

    /**
     * Constructor function with log message, parent uid and the stage.
     */
    public CommitData(String logMessage, String parentUID, Stage stage) {
        timestamp = new Date();
        this.logMessage = logMessage;
        _parentUID = parentUID;
        CommitData parentCommit = repo.objectFolder.getCommit(parentUID);

        assert parentCommit != null;
        fileMap = new TreeMap<>(parentCommit.fileMap);
        fileMap.putAll(stage.additionMap);
        for (String fileName : stage.removalSet) {
            fileMap.remove(fileName);
        }
    }

    /**
     * Constructor function with log message, parent uid, second parent uid and stage.
     */
    public CommitData(String logMessage, String parentUID, String secParentUID, Stage stage) {
        timestamp = new Date();
        this.logMessage = logMessage;
        _parentUID = parentUID;
        _secParentUID = secParentUID;
        CommitData parentCommit = repo.objectFolder.getCommit(parentUID);

        assert parentCommit != null;
        fileMap = new TreeMap<>(parentCommit.fileMap);
        fileMap.putAll(stage.additionMap);
        for (String file : stage.removalSet) {
            fileMap.remove(file);
        }
    }

    /**
     * Return true if this commit contains file FILENAME.
     */
    public boolean containsFile(String fileName) {
        return fileMap.containsKey(fileName);
    }

    /**
     * Return the UID of the file FILENAME.
     */
    public String getBolbUid(String fileName) {
        return fileMap.get(fileName);
    }

    /**
     * Return true if this commit has parent.
     */
    public boolean hasParent() {
        return _parentUID != null;
    }

    /**
     * Return parent.
     */
    public CommitData getParent() {
        if (hasParent()) {
            return repo.objectFolder.getCommit(_parentUID);
        }
        return null;
    }

    /**
     * Return true if this commit has a second parent.
     */
    public boolean hasSecParent() {
        return _secParentUID != null;
    }

    /**
     * Return second parent.
     */
    public CommitData getSecParent() {
        if (hasSecParent()) {
            return repo.objectFolder.getCommit(_secParentUID);
        }
        return null;
    }

    /**
     * Return the log message of this commit.
     */
    public String getMessage() {
        return logMessage;
    }

    /**
     * Return the timestamp of this commit.
     */
    private String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd hh:mm:ss yyyy Z");
        return dateFormat.format(timestamp);
    }

    /**
     * Return a sorted set of all files in this commit.
     */
    public Set<String> getAllFileName() {
        return fileMap.keySet();
    }

    /**
     * Return log of this commit.
     */
    public String getLog() {
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

    @Override
    public String getUID() {
        StringBuilder content = new StringBuilder(timestamp.toString());
        content.append(logMessage);
        if (_parentUID != null) {
            content.append(_parentUID);
        }
        if (_secParentUID != null) {
            content.append(_secParentUID);
        }
        for (String file : fileMap.keySet()) {
            content.append(file).append(fileMap.get(file));
        }
        return Utils.sha1(content.toString());
    }

    /**
     * Return a queue of all history commits of the given commit.
     */
    public Queue<CommitData> getHistoryCommit() {
        /* Use BFS algorithm to retrieve commit tree. */
        Queue<CommitData> queue = new LinkedList<>();
        Queue<CommitData> history = new LinkedList<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            CommitData commit = queue.poll();
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

    /**
     * Return false if this commit contains the same file UID with the given commit.
     * If they both don't contain this file, then return false.
     */
    private boolean notContainSameFile(CommitData commit, String fileName) {
        if (commit.containsFile(fileName) && containsFile(fileName)) {
            return !fileMap.get(fileName).equals(commit.getBolbUid(fileName));
        } else {
            return (commit.containsFile(fileName) || containsFile(fileName));
        }
    }

    /**
     * Return the intersection set of this commit and the given commit.
     */
    public Set<String> getInterSet(CommitData commit) {
        Set<String> intersection = new HashSet<>(fileMap.keySet());
        intersection.retainAll(commit.getAllFileName());
        return intersection;
    }

    /**
     * Return the asymmetric difference set of this commit and the given commit.
     */
    public Set<String> getDiffSet(CommitData commit) {
        Set<String> difference = new HashSet<>(fileMap.keySet());
        difference.removeAll(commit.getAllFileName());
        return difference;
    }

    /**
     * Return the different files set in intersection of this commit and the given commit.
     */
    public Set<String> getInterDiffFiles(CommitData commit) {
        Set<String> intersection = getInterSet(commit);
        Set<String> interDiffFiles = new HashSet<>();
        for (String fileName : intersection) {
            if (notContainSameFile(commit, fileName)) {
                interDiffFiles.add(fileName);
            }
        }
        return interDiffFiles;
    }

    /**
     * Return an asymmetric different files set between this commit and the other commit,
     * including files that only exist in the other commit but not in this commit.
     */
    public Set<String> getDiffFiles(CommitData commit) {
        Set<String> diffFiles = new HashSet<>();
        Set<String> others = commit.getAllFileName();
        for (String fileName : others) {
            if (notContainSameFile(commit, fileName)) {
                diffFiles.add(fileName);
            }
        }
        return diffFiles;
    }

    /**
     * The timestamp of this commit.
     */
    private final Date timestamp;

    /**
     * The log message of this commit.
     */
    private final String logMessage;

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
    private final TreeMap<String, String> fileMap;
}
