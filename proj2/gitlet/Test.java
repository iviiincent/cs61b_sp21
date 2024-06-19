package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.writeContents;

public class Test {
    public static void main(String[] args) {
        Repository.init();
        String filename = "hello.txt";
        File file = join(Repository.CWD, filename);
        writeContents(file, "hello");

        Repository.add(filename);
        Repository.commit("created hello.txt");

        String branch = "another-branch";
        Repository.branch(branch);
        Repository.checkoutBranch(branch);
        Repository.rmBranch("master");
        System.out.println(Branch.getAllBranchesName());
    }
}
