package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import static gitlet.Utils.*;

/**
 * Represents the Staging Area, storing files being added and removed.
 *
 * @author Vincent Ma
 */
public class Staging implements Serializable {
    /**
     * The file storing the staging object.
     */
    public static File STAGINGFILE = join(Repository.GITLET_DIR, "index");


    /**
     * Casting the filename of additional files in staging area
     * to their SHA1 code.
     */
    private final HashMap<String, String> additionalMap = new HashMap<>();

    /**
     * The set of the filename of removal files in staging area.
     */
    private final HashSet<String> removalSet = new HashSet<>();

    /**
     * Get Staging object from .gitlet/index.
     */
    public static Staging getCurStaging() {
        return readObject(STAGINGFILE, Staging.class);
    }

    /**
     * Stores an empty stage object to .gitlet/index.
     */
    public static void clearStaging() {
        new Staging().save();
    }

    public HashMap<String, String> getAdditionalMap() {
        return additionalMap;
    }

    public HashSet<String> getRemovalSet() {
        return removalSet;
    }

    /**
     * Add given FILE to Staging Area according SHA of it
     * in last commit, and current workplace.
     */
    public void addFile(String filename, String commitSha, String stagingSha, Blob blob) {
        // starts tracking again
        removalSet.remove(blob.getStoredFile().getName());

        String curSha = blob.getSha1();
        if (Objects.equals(stagingSha, curSha)) {
            // no changes in this file
            System.exit(0);
        } else if (Objects.equals(commitSha, curSha)) {
            // change back to last commit version
            additionalMap.remove(filename);
        } else {
            // continue to change
            additionalMap.put(filename, curSha);
            blob.save();
        }
    }

    /**
     * Removes file from additional if it's there.
     * Adds to removal and removes the file from working directory if
     * it's tracked in last commit.
     */
    public void rmFile(String filename) {
        String sha = additionalMap.remove(filename);

        Commit lastCommit = Commit.getProjectHeadCommit();
        if (lastCommit.getTrackedMap().containsKey(filename)) {
            removalSet.add(filename);
            restrictedDelete(filename);
        } else if (sha == null) {
            System.out.println("No reason to remove the file.");
        }
    }

    /**
     * Saves current Staging to .gitlet/INDEX.
     */
    public void save() {
        writeObject(STAGINGFILE, this);
    }
}
