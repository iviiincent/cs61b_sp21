package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

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
    private static final File STAGINGFILE = join(Repository.GITLET_DIR, "index");

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

    /**
     * Gets a list of Modifications Not Staged For Commit in gitlet status.
     */
    public static List<String> getModifiedFiles(
            List<String> wdFilesName,
            HashMap<String, String> trackedMap,
            HashMap<String, Blob> wdBlobs,
            HashMap<String, String> additionalMap,
            HashSet<String> removalSet) {
        List<String> resFiles = new ArrayList<>();
        if (wdFilesName == null) {
            return resFiles;
        }

        HashSet<String> allFilesName = new HashSet<>(wdFilesName);
        allFilesName.addAll(trackedMap.keySet());
        for (String filename : allFilesName) {
            Blob blob = wdBlobs.get(filename);
            String trackedSha = trackedMap.get(filename);
            String stagedSha = additionalMap.get(filename);
            boolean isInWD = blob != null;
            boolean isTracked = trackedSha != null;
            boolean isStaged = stagedSha != null;

            boolean con = false;
            con |= isInWD && isTracked
                    && !Objects.equals(trackedSha, blob.getSha1())
                    && !isStaged;
            con |= isInWD && isStaged
                    && !Objects.equals(stagedSha, blob.getSha1());
            con |= !isInWD && isStaged;
            con |= !removalSet.contains(filename) && isTracked
                    && !isInWD;
            if (con) {
                resFiles.add(filename);
            }
        }
        return resFiles;
    }

    /**
     * Returns a list of untracked files.
     */
    public static List<String> getUntrackedFilesInStatus(
            List<String> wdFilesName,
            HashMap<String, String> trackedMap,
            HashMap<String, String> additionalMap,
            HashSet<String> removalSet
    ) {
        List<String> resFiles = new ArrayList<>();
        if (wdFilesName == null) {
            return resFiles;
        }
        HashSet<String> allFiles = new HashSet<>(wdFilesName);
        allFiles.addAll(removalSet);
        for (String filename : allFiles) {
            boolean isTracked = trackedMap.containsKey(filename);
            boolean isStaged = additionalMap.containsKey(filename);
            boolean isInWD = wdFilesName.contains(filename);

            boolean con = false;
            con |= isInWD && !isStaged && !isTracked;
            con |= removalSet.contains(filename) && isInWD;
            if (con) {
                resFiles.add(filename);
            }
        }
        return resFiles;
    }

    /**
     * @return If this staging objects is empty.
     */
    public boolean isEmpty() {
        return additionalMap.isEmpty() && removalSet.isEmpty();
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
            Repository.exit();
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
     * Directly add to additionalMap. Only used when blob file is stored.
     */
    public void addExisted(String filename, String sha) {
        if (Blob.getBlobFile(sha) == null) {
            throw new NullPointerException("Missing blob file.");
        }
        additionalMap.put(filename, sha);
    }

    /**
     * Unstage the file if it is currently staged for addition. If the file is
     * tracked in the current commit, stage it for removal and remove the file
     * from the working directory if the user has not already done so (do not
     * remove it unless it is tracked in the current commit).
     */
    public void rmFile(String filename) {
        String sha = additionalMap.remove(filename);

        Commit lastCommit = Commit.getProjectHeadCommit();
        if (lastCommit.getTrackedMap().containsKey(filename)) {
            removalSet.add(filename);
            restrictedDelete(filename);
        } else if (sha == null) {
            Repository.exit("No reason to remove the file.");
        }
    }

    /**
     * Saves current Staging to .gitlet/INDEX.
     */
    public void save() {
        writeObject(STAGINGFILE, this);
    }
}
