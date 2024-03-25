package lec.lec03;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestSort {
    @Test
    public void testSort() {
        String[] s = {"i", "am", "a", "human"};
        String[] expect = {"a", "am", "human", "i"};
        Sort.sort(s);
        assertArrayEquals(s, expect);
    }

}
