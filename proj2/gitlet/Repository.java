package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.writeContents;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
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
        setBranch(DEFAULT_BRANCH_NAME);

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
    }

    /* help setting a new branch */
    private static void setBranch(String branchName) {
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

    /* find the branch head file in the directory */
    private static File getBranchHeadFile(String branchName) {
        return join(BRANCH_HEADS_DIR, branchName);
    }
}
