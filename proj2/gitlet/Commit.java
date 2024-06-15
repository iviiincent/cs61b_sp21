package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *
 * @author Vincent Ma
 */
public class Commit implements Serializable {
    /**
     * The message of this Commit.
     */
    private final String message;

    /**
     * The timestamp of this Commit.
     */
    private final Date timestamp;

    /**
     * The parent(s) of this Commit
     */
    private final Commit[] parents;

    /**
     * The sha1 code of this commit, same as the commit id of this Commit,
     * decided by its other variables.
     */
    private final String commitId;

    /**
     * Casts the tracked files' name to their sha1.
     */
    private final HashMap<String, String> trackedMap;

    /**
     * Creates a Commit by given MESSAGE and PARENTS.
     * Merge the trackedMap of last commit and staging area
     * to trackedMap of this commit.
     *
     * @param parents The number of PARENTS can be 0, 1 and 2.
     *                If it's 0, the commit is an initial commit.
     */
    public Commit(String message, Commit... parents) {
        this.message = message;
        this.parents = parents;
        if (parents.length == 0) {
            // initial commit
            timestamp = new Date(0);
            trackedMap = new HashMap<>();
        } else {
            timestamp = new Date();
            trackedMap = new HashMap<>(parents[0].trackedMap);
        }

        Staging staging = Staging.getCurStaging();
        HashMap<String, String> additional = staging.getAdditional();
        HashSet<String> removal = staging.getRemoval();

        if (parents.length != 0 && additional.isEmpty() && removal.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        trackedMap.putAll(additional);
        for (String removalFileName : removal) {
            trackedMap.remove(removalFileName);
        }
        commitId = sha1(message, serialize(parents),
                serialize(timestamp), serialize(trackedMap));
    }

    public static File getCommitFile(String commitId) {
        return join(Repository.OBJECTS_DIR, commitId);
    }

    /**
     * Gets the head commit of given branch.
     */
    public static Commit getHeadCommit(String branchName) {
        File branchFile = Branch.getBranchFile(branchName);
        String commitId = readContentsAsString(branchFile);
        File lastCommitFile = Commit.getCommitFile(commitId);
        return readObject(lastCommitFile, Commit.class);
    }

    /**
     * Gets the head commit of head of the project.
     */
    public static Commit getProjectHeadCommit() {
        return getHeadCommit(readContentsAsString(Head.HEAD_FILE));
    }

    public HashMap<String, String> getTrackedMap() {
        return trackedMap;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCommitId() {
        return commitId;
    }

    public Commit[] getParents() {
        return parents;
    }

    /**
     * Saves this Commit to /.gitlet/objects/ID.
     */
    public void save() {
        File blob = join(Repository.OBJECTS_DIR, commitId);
        writeObject(blob, this);
    }
}
