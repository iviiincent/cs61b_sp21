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
     * The file storing the info in Staging Area.
     */
    public static File STAGINGFILE = join(Repository.GITLET_DIR, "index");


    /**
     * Casting the filename of additional files in staging area
     * to their SHA1 code.
     */
    private final HashMap<String, String> additional = new HashMap<>();

    /**
     * The set of the filename of removal files in staging area.
     */
    private final HashSet<String> removal = new HashSet<>();

    /**
     * Creates a staging with empty additional and removal.
     */
    public Staging() {
    }

    /**
     * Get Staging object from .gitlet/index.
     */
    public static Staging getCurStaging() {
        return readObject(STAGINGFILE, Staging.class);
    }

    /**
     * Clears the staging area.
     */
    public static void clearStaging() {
        new Staging().save();
    }

    public HashMap<String, String> getAdditional() {
        return additional;
    }

    public HashSet<String> getRemoval() {
        return removal;
    }

    /**
     * Add given FILE to Staging Area according SHA of it
     * in last commit, and current workplace.
     */
    public void addFile(String filename, String commitSha, String stagingSha, Blob blob) {
        // starts tracking again
        removal.remove(blob.getStoredFile().getName());

        String curSha = blob.getSha1();
        if (Objects.equals(stagingSha, curSha)) {
            // no changes in this file
            System.exit(0);
        } else if (Objects.equals(commitSha, curSha)) {
            // change back to last commit version
            additional.remove(filename);
        } else {
            // continue to change
            additional.put(filename, curSha);
            blob.save();
        }
    }

    /**
     * Removes file from Staging Area.
     */
    public void rmFile(String filename) {

    }

    /**
     * Saves current Staging to .gitlet/INDEX.
     */
    public void save() {
        writeObject(STAGINGFILE, this);
    }
}
