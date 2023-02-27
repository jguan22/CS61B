package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Jiehao Guan
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private Date date;
    private String time;
    private ArrayList<String> parents;

    /* The map of the added files
     * the filename as the key
     * the sha1 ID as the value
     */
    private Map<String, String> blobs;
    private String sha1ID;

    /** The message of this Commit. */
    private String message;
    /* Saved commit file */
    private File file;

    /* The init commit */
    public Commit() {
        date = new Date(0);
        time = dateToTimeStamp(date);
        message = "initial commit";
        parents = new ArrayList<>();
        blobs = new HashMap<>();
        sha1ID = this.generateId();
        file = Utils.getObjFile(sha1ID);
    }

    public Commit(String msg, ArrayList<String> p, Map<String, String> b) {
        date = new Date();
        time = dateToTimeStamp(date);
        message = msg;
        parents = p;
        blobs = b;
        sha1ID = generateId();
        file = Utils.getObjFile(sha1ID);
    }

    /* Change the date format to the required format */
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    /* Generate the sha1 ID for the commit */
    private String generateId() {
        return Utils.sha1(time, message, parents.toString(), blobs.toString());
    }

    /* get the sha1 ID of the commit */
    public String getSha1ID() {
        return sha1ID;
    }

    /* get the time of the commit */
    public String getTime() {
        return time;
    }

    /* get the msg of the commit */
    public String getMessage() {
        return message;
    }

    /* get the parent of the commit */
    public ArrayList<String> getParents() {
        return parents;
    }

    /* save the commit to the objects folder */
    public void save() {
        Utils.saveObj(file, this);
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    /* get the i-th parent as a Commit class */
    public Commit getParent(int i) {
        String id = this.getParents().get(i);
        File parent = Utils.getObjFile(id);
        return Utils.readObject(parent, Commit.class);
    }

}
