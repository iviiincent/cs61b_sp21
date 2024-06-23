package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Test {
    public static void main(String[] args) {
        Repository.init();
        Repository.branch("another");
        int i = 0;

        System.out.println(i++);
        File helloFile = join(Repository.CWD, "hello.txt");
        writeContents(helloFile, "hello");
        Repository.add(helloFile.getName());
        Repository.commit("created hello.txt");

        System.out.println(i++);
        writeContents(helloFile, "hello world");
        Repository.add(helloFile.getName());
        Repository.commit("modified hello.txt");

        printWdFiles();
        System.out.println(i++);
        Repository.checkoutBranch("another");
        System.out.println(plainFilenamesIn(Repository.CWD));
        printWdFiles();

        System.out.println(i++);
        File howFile = join(Repository.CWD, "how.txt");
        writeContents(howFile, "how");
        Repository.add(howFile.getName());
        Repository.commit("created how.txt");

        System.out.println(i++);
        writeContents(howFile, "how are you");
        Repository.add(howFile.getName());
        Repository.commit("modified how.txt");

        System.out.println(i++);
        Repository.checkoutBranch("master");
        Repository.merge("another");

        printWdFiles();
    }

    public static void printWdFiles() {
        for (String filename : plainFilenamesIn(Repository.CWD)) {
            File file = join(Repository.CWD, filename);
            System.out.println(filename);
            System.out.println(readContentsAsString(file));
            System.out.println();
        }
        System.out.println(" - - - - - - - - - - ");
    }
}
