package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;


/**
 * Represents a gitlet repository.
 *
 * @author Vincent Ma
 */
public class Repository {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * The directory where blobs of files are saved.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "blobs");

    /**
     * The directory where commits are saved.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

    /**
     * The heads directory, where branches are saved.
     */
    public static final File HEADS_DIR = join(GITLET_DIR, "heads");

    /* Main functions */

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit:
     * a commit that contains no files and has the commit message initial
     * commit (just like that, with no punctuation). It will have a single
     * branch: master, which initially points to this initial commit,
     * and master will be the current branch. The timestamp for this
     * initial commit will be 00:00:00 UTC, Thursday, 1 January 1970
     * in whatever format you choose for dates (this is called
     * “The (Unix) Epoch”, represented internally by the time 0.)
     * Since the initial commit in all repositories created by Gitlet
     * will have exactly the same content, it follows that all repositories
     * will automatically share this commit (they will all have the same
     * UID) and all commits in all repositories will trace back to it.
     */
    public static void init() {
        /*
         * Failure cases: If there is already a Gitlet version-control
         * system in the current directory, it should abort. It should
         * NOT overwrite the existing system with a new one. Should print
         * the error message A Gitlet version-control system already
         * exists in the current directory.
         */
        if (GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system already " + "exists in the current directory.");
            System.exit(0);
        }

        if (!GITLET_DIR.mkdir() || !OBJECTS_DIR.mkdir()
                || !COMMITS_DIR.mkdir() || !HEADS_DIR.mkdir()) {
            System.out.println("mkdir failed.");
            System.exit(0);
        }


        Staging staging = new Staging();
        staging.save();
        Head.setHead("master");

        Commit initCommit = new Commit("initial commit");
        initCommit.save();

        Branch branch = new Branch("master", initCommit.getCommitId());
        branch.save();
    }

    /**
     * Adds a copy of the file as it currently exists to the staging
     * area (see the description of the commit command). For this reason,
     * adding a file is also called staging the file for addition. Staging
     * an already-staged file overwrites the previous entry in the staging
     * area with the new contents. The staging area should be somewhere in
     * .gitlet. If the current working version of the file is identical to
     * the version in the current commit, do not stage it to be added, and
     * remove it from the staging area if it is already there (as can
     * happen when a file is changed, added, and then changed back to its
     * original version). The file will no longer be staged for removal
     * (see gitlet rm), if it was at the time of the command.
     */
    public static void add(String filename) {
        /*
         * Failure cases: If the file does not exist, print the error
         * message "File does not exist".
         * And exit without changing anything.
         */
        File addedFile = join(CWD, filename);
        if (!addedFile.isFile()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        Blob blob = new Blob(addedFile);

        Commit lastCommit = Commit.getProjectHeadCommit();
        HashMap<String, String> trackedMap = lastCommit.getTrackedMap();
        String commitSha = trackedMap.get(filename);

        Staging staging = Staging.getCurStaging();
        String stagingSha = staging.getAdditionalMap().get(filename);
        staging.addFile(filename, commitSha, stagingSha, blob);
        staging.save();
    }

    /**
     * Saves a snapshot of tracked files in the current commit and staging
     * area, so they can be restored at a later time, creating a new
     * commit. The commit is said to be tracking the saved files. By
     * default, each commit’s snapshot of files will be exactly the same
     * as its parent commit’s snapshot of files; it will keep versions of files exactly as they are, and not update them. A commit will only
     * update the contents of files it is tracking that have been staged
     * for addition at the time of commit, in which case the commit will
     * now include the version of the file that was staged instead of the
     * version it got from its parent. A commit will save and start
     * tracking any files that were staged for addition but were not
     * tracked by its parent. Finally, files tracked in the current commit
     * may be untracked in the new commit as a result being staged for
     * removal by the rm command (below).
     */
    public static void commit(String message) {
        /*
         * Failure cases: If no files have been staged, abort. Print the
         *  message "No changes added to the commit." Every commit must
         *  have a non-blank message. If it does not, print the error
         *  message "Please enter a commit message." It is not a failure
         *  for tracked files to be missing from the working directory or
         *  changed in the working directory. Just ignore everything
         *  outside the .gitlet directory entirely.
         */
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit lastCommit = Commit.getProjectHeadCommit();

        Commit commit = new Commit(message, lastCommit);
        Branch branch = new Branch(Head.getHeadBranchName(), commit.getCommitId());

        branch.save();
        commit.save();
        Staging.clearStaging();
    }

    /**
     * Unstage the file if it is currently staged for addition. If the
     * file is tracked in the current commit, stage it for removal and
     * remove the file from the working directory if the user has not
     * already done so (do not remove it unless it is tracked in the
     * current commit).
     */
    public static void rm(String filename) {
        /*
         * Failure cases: If the file is neither staged nor tracked by the
         *  head commit, print the error message "No reason to remove the
         *  file."
         */
        Staging staging = Staging.getCurStaging();
        staging.rmFile(filename);
        staging.save();
    }

    /**
     * Starting at the current head commit, display information about each
     * commit backwards along the commit tree until the initial commit,
     * following the first parent commit links, ignoring any second
     * parents found in merge commits. (In regular Git, this is what you
     * get with "git log --first-parent"). This set of commit nodes is
     * called the commit’s history. For every node in this history, the
     * information it should display is the commit id, the time the commit
     * was made, and the commit message.
     */
    public static void log() {
        Commit commit = Commit.getProjectHeadCommit();
        while (commit.hasParents()) {
            commit.printLog();
            commit = commit.getParents()[0];
        }
        // Also prints the initial commit.
        commit.printLog();
    }

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter.
     */
    public static void global_log() {
        for (Commit commit : Commit.getAllCommits()) {
            commit.printLog();
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message,
     * one per line. If there are multiple such commits, it prints the ids
     * out on separate lines. The commit message is a single operand; to
     * indicate a multiword message, put the operand in quotation marks,
     * as for the commit command below.
     */
    public static void find(String message) {
        /*
         * Failure cases: If no such commit exists, prints the error
         * message "Found no commit with that message."
         */
        List<Commit> commits = Commit.getAllCommits();
        boolean found = false;
        for (Commit commit : commits) {
            if (Objects.equals(message, commit.getMessage())) {
                System.out.println(commit.getCommitId());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Displays what branches currently exist, and marks the current
     * branch with a *. Also displays what files have been staged
     * for addition or removal.
     */
    public static void status() {
        StringBuilder builder = new StringBuilder("=== Branches ===\n");
        String headBranch = Head.getHeadBranchName();
        List<String> branchesName = Branch.getAllBranchesName();
        builder.append("*").append(headBranch).append("\n");
        for (String branchName : branchesName) {
            if (!Objects.equals(headBranch, branchName)) {
                builder.append(branchName).append("\n");
            }
        }

        Staging staging = Staging.getCurStaging();
        HashMap<String, String> trackedMap =
                Commit.getProjectHeadCommit().getTrackedMap();
        HashMap<String, String> additionalMap = staging.getAdditionalMap();
        HashSet<String> removalSet = staging.getRemovalSet();
        List<String> wdFilesName = plainFilenamesIn(CWD);
        if (wdFilesName == null) {
            System.out.println("\n=== Staged Files ===\n" +
                    "\n=== Removed Files ===\n" +
                    "\n=== Modifications Not Staged For Commit ===\n" +
                    "\n=== Untracked Files ===\n");
            System.exit(0);
        }
        HashMap<String, Blob> wdBlobs = new HashMap<>();
        for (String filename : wdFilesName) {
            Blob blob = new Blob(join(Repository.CWD, filename));
            wdBlobs.put(filename, blob);
        }

        builder.append("\n=== Staged Files ===\n");
        for (String filename : additionalMap.keySet()) {
            builder.append(filename).append("\n");
        }

        builder.append("\n=== Removed Files ===\n");
        for (String filename : removalSet) {
            builder.append(filename).append("\n");
        }

        builder.append("\n=== Modifications Not Staged For Commit ===\n");
        HashSet<String> allFilesName = new HashSet<>(wdFilesName);
        allFilesName.addAll(trackedMap.keySet());
        for (String filename : allFilesName) {
            Blob blob = wdBlobs.get(filename);
            String trackedSha = trackedMap.get(filename);
            String stagedSha = additionalMap.get(filename);
            boolean isInWD = blob != null;
            boolean isTracked = trackedSha != null;
            boolean isStaged = stagedSha != null;
            if (isInWD && isTracked && !Objects.equals(trackedSha, blob.getSha1()) && !isStaged) {
                // Tracked in the current commit, changed in the working
                // directory, but not staged; or
                builder.append(filename).append("\n");
            } else if (isInWD && isStaged && !Objects.equals(stagedSha, blob.getSha1())) {
                // Staged for addition, but with different contents than
                // in the working directory; or
                builder.append(filename).append("\n");
            } else if (!isInWD && isStaged) {
                // Staged for addition, but deleted in the working
                // directory; or
                builder.append(filename).append("\n");
            } else if (!removalSet.contains(filename) && isTracked && !isInWD) {
                // Not staged for removal, but tracked in the current
                // commit and deleted from the working directory.
                builder.append(filename).append("\n");
            }
        }

        builder.append("\n=== Untracked Files ===\n");
        allFilesName = new HashSet<>(wdFilesName);
        allFilesName.addAll(removalSet);
        for (String filename : wdFilesName) {
            boolean isTracked = trackedMap.containsKey(filename);
            boolean isStaged = additionalMap.containsKey(filename);
            boolean isInWD = wdFilesName.contains(filename);
            if (isInWD && !isStaged && !isTracked) {
                // In the working directory but neither staged for
                // addition nor tracked.
                builder.append(filename).append("\n");
            } else if (removalSet.contains(filename) && isInWD) {
                // Staged for removal, but then re-created without
                // Gitlet’s knowledge
                builder.append(filename).append("\n");
            }
        }
        System.out.println(builder);
    }
}
