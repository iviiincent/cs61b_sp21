package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

/**
 * Represents a Binary Large Object.
 * Its name is the SHA1 of the file, and content is the content of file
 * after being serialized.
 *
 * @author Vincent Ma
 */
public class Blob implements Serializable {
    /**
     * The Blob file.
     */
    private final File blobFile;


    /**
     * The file being stored in this Blob.
     */
    private final File storedFile;

    /**
     * The sha1 of the file, as the name of this Blob.
     */
    private final String sha1;

    public Blob(File file) {
        assert file.isFile();
        this.storedFile = file;
        sha1 = sha1(readContents(file));
        blobFile = join(Repository.OBJECTS_DIR, sha1);
    }

    /**
     * Gets Blob File via sha1.
     */
    public static File getBlobFile(String sha1) {
        File file = join(Repository.OBJECTS_DIR, sha1);
        return file.isFile() ? file : null;
    }

    /**
     * Replaces the content of file with given name, to the file with given sha.
     *
     * @param sha1 Asserts that there is such file with given sha1.
     */
    public static void loadBlob(String sha1, String filename) {
        File blobFile = getBlobFile(sha1);
        File file = join(Repository.CWD, filename);
        writeContents(file, readContents(blobFile));
    }

    public File getBlobFile() {
        return blobFile;
    }

    public File getStoredFile() {
        return storedFile;
    }

    public String getSha1() {
        return sha1;
    }

    /**
     * Saves STORED FILE to BLOB FILE.
     */
    public void save() {
        writeContents(blobFile, readContents(storedFile));
    }

}
