package gitlet;

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
            Utils.exitWithError("Please enter a command.");
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Utils.validateNumberArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "error":
                Utils.exitWithError("Incorrect operands.");

            default:
                Utils.exitWithError("No command with that name exists.");
        }

    }
}
