package gitlet;

import java.io.File;
import java.io.Serializable;

/** Represents a gitlet Blob object.
 *  does at a high level.
 *
 *  @author Jiehao Guan
 */

public class Blob implements Serializable {
    /* The name of the source file */
    private String filename;

    /* The content of the file */
    private byte[] content;


    public Blob(String source, File sourceFile) {
        filename = source;
        content = Utils.readContents(sourceFile);
    }

    /* Get the ID of the blob */
    public String getID() {
        return Utils.sha1(filename, content);
    }

    /* Get the filename of the blob */
    public String getFilename() {
        return filename;
    }

    /* Get the content of the blob */

    public byte[] getContent() {
        return content;
    }

    /* Get the Blob file */
    public File getFile() {
        return Utils.getObjFile(getID());
    }

    /* save the blob to the objects folder */
    public void save() {
        Utils.saveObj(getFile(), this);
    }

}
