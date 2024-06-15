package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Represents the HEAD of the project,
 * storing the branch name of the head.
 *
 * @author Vincent Ma
 */
public class Head {
    /**
     * The File storing the head of the project.
     */
    public static final File HEAD_FILE = join(
            Repository.GITLET_DIR, "HEAD");

    /**
     * Set the head of the project BRANCH NAME.
     */
    public static void setHead(String branchName) {
        writeContents(HEAD_FILE, branchName);
    }

    /**
     * Gets the branch name of the head.
     */
    public static String getHeadBranchName() {
        return readContentsAsString(HEAD_FILE);
    }
}
