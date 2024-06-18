package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Test {
    public static void main(String[] args) {
        Repository.init();
        String filename = "hello.txt";
        File file = join(Repository.CWD, filename);
        writeContents(file, "hello");
        Repository.add(filename);
        Repository.commit("created hello.txt");

        Repository.branch("dev");
        Repository.checkoutBranch("dev");

        writeContents(file, "hello world");
        Repository.add(filename);
        Repository.commit("modified hello.txt");

        System.out.println(readContentsAsString(file));
        Repository.checkoutBranch("master");
        System.out.println(readContentsAsString(file));
    }
}
