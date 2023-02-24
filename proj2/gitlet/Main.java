package gitlet;

import static gitlet.Utils.exitWithError;
import static gitlet.Utils.validateNumberArgs;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Jiehao Guan
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // print the error and exit.
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }

        Repository repo = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumberArgs(args, 1);
                repo.init();
                break;
            case "add":
                validateNumberArgs(args, 2);
                repo.add(args[1]);
                break;
            case "commit":
                validateNumberArgs(args, 2);
                String msg = args[1];
                if (msg.length() == 0) {
                    exitWithError("Please enter a commit message.");
                }
                repo.commit(msg);
                break;
            case "rm":
                validateNumberArgs(args, 2);
                repo.remove(args[1]);
                break;
            case "log":
                validateNumberArgs(args, 1);
                repo.log();
                break;
            case "global-log":
                validateNumberArgs(args, 1);
                repo.globalLog();
                break;
            case "find":
                validateNumberArgs(args, 2);
                repo.find();
                break;
            case "status":
                validateNumberArgs(args, 1);
                repo.status();
                break;
            case "checkout":
                switch (args.length){
                    case 2:
                        repo.checkoutBranchName(args[1]);
                        break;
                    case 3:
                        if (!args[1].equals("--")) {
                            exitWithError("Incorrect operands.");
                        }
                        repo.checkoutFilename(args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            exitWithError("Incorrect operands.");
                        }
                        repo.checkoutCommit(args[1], args[3]);
                        break;
                    default:
                        exitWithError("Incorrect operands.");
                        break;
                }
            case "branch":
                validateNumberArgs(args, 2);
                repo.branch(args[1]);
                break;
            case "rm-branch":
                validateNumberArgs(args, 2);
                repo.rmBranch(args[1]);
                break;
            case "reset":
                validateNumberArgs(args, 2);
                repo.reset();
                break;
            case "merge":
                validateNumberArgs(args, 2);
                repo.merge();
                break;

            default:
                exitWithError("No command with that name exists.");
        }

    }
}
