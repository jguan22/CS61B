package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Represents a staging area object.
 *
 *  @author Jiehao Guan
 */

public class StagingArea implements Serializable {

    /* The map of the added files
     * the filename as the key
     * the sha1 ID as the value
     */
    private Map<String, String> addedStage = new HashMap<>();

    /* The hashset of the removal files
     * the filename as the key
     */
    private Set<String> removedStage = new HashSet<>();

    /* Read the staging area from the INDEX file */
    public static StagingArea fromFile() {
        return Utils.readObject(Repository.STAGE, StagingArea.class);
    }

    /* Write the staging area to the INDEX */
    public void save() {
        Utils.writeObject(Repository.STAGE, this);
    }

    /* Get the map of the added file */
    public Map<String, String> getAdded() {
        return addedStage;
    }

    /* Get the set of the removed file */
    public Set<String> getRemoved() {
        return removedStage;
    }

    /* Show if the staging area is empty */
    public boolean isClean() {
        return addedStage.isEmpty() && removedStage.isEmpty();
    }

    /* Clean the staging area */
    public void clean() {
        addedStage.clear();
        removedStage.clear();
        Utils.writeObject(Repository.STAGE, this);
    }

}
