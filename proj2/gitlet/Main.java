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

        switch (args[0]) {
            case "init":
                // gitlet.Main init
                Repository.validArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // gitlet.Main add FILENAME
                Repository.validArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                // gitlet.Main commit MESSAGE
                Repository.validArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                // gitlet.Main rm FILENAME
                Repository.validArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                // gitlet.Main log
                Repository.validArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                // gitlet.Main global-log
                Repository.validArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                // gitlet.Main find MESSAGE
                Repository.validArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                // gitlet.Main status
                Repository.validArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                Repository.validArgs(args, 2, 3, 4);
                Repository.checkout(args);
                break;
            case "branch":
                // gitlet.Main branch [BRANCH NAME]
                Repository.validArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                // gitlet.Main rm-branch [BRANCH NAME]
                Repository.validArgs(args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                // gitlet.main reset [COMMIT ID]
                Repository.validArgs(args, 2);
                Repository.reset(args[1]);
                break;
            default:
                Repository.receiveInvalidCommand();
        }
    }
}
