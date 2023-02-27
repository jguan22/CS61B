package gitlet;

import java.io.File;
import java.util.*;
import java.util.PriorityQueue;

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
        BRANCH_HEADS_DIR.mkdir();
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
        List<String> allCommits = findAllCommits();
        for (String commitID : allCommits) {
            Commit thisCommit = readObject(getObjFile(commitID), Commit.class);
            printCommit(thisCommit);

        }
    }

    /* Find all commits */
    private List<String> findAllCommits() {
        List<String> commits = new ArrayList<>();
        for (String branch : plainFilenamesIn(BRANCH_HEADS_DIR)) {
            Commit commit = readObject(getBranchHeadFile(branch), Commit.class);
            do {
                if (!commits.contains(commit.getSha1ID())) {
                    commits.add(commit.getSha1ID());
                }
                commit = commit.getParent(0);
            } while (commit.getParents() != null);
        }
        return commits;
    }

    /* print the commit */
    private void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("Commit " + commit.getSha1ID());
        System.out.println("Date:" + commit.getTime());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /* The find command */
    public void find(String msg) {
        List<String> allCommits = findAllCommits();
        List<Commit> commits = new ArrayList<>();
        for (String commitID : allCommits) {
            Commit thisCommit = readObject(getObjFile(commitID), Commit.class);
            if (thisCommit.getMessage().equals(msg)) {
                commits.add(thisCommit);
            }
        }

        if (commits.isEmpty()) {
            exitWithError("Found no commit with that message.");
        } else {
            for (Commit commit : commits) {
                printCommit(commit);
            }
        }
    }

    /* The status command */
    public void status() {
        System.out.println("=== Branches ===");
        for (String branchNames : plainFilenamesIn(BRANCH_HEADS_DIR)) {
            if (getCurrentBranch().equals(branchNames)) {
                System.out.println("*" + branchNames);
            } else {
                System.out.println(branchNames);
            }
        }

        System.out.println("=== Staged Files ===");
        Map<String, String> added = StagingArea.fromFile().getAdded();
        for (String name : added.keySet()) {
            System.out.println(name);
        }

        System.out.println("=== Removed Files ===");
        Set<String> removed = StagingArea.fromFile().getRemoved();
        for (String name : removed) {
            System.out.println(name);
        }

        System.out.println("=== Modifications Not Staged For Commit ===");

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

        Commit newCommit = readObject(getBranchHeadFile(branchName), Commit.class);
        checkCWD(newCommit);
        for (File f : CWD.listFiles()) {
            if (!restrictedDelete(f)) {
                exitWithError("Cannot delete the file: " + f.getName());
            }
        }
        clearStage();
        copyBlobs(newCommit);
        setHEAD(branchName);
    }

    /* Help checking if the branch exists */
    private void checkBranch(String branchName) {
        if (!getBranchHeadFile(branchName).exists()) {
            exitWithError("No such branch exists.");
        }
    }

    /* Help checking if any untracked files */
    private void checkCWD(Commit newCommit) {
        List<String> fileList = Utils.plainFilenamesIn(CWD);
        List<String> untrackedFiles = new ArrayList<>();

        /* check the current commit and stage */
        Map<String, String> currentBlobs = getCurrentCommit().getBlobs();
        StagingArea stage = StagingArea.fromFile();
        for (String filename : fileList) {
            if (currentBlobs.containsKey(filename)) {
                if (stage.getRemoved().contains(filename)) {
                    untrackedFiles.add(filename);
                }
            } else if (!stage.getAdded().containsKey(filename)) {
                untrackedFiles.add(filename);
            }
        }

        /* compare to the new commit */
        Map<String, String> newBlobs = newCommit.getBlobs();
        for (String filename : untrackedFiles) {
            if (!newBlobs.containsKey(filename)) {
                exitWithError("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
            }
        }
    }

    /* Clear the staging area */
    private void clearStage() {
        StagingArea stage = StagingArea.fromFile();
        if (!stage.isClean()) {
            stage.clean();
        }
    }

    /* Copy all the files from the commit */
    private void copyBlobs(Commit commit) {
        Map<String, String> blobs = commit.getBlobs();
        for (String sha1 : blobs.values()) {
            Blob blob = readObject(getObjFile(sha1), Blob.class);
            File file = join(blob.getFilename());
            writeContents(file, blob.getContent());
        }
    }

    /* The branch command */
    public void branch(String branchName) {
        checkBranchName(branchName);

        /* update the branch to the current commit */
        Commit current = getCurrentCommit();
        updateBranchHead(branchName, current.getSha1ID());
    }

    /* Check if the branch exists */
    private void checkBranchName(String branchName) {
        File branch = getBranchHeadFile(branchName);
        if (!branch.exists()) {
            exitWithError("A branch with that name does not exist.");
        }
    }

    /* The branch command */
    public void rmBranch(String branchName) {
        checkBranchName(branchName);

        /* Check if this branch is currently occupied */
        if (branchName.equals(getCurrentBranch())) {
            exitWithError("Cannot remove the current branch.");
        }

        /* delete the branch */
        File branchToRemove = getBranchHeadFile(branchName);
        branchToRemove.delete();
    }

    /* The reset command */
    public void reset(String commitID) {
        checkCommitID(commitID);
        Commit newCommit = readObject(getObjFile(commitID), Commit.class);
        checkCWD(newCommit);
        for (File f : CWD.listFiles()) {
            if (!restrictedDelete(f)) {
                exitWithError("Cannot delete the file: " + f.getName());
            }
        }
        clearStage();
        copyBlobs(newCommit);
        updateBranchHead(getCurrentBranch(), newCommit.getSha1ID());
    }

    /* The merge command */
    public void merge(String branchName) {
        checkStage();
        checkBranchName(branchName);
        String currentBranch = getCurrentBranch();
        if (currentBranch.equals(branchName)) {
            exitWithError("Cannot merge a branch with itself.");
        }

        Commit branchHeadCommit = readObject(getBranchHeadFile(branchName), Commit.class);
        Commit currentCommit = readObject(getBranchHeadFile(currentBranch) , Commit.class);
        checkCWD(branchHeadCommit);

        Commit splitPoint = getLastCommonAncestor(branchHeadCommit, currentCommit);

        /*  The branch is the child of the current */
        if (splitPoint.getSha1ID().equals(currentCommit.getSha1ID())) {
            checkoutBranchName(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        /* The current is the child of the branch */
        if (splitPoint.getSha1ID().equals(branchHeadCommit.getSha1ID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        /* Merge the heads of these two branches */
        Map<String, String> blobs = mergeIntoNewCommit(splitPoint, currentCommit, branchHeadCommit);
        ArrayList<String> parents = new ArrayList<>();
        parents.add(getCurrentBranch());
        parents.add(branchName);
        String msg = "Merge" + " " + branchName + " " + "into" + " " + getCurrentBranch() + ".";
        Commit newCommit = new Commit(msg, parents, blobs);
        newCommit.save();
    }

    /* help merging the input commits */
    private Map<String, String> mergeIntoNewCommit(Commit splitPoint, Commit currentCommit, Commit mergeCommit) {
        List<String> allFiles = getAllMergeFiles(splitPoint, currentCommit, mergeCommit);
        Map<String, String> splitBlobs = splitPoint.getBlobs();
        Map<String, String> currentBlobs = currentCommit.getBlobs();
        Map<String, String> mergeCommitBlobs = mergeCommit.getBlobs();

        /* All possible merge cases table
                  split   HEAD   other   result
         *case 1    A      A      !A       !A    Modified in other but not in HEAD
          case 2    B     !B       B       !B    Modified in HEAD but not in other
          case 3-1  C     !C      !C       !C    Same modification in both
         *case 3-2  D     !D      !D    conflict Different modification in other and HEAD
          case 4    X      E       X        E    Not in split and other but in HEAD
         *case 5    X      X      !F       !F    Not in split and HEAD but in other
         *case 6    G      G       X        X    Unmodified in HEAD but removed in other
          case 7    H      X       H        X    Unmodified in other but removed in HEAD

         case 1, 5: change to other
         case 6: remove
         case 2, 3-1, 4, 7: remain unchanged
         case 3-2: solve conflict
         */

        List<String> listToAdd = new ArrayList<>();    /* case 5 */
        List<String> listToChange = new ArrayList<>(); /* case 1 */
        List<String> listToRemove = new ArrayList<>(); /* case 6 */
        List<String> listToMerge = new ArrayList<>();  /* case 3-2 */

        for (String filename : allFiles) {
            if (mergeCommitBlobs.containsKey(filename)) {
                /* case 5 */
                if (!currentBlobs.containsKey(filename) && !splitBlobs.containsKey(filename)) {
                    listToAdd.add(filename);
                } else if (currentBlobs.containsKey(filename) && splitBlobs.containsKey(filename)) {
                    /* case 1 */
                    if (currentBlobs.get(filename).equals(splitBlobs.get(filename))) {
                        listToChange.add(filename);
                    } else if (!mergeCommitBlobs.get(filename).equals(splitBlobs.get(filename))
                            && !mergeCommitBlobs.get(filename).equals(currentBlobs.get(filename))) {
                        /* case 3-2 */
                        listToMerge.add(filename);
                    }
                }
            } else if (currentBlobs.containsKey(filename) && splitBlobs.containsKey(filename)){
                if (currentBlobs.get(filename).equals(splitBlobs.get(filename))){
                    /* case 6 */
                    listToRemove.add(filename);
                }
            }
        }

        if (!listToAdd.isEmpty()) {
            for (String f : listToAdd) {
                String thisValue = mergeCommitBlobs.get(f);
                currentBlobs.put(f, thisValue);
            }
        }

        if (!listToRemove.isEmpty()) {
            for (String f : listToRemove) {
                String thisValue = currentBlobs.get(f);
                currentBlobs.remove(f, thisValue);
            }
        }

        if (!listToChange.isEmpty()) {
            for (String f : listToChange) {
                String thisValue =mergeCommitBlobs.get(f);
                currentBlobs.replace(f, thisValue);
            }
        }

        if (!listToMerge.isEmpty()) {
            System.out.println("Encountered a merge conflict.");
            for (String f : listToMerge) {
                Blob conflictBlobInCurrent = readObject(getObjFile(currentBlobs.get(f)), Blob.class);
                String conflictContentInCurrent = conflictBlobInCurrent.getContent().toString();
                Blob conflictBlobInMerge = readObject(getObjFile(mergeCommitBlobs.get(f)), Blob.class);
                String conflictContentInMerge = conflictBlobInMerge.getContent().toString();
                String conflictContents = "<<<<<<< HEAD\n" + conflictContentInCurrent
                        + "=======\n" + conflictContentInMerge + ">>>>>>>\n";
                File conflictFile = join(CWD, f);
                writeContents(conflictFile, conflictContents);
                Blob newBlob = new Blob(f, conflictFile);
                newBlob.save();
                currentBlobs.put(f, newBlob.getID());
            }
        }

        return currentBlobs;
    }

    /* Get all the files from the commits */
    private List<String> getAllMergeFiles(Commit splitPoint, Commit current, Commit mergeCommit) {
        Set<String> all = new HashSet<>(splitPoint.getBlobs().keySet());
        all.addAll(current.getBlobs().keySet());
        all.addAll(mergeCommit.getBlobs().keySet());
        List<String> allFiles = new ArrayList<>();
        allFiles.addAll(all);
        return allFiles;
    }


    /* check the staging area if it has any uncommitted change */
    private void checkStage() {
        StagingArea stage = StagingArea.fromFile();
        if (!stage.isClean()) {
            exitWithError("You have uncommitted changes.");
        }
    }

    /* get the latest common ancestor */
    private Commit getLastCommonAncestor(Commit a, Commit b) {
        Comparator<Commit> commitComparator = Comparator.comparing(Commit::getTime).reversed();
        PriorityQueue<Commit> commitToCheck = new PriorityQueue<>(commitComparator);
        commitToCheck.add(a);
        commitToCheck.add(b);
        Set<String> candidates = new HashSet<>();
        while (true) {
            Commit thisCommit = commitToCheck.poll();
            String parentID = thisCommit.getParents().get(0);
            Commit parent = thisCommit.getParent(0);
            if (candidates.contains(parentID)) {
                return parent;
            }
            commitToCheck.add(parent);
            candidates.add(parentID);
        }
    }
}
