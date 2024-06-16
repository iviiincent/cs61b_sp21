package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *
 * @author Vincent Ma
 */
public class Commit implements Serializable {
    /**
     * The format used in the log printing.
     */
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
    /**
     * The message of this Commit.
     */
    private final String message;
    /**
     * The timestamp of this Commit.
     */
    private final Date date;
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
            date = new Date(0);
            trackedMap = new HashMap<>();
        } else {
            date = new Date();
            trackedMap = new HashMap<>(parents[0].trackedMap);
        }

        Staging staging = Staging.getCurStaging();
        HashMap<String, String> additional = staging.getAdditionalMap();
        HashSet<String> removal = staging.getRemovalSet();

        if (parents.length != 0 && additional.isEmpty() && removal.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        trackedMap.putAll(additional);
        for (String removalFileName : removal) {
            trackedMap.remove(removalFileName);
        }
        commitId = sha1(message, serialize(parents),
                serialize(date), serialize(trackedMap));
    }

    public static File getCommitFile(String commitId) {
        return join(Repository.COMMITS_DIR, commitId);
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

    /**
     * Gets one commit with given commit id.
     *
     * @return The commit with correct commit id, null if there is no such commit.
     */
    public static Commit getCommit(String commitId) {
        File file = join(Repository.COMMITS_DIR, commitId);
        if (file.isFile()) {
            return readObject(file, Commit.class);
        }
        return null;
    }

    /**
     * Return a list of all the commits ever created in the project.
     */
    public static List<Commit> getAllCommits() {
        List<String> commitIds = plainFilenamesIn(Repository.COMMITS_DIR);
        List<Commit> commits = new ArrayList<>();
        if (commitIds == null || commitIds.isEmpty()) {
            return commits;
        }
        for (String commitId : commitIds) {
            commits.add(getCommit(commitId));
        }
        return commits;
    }

    public HashMap<String, String> getTrackedMap() {
        return trackedMap;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
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
        File blob = join(Repository.COMMITS_DIR, commitId);
        writeObject(blob, this);
    }

    /**
     * Prints the log of current commit in given format.
     * If this commit is a merged commit, then there's one more line
     * indicating its parents.
     */
    public void printLog() {
        System.out.println("===");
        System.out.println("commit " + commitId);
        if (parents.length == 2) {
            // This is a merged commit.
            System.out.println("Merge: " + parents[0].commitId.substring(0, 7) +
                    " " + parents[1].commitId.substring(0, 7));
        }
        System.out.println("Date: " + dateFormat.format(date));
        System.out.println(message);
        System.out.println();
    }

    /**
     * @return Returns true if current commit has parent, otherwise return false.
     */
    public boolean hasParents() {
        return parents.length > 0;
    }
}
