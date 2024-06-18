package gitlet;

import java.io.File;
import java.util.List;
import java.util.Objects;

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

    public static File getBranchFile(String branchName) {
        return join(Repository.HEADS_DIR, branchName);
    }

    public static List<String> getAllBranchesName() {
        return plainFilenamesIn(Repository.HEADS_DIR);
    }

    /**
     * Prints the branch part of the status.
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
        System.out.println(builder);
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
