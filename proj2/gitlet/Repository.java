package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Jiehao Guan
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJECTS_DIR = join(GITLET_DIR, "Objects");

    private static final File HEAD = join(GITLET_DIR, "HEAD");

    private static final File REFS_DIR = join(GITLET_DIR, "Refs");

    private static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");
    private static final String DEFAULT_BRANCH_NAME = "master";

    /* The index file for staging area */
    public static File STAGE = join(GITLET_DIR, "stage");

    /* The init command */
    public static void init() {
        /* Failure case */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        /* Create a .gitlet directory */
        createInitDirectory();

        /* Set the default branch and master head */
        setHEAD(DEFAULT_BRANCH_NAME);

        /* Set the init commit */
        setInitCommit();
    }

    /* Initialize a repository at the current working directory.
     * .gitlet
     * |-- Objects
     *     |--Commits and Blobs
     * |-- refs
     *     |-- heads
     * |--HEAD
     * |--Stage
     */
    private static void createInitDirectory() {
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEAD.mkdir();
        STAGE.mkdir();
    }

    /* Set the HEAD pointer to the selected branch */
    private static void setHEAD(String branchName) {
        writeContents(HEAD, join(BRANCH_HEADS_DIR, branchName));
    }

    /* Create the initial commit file */
    private static void setInitCommit(){
        Commit initCommit = new Commit();
        initCommit.save();
        updateBranchHead(DEFAULT_BRANCH_NAME, initCommit.getSha1ID());
    }

    /* update the current Branch head to the new commit
    *  find the branch head file in the directory
    */
    private static void updateBranchHead(String branchName, String id) {
        File branchHeadFile = getBranchHeadFile(branchName);
        updateBranchHead(branchHeadFile, id);
    }

    /* save the commit id to the branch head */
    private static void updateBranchHead(File branchHeadFile, String id) {
        writeContents(branchHeadFile, id);
    }

    /* get the current branch */
    private String getCurrentBranch() {
        String branchName = Utils.readContentsAsString(HEAD);
        return branchName.replace("ref: refs/heads/", "");
    }

    /* find the branch head file in the directory */
    private static File getBranchHeadFile(String branchName) {
        return join(BRANCH_HEADS_DIR, branchName);
    }

    /* The add command */
    public void add(String filename) {
        File file = getFile(filename);
        validateFile(file);

        /* check if this blob exists in the current commit and stage
         * and decide whether to add the blob to the stage and save or not
         */
        Blob blob = new Blob(filename, file);
        String blobID = Utils.sha1(blob);
        StagingArea stage = StagingArea.fromFile();

        /* if the current commit has this blob
         * then remove it from stage if it is already there
         * if false, then do nothing
         */
        if (checkCurrentCommit(filename, blobID)) {
            if (checkRemoved(filename, stage)) {
                stage.getRemoved().remove(filename);
            }
            if (checkAdded(filename, blobID, stage)) {
                stage.getAdded().remove(filename);
            }
        } else if (!checkAdded(filename, blobID, stage)) {
            stage.getAdded().put(filename, blobID);
            blob.save();
        }
        stage.save();
    }

    /* The rm command */
    public void remove(String filename) {
        StagingArea stage = StagingArea.fromFile();

        /* check if this blob exists in the current commit and stage */
        if (stage.getAdded().containsKey(filename)) {
            stage.getAdded().remove(filename);
            stage.save();
        }
        if (getCurrentCommit().getBlobs().get(filename) != null) {
            Utils.restrictedDelete(filename);
            stage.getRemoved().add(filename);
            stage.save();
        } else {
            exitWithError("No reason to remove the file.");
        }
    }

    /* check if the blob is in the current commit
     *  return ture if it already exists
     */
    private boolean checkCurrentCommit(String filename, String sha1) {
        return getCurrentCommit().getBlobs().get(filename) != null
                && getCurrentCommit().getBlobs().get(filename).equals(sha1);
    }

    /* check if the blob is in the added stage
     *  return ture if it already exists
     */
    private boolean checkAdded(String filename, String sha1, StagingArea Stage) {
        return Stage.getAdded().get(filename).equals(sha1);
    }

    /* check if the blob is in the removed stage
     *  return ture if it already exists
     */
    private boolean checkRemoved(String filename, StagingArea Stage) {
        return Stage.getRemoved().contains(filename);
    }

    /* Check if the file exists */
    private static void validateFile(File file) {
        if (!file.exists()) {
            exitWithError("File does not exist.");
        }
    }
    private static File getFile(String filename) {
        return join(CWD, filename);
    }

    /* get the current commit */
    public static Commit getCurrentCommit() {
        String currentCommit = readContentsAsString(HEAD);
        File commit = Utils.getObjFile(currentCommit);
        return readObject(commit, Commit.class);
    }

    /* The commit command */
    public void commit(String msg) {
        StagingArea stage = StagingArea.fromFile();

        /* Abort if no files have been staged */
        if (stage.isClean()) {
            Utils.exitWithError("No changes added to the commit.");
        }

        /* Copy the information from the current commit */
        ArrayList<String> parents = new ArrayList<>();
        parents.add(getCurrentCommit().getSha1ID());
        Map<String, String> newBlobs = updateBlobs(stage);

        /* Create a new Commit */
        Commit newCommit = new Commit(msg, parents, newBlobs);
        newCommit.save();

        /* Update the BranchHead */
        updateBranchHead(getCurrentBranch(), newCommit.getSha1ID());

        /* Clear the staging area after a commit */
        stage.clean();
    }


    /* Update the blobs map based on the staging area. */
    private Map<String, String> updateBlobs(StagingArea stage) {
        Commit current = getCurrentCommit();
        Map<String, String> newBlobs = current.getBlobs();
        newBlobs.putAll(stage.getAdded());
        for (String filename : stage.getRemoved()) {
            newBlobs.remove(filename);
        }
        return newBlobs;
    }

    /* The log command */
    public void log() {
        Commit current = getCurrentCommit();
        Commit parent = current.getParent(0);
        Commit secondParent = current.getParent(1);

        /* trace back the commit tree and print */
        do {
            System.out.println("===");
            System.out.println("Commit " + current.getSha1ID());
            if (secondParent != null) {
                System.out.println("Merge: " +
                        parent.getSha1ID().substring(0, 6) + " " +
                        secondParent.getSha1ID().substring(0, 6));
            }
            System.out.println("Date:" + current.getTime());
            System.out.println(current.getMessage());
            System.out.println();

            current = parent;
            parent = current.getParent(0);
            secondParent = current.getParent(1);
        } while (parent != null);
    }

    /* The global-log command */
    public void globalLog() {


    }

    /* The find command */
    public void find() {

    }

    /* The status command */
    public void status() {

        System.out.println("=== Branches ===");

        System.out.println("=== Staged Files ===");

        System.out.println("=== Removed Files ===");

        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println("=== Staged Files ===");

        System.out.println("=== Untracked Files ===");
    }

    /* The checkout command for the filename */
    public void checkoutFilename(String filename) {
        Commit current = getCurrentCommit();
        checkFilename(current, filename);
        pullFileToCWD(current, filename);
    }

    /* help checking if the commit contains this file */
    private void checkFilename(Commit commit, String filename) {
        if (!commit.getBlobs().containsKey(filename)) {
            exitWithError("File does not exist in that commit.");
        }
    }

    /* Pull the file into the CWD */
    private void pullFileToCWD(Commit commit, String filename) {
        if (Utils.join(CWD, filename).exists()) {
            Utils.restrictedDelete(Utils.join(CWD, filename));
        }

        File file = Utils.join(CWD, filename);
        String fileID = commit.getBlobs().get(filename);
        Blob blob = new Blob(filename, getObjFile(fileID));
        writeObject(file, blob);
    }

    /* The checkout command for the commit */
    public void checkoutCommit(String commitID, String filename) {
        checkCommitID(commitID);
        Commit thisCommit = readObject(getObjFile(commitID), Commit.class);
        checkFilename(thisCommit, filename);
        pullFileToCWD(thisCommit, filename);
    }

    /* Help checking if the commit ID exists */
    private void checkCommitID(String id) {
        if (!getObjFile(id).exists()) {
            exitWithError("No commit with that id exists.");
        }
    }

    /* The checkout command for the branch name */
    public void checkoutBranchName(String branchName) {
        checkBranch(branchName);
        if (getCurrentBranch().equals(branchName)) {
            exitWithError("No need to checkout the current branch.");
        }

    }

    /* Help checking if the branch exists */
    private void checkBranch(String branchName) {
        if (!getBranchHeadFile(branchName).exists()) {
            exitWithError("No such branch exists.");
        }
    }

    /* The branch command */
    public void branch(String branchName) {
        /* Check if this branch already exists */
        File branch = getBranchHeadFile(branchName);
        if (branch.exists()) {
            exitWithError("A branch with that name already exist.");
        }

        /* update the branch to the current commit */
        Commit current = getCurrentCommit();
        updateBranchHead(branchName, current.getSha1ID());
    }

    /* The branch command */
    public void rmBranch(String branchName) {
        /* Check if this branch exists */
        File branchToRemove = getBranchHeadFile(branchName);
        if (!branchToRemove.exists()) {
            exitWithError("A branch with that name does not exist.");
        }

        /* Check if this branch is currently occupied */
        if (branchName.equals(getCurrentBranch())) {
            exitWithError("Cannot remove the current bran");
        }

        /* delete the branch */
        branchToRemove.delete();
    }

    /* The reset command */
    public void reset() {

    }

    /* The merge command */
    public void merge() {

    }
}
