package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static gitlet.Utils.*;


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
        checkInitialized();
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
     * as its parent commit’s snapshot of files; it will keep versions of
     * files exactly as they are, and not update them. A commit will only
     * update the contents of files it is tracking that have been staged
     * for addition at the time of commit, in which case the commit will
     * now include the version of the file that was staged instead of the
     * version it got from its parent. A commit will save and start
     * tracking any files that were staged for addition but were not
     * tracked by its parent. Finally, files tracked in the current commit
     * may be untracked in the new commit as a result being staged for
     * removal by the rm command (below).
     *
     * @return The id of the created commit.
     */
    public static String commit(String message) {
        /*
         * Failure cases: If no files have been staged, abort. Print the
         *  message "No changes added to the commit." Every commit must
         *  have a non-blank message. If it does not, print the error
         *  message "Please enter a commit message." It is not a failure
         *  for tracked files to be missing from the working directory or
         *  changed in the working directory. Just ignore everything
         *  outside the .gitlet directory entirely.
         */
        checkInitialized();
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
        return commit.getCommitId();
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
        checkInitialized();
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
        checkInitialized();
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
    public static void globalLog() {
        checkInitialized();
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
        checkInitialized();
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
     * Prints the status of gitlet.
     */
    public static void status() {
        checkInitialized();
        Branch.status();

        List<String> wdFilesName = plainFilenamesIn(CWD);
        Staging staging = Staging.getCurStaging();
        HashMap<String, String> trackedMap =
                Commit.getProjectHeadCommit().getTrackedMap();
        HashMap<String, String> additionalMap = staging.getAdditionalMap();
        HashSet<String> removalSet = staging.getRemovalSet();
        HashMap<String, Blob> wdBlobs = new HashMap<>();
        if (wdFilesName != null) {
            for (String filename : wdFilesName) {
                wdBlobs.put(
                        filename,
                        new Blob(join(Repository.CWD, filename)));
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("=== Staged Files ===\n");
        for (String filename : additionalMap.keySet()) {
            builder.append(filename).append("\n");
        }

        builder.append("\n=== Removed Files ===\n");
        for (String filename : removalSet) {
            builder.append(filename).append("\n");
        }

        builder.append("\n=== Modifications Not Staged For Commit ===\n");
        List<String> modifiedFiles = Staging.getModifiedFiles(
                wdFilesName, trackedMap, wdBlobs, additionalMap, removalSet
        );
        for (String filename : modifiedFiles) {
            builder.append(filename).append("\n");
        }

        builder.append("\n=== Untracked Files ===\n");
        List<String> untrackedFiles = Staging.getUntrackedFiles(
                wdFilesName, trackedMap, additionalMap, removalSet);
        for (String filename : untrackedFiles) {
            builder.append(filename).append("\n");
        }
        System.out.println(builder);
    }

    /**
     * Handle three types of checkout command.
     */
    public static void checkout(String... args) {
        checkInitialized();
        if (args.length == 3 && args[1].equals("--")) {
            // gitlet.Main checkout -- [file name]
            checkoutFile(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            // gitlet.Main checkout [commit id] -- [file name]
            checkoutFile(args[1], args[3]);
        } else if (args.length == 2) {
            // gitlet.Main checkout [branch name]
            checkoutBranch(args[1]);
        } else {
            receiveInvalidCommand();
        }
    }

    /**
     * gitlet.Main checkout [commit id] -- [file name]
     * <p>
     * Takes the version of the file as it exists in the commit
     * with the given id, and puts it in the working directory,
     * overwriting the version of the file that’s already there
     * if there is one. The new version of the file is not staged.
     */
    public static void checkoutFile(String commitId, String filename) {
        Commit commit = Commit.getCommit(commitId);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        HashMap<String, String> trackedMap = commit.getTrackedMap();
        String trackedSha = trackedMap.get(filename);
        if (trackedSha == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File blobFile = Blob.getBlobFile(trackedSha);
        File wroteFile = join(CWD, filename);
        writeContents(wroteFile, readContents(blobFile));
    }

    /**
     * gitlet.Main checkout -- [file name]
     * <p>
     * Takes the version of the file as it exists in the head
     * commit and puts it in the working directory, overwriting
     * the version of the file that’s already there if there is
     * one. The new version of the file is not staged.
     */
    public static void checkoutFile(String filename) {
        String commitId = Commit.getProjectHeadCommit().getCommitId();
        checkoutFile(commitId, filename);
    }

    // TODO: not checked yet

    /**
     * gitlet.Main checkout [branch name]
     * <p>
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the
     * versions of the files that are already there if they exist. Also,
     * at the end of this command, the given branch will now be
     * considered the current branch (HEAD). Any files that are tracked
     * in the current branch but are not present in the checked-out
     * branch are deleted. The staging area is cleared, unless the
     * checked-out branch is the current branch.
     */
    public static void checkoutBranch(String branchName) {
        checkInitialized();
        if (Objects.equals(Head.getHeadBranchName(), branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        Commit commit = Commit.getHeadCommit(branchName);
        if (commit == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        Staging staging = Staging.getCurStaging();
        List<String> wdFilesName = plainFilenamesIn(CWD);
        HashMap<String, String> trackedMap = commit.getTrackedMap();
        HashMap<String, String> additionalMap = staging.getAdditionalMap();
        HashSet<String> removalSet = staging.getRemovalSet();
        List<String> untrackedFiles = Staging.getUntrackedFiles(
                wdFilesName, trackedMap, additionalMap, removalSet
        );

        for (HashMap.Entry<String, String> entry : trackedMap.entrySet()) {
            String filename = entry.getKey();
            File writtenFile = join(CWD, filename);
            File blobFile = Blob.getBlobFile(entry.getValue());
            writeContents(writtenFile, readContents(blobFile));
        }

        Head.setHead(branchName);
        Staging.clearStaging();
    }

    /**
     * Creates a new branch with the given name, and points it at the
     * current head commit. A branch is nothing more than a name for
     * a reference (a SHA-1 identifier) to a commit node. This command
     * does NOT immediately switch to the newly created branch (just as
     * in real Git).
     */
    public static void branch(String branchName) {
        List<String> branchNames = Branch.getAllBranchesName();
        if (branchNames.contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Branch branch = new Branch(
                branchName, Commit.getProjectHeadCommit().getCommitId());
        branch.save();
    }

    /* Helper functions */

    /**
     * Checks if the number of ARGS equals to VALID NUMS.
     * Quits if not valid.
     *
     * @param validNums Valid numbers of arguments.
     *                  Can be more than one valid number.
     */
    public static void validArgs(String[] args, int... validNums) {
        for (int n : validNums) {
            if (args.length == n) {
                return;
            }
        }
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

    /**
     * Has to run this when command is invalid.
     */
    public static void receiveInvalidCommand() {
        System.out.println("No command with that name exists.");
        System.exit(0);
    }

    /**
     * Quits when it's not initialized yet, otherwise continues to run.
     */
    public static void checkInitialized() {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
