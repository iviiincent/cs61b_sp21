package gitlet;

import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.join;


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
     * The objects directory, where blobs of files and commits are saved.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

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

        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        HEADS_DIR.mkdir();

        Staging staging = new Staging();
        staging.save();
        Head.setHead("master");

        Commit initCommit = new Commit("init");
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
        String curSha = blob.getSha1();

        Commit lastCommit = Commit.getProjectHeadCommit();
        HashMap<String, String> trackedMap = lastCommit.getTrackedMap();
        String commitSha = trackedMap.get(filename);

        Staging staging = Staging.getCurStaging();
        String stagingSha = staging.getAdditional().get(filename);
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
         *  have a non-blank message. If it doesn’t, print the error
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

    }
}
