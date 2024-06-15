package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;

/**
 * Represents a branch in the project.
 *
 * @author Vincent Ma
 */
public class Branch {
    /**
     * The file storing a branch in .gitlet/heads.
     * Its content is the id of the head commit of this branch.
     */
    private static File branchFile;

    /**
     * The name of this branch, as the name of branch file.
     */
    private final String branchName;

    /**
     * The head commit id of this branch, as the content of branch file.
     */
    private String headCommitId;

    public Branch(String branchName, String commitId) {
        this.branchName = branchName;
        this.headCommitId = commitId;
        branchFile = join(Repository.HEADS_DIR, branchName);
    }


    /**
     * Return all branches' name.
     *
     * @return The names of all branches in this project.
     */
    public static List<String> getAllBranches() {
        return plainFilenamesIn(Repository.HEADS_DIR);
    }

    public static File getBranchFile(String branchName) {
        return join(Repository.HEADS_DIR, branchName);
    }

    public String getBranchName() {
        return branchName;
    }

    public String getHeadCommitId() {
        return headCommitId;
    }

    public void setHeadCommitId(String headCommitId) {
        this.headCommitId = headCommitId;
    }

    /**
     * Saves current branch in .gitlet/heads/[BRANCH NAME].
     */
    public void save() {
        writeContents(branchFile, headCommitId);
    }
}
