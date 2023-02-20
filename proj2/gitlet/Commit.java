package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
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
    private HashMap<String, String> blobIDs;
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
        blobIDs = new HashMap<>();
        sha1ID = generateId();
        file = Utils.getObjFile(sha1ID);
    }

    public Commit(String message, String parent, Date date, ) {

    }

    /* Change the date format to the required format */
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    /* Generate the sha1 ID for the commit */
    private String generateId() {
        return Utils.sha1(time, message, parents.toString(), blobIDs.toString());
    }

    /* get the sha1 ID of the commit */
    public String getSha1ID() {
        return sha1ID;
    }

    /* save the commit to the objects folder */
    public void save() {
        Utils.saveObj(file, this);
    }

}
