package deque;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Comparator;

public class MaxArrayDequeTest {
    private static class IntCmp implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    private static class RevIntCmp implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }

    @Test
    public void test() {
        MaxArrayDeque deque = new MaxArrayDeque(new IntCmp());
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
            deque.addFirst(i);
        }
        assertEquals(99, deque.max());
        assertEquals(0, deque.max(new RevIntCmp()));
    }
}
