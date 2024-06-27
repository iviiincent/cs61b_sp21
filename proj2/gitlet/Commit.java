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
public class Commit implements Serializable, Dumpable {
    /**
     * The initial commit for the repo.
     */
    public static final Commit INIT_COMMIT = new Commit("initial commit");


    /**
     * The format used in the log printing.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "EEE MMM d HH:mm:ss yyyy Z");

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
    private final TreeMap<String, String> trackedMap;


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
            trackedMap = new TreeMap<>();
        } else {
            date = new Date();
            trackedMap = new TreeMap<>(parents[0].trackedMap);
        }

        Staging staging = Staging.getCurStaging();
        Map<String, String> additional = staging.getAdditionalMap();
        Set<String> removal = staging.getRemovalSet();

        if (parents.length != 0 && additional.isEmpty() && removal.isEmpty()) {
            Repository.exit("No changes added to the commit.");
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
     *
     * @return The head commit of given branch name,
     * and null if there's no such branch.
     */
    public static Commit getHeadCommit(String branchName) {
        File branchFile = Branch.getBranchFile(branchName);
        if (!branchFile.isFile()) {
            return null;
        }
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
     * @param commitId Its length can be 40, or less than 40.
     * @return The commit with correct commit id,
     * and null if there is no such commit.
     */
    public static Commit getCommit(String commitId) {
        int len = commitId.length();
        if (len < UID_LENGTH) { // short commit id
            List<String> ids = plainFilenamesIn(Repository.COMMITS_DIR);
            if (ids == null) {
                throw new NullPointerException("0 commit");
            }
            for (String id : ids) {
                if (Objects.equals(id.substring(0, len), commitId)) {
                    File file = join(Repository.COMMITS_DIR, id);
                    return readObject(file, Commit.class);
                }
            }
        }
        File file = join(Repository.COMMITS_DIR, commitId);
        return file.isFile() ? readObject(file, Commit.class) : null;
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

    /**
     * Asserts that the relationship between given two commits is ancestor and
     * child, returns the split commit of those.
     */
    public static Commit getSplitCommit(Commit a, Commit b) {
        List<List<Commit>> lla = bfsAncestors(a);
        List<Commit> lb = getAncestors(b);
        for (List<Commit> la : lla) {
            for (Commit ca : la) {
                if (lb.contains(ca)) {
                    return ca;
                }
            }
        }
        return INIT_COMMIT;
    }

    /**
     * Returns a list of commits, which are all ancestors of given commit.
     */
    public static List<Commit> getAncestors(Commit commit) {
        List<Commit> ancestors = new ArrayList<>();
        if (commit == null) {
            return ancestors;
        }
        Queue<Commit> que = new ArrayDeque<>();
        que.add(commit);
        while (!que.isEmpty()) {
            commit = que.peek();
            ancestors.add(commit);
            que.remove();
            que.addAll(List.of(commit.parents));
        }
        return ancestors;
    }

    /**
     * Returns a list of lists of commits, in the order of bfs in tree.
     */
    public static List<List<Commit>> bfsAncestors(Commit commit) {
        List<List<Commit>> bfs = new ArrayList<>();
        if (commit == null) {
            return bfs;
        }
        Queue<Commit> que = new ArrayDeque<>();
        que.add(commit);
        while (!que.isEmpty()) {
            int n = que.size();
            List<Commit> level = new ArrayList<>(n);
            for (int i = 0; i < n; ++i) {
                commit = que.peek();
                level.add(commit);
                que.remove();
                que.addAll(List.of(commit.parents));
            }
            bfs.add(level);
        }
        return bfs;
    }

    public Map<String, String> getTrackedMap() {
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

    @Override
    public int hashCode() {
        return commitId.hashCode();
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
            System.out.println("Merge: " + parents[0].commitId.substring(0, 7)
                    + " " + parents[1].commitId.substring(0, 7));
        }
        System.out.println("Date: " + DATE_FORMAT.format(date));
        System.out.println(message);
        System.out.println();
    }

    /**
     * @return Returns true if current commit has parent, otherwise return false.
     */
    public boolean hasParents() {
        return parents.length > 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Commit
                && Objects.equals(hashCode(), obj.hashCode());
    }

    @Override
    public String toString() {
        return message;
    }

    /**
     * Print useful information about this object on System.out.
     */
    @Override
    public void dump() {
        System.out.println(commitId);
        System.out.println(message);
        System.out.println(trackedMap.keySet());
    }
}
