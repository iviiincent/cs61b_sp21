package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bl = new BuggyAList<>();

        for (int i = 4; i <= 6; i++) {
            al.addLast(i);
            bl.addLast(i);
        }
        for (int i = 4; i <= 6; i++) {
            assertEquals(al.removeLast(), bl.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bl = new BuggyAList<>();
        int N = 50000;

        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0 && al.size() != 1000) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                al.addLast(randVal);
                bl.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(al.size(), bl.size());
            } else if (operationNumber == 2 && al.size() > 0) {
                // getLast
                assertEquals(al.getLast(), bl.getLast());
            } else if (operationNumber == 3 && al.size() > 0) {
                // removeLast
                assertEquals(al.removeLast(), bl.removeLast());
                assertEquals(al.size(), bl.size());
            }
        }
    }
}
