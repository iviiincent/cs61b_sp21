package lec.lec03;

import org.junit.Test;

import static org.junit.Assert.*;


public class Sort {

    public static void sort(String[] x) {
        sort(x, 0);
    }

    private static void sort(String[] x, int start) {
        if (start == x.length) {
            return;
        }
        int smallestIndex = findSmallestIndex(x, start);
        swap(x, start, smallestIndex);
        sort(x, start + 1);
    }

    private static void swap(String[] x, int a, int b) {
        String t = x[a];
        x[a] = x[b];
        x[b] = t;
    }


    private static int findSmallestIndex(String[] x, int start) {
        int res = start;
        for (int i = start + 1; i < x.length; ++i) {
            if (x[i].compareTo(x[res]) < 0) {
                res = i;
            }
        }
        return res;
    }

    @Test
    public void testSort() {
        String[] s = {"i", "am", "a", "human"};
        String[] expect = {"a", "am", "human", "i"};
        sort(s);
        assertArrayEquals(s, expect);
    }

    @Test
    public void testFindSmallestIndex() {
        String[] s = {"i", "am", "a", "human"};
        int expect = 2;
        assertEquals(findSmallestIndex(s, 0), expect);
    }
}
