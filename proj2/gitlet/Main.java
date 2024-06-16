package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Vincent Ma
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }


//          TODO:
//          - checkout -- [filename]
//          - checkout [commit id] -- [filename]

        switch (args[0]) {
            case "init":
                // gitlet.Main init
                validArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // gitlet.Main add FILENAME
                validArgs(args, 2);
                checkInitiazed();
                Repository.add(args[1]);
                break;
            case "commit":
                // gitlet.Main commit MESSAGE
                validArgs(args, 2);
                checkInitiazed();
                Repository.commit(args[1]);
                break;
            case "rm":
                // gitlet.Main rm FILENAME
                validArgs(args, 2);
                checkInitiazed();
                Repository.rm(args[1]);
                break;
            case "log":
                // gitlet.Main log
                validArgs(args, 1);
                checkInitiazed();
                Repository.log();
                break;
            case "global-log":
                // gitlet.Main global-log
                validArgs(args, 1);
                checkInitiazed();
                Repository.global_log();
                break;
            case "find":
                // gitlet.Main find MESSAGE
                validArgs(args, 2);
                checkInitiazed();
                Repository.find(args[1]);
                break;
            case "status":
                // gitlet.Main status
                validArgs(args, 1);
                checkInitiazed();
                Repository.status();
                break;
            default:
                // invalid command
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Checks if the number of ARGS equals to VALID NUMS.
     * Quits if not valid.
     *
     * @param validNums Valid numbers of arguments.
     *                  Can be more than one valid number.
     */
    public static void validArgs(String[] args, int... validNums) {
        for (int n : validNums) {
            if (args.length == n) {
                return;
            }
        }
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

    /**
     * Quits when it's not initialized yet, otherwise continues to run.
     */
    public static void checkInitiazed() {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
