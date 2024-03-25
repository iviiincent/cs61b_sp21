package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void test1() {
        IntList lst = IntList.of(8,8,11);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("8 -> 8 -> 121", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void test2() {
        IntList lst = IntList.of(11,11,8);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("121 -> 121 -> 8", lst.toString());
        assertTrue(changed);
    }

    public void test3() {
        IntList lst = IntList.of(11,11,11);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("121 -> 121 -> 121", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimes1() {
        IntList lst = IntList.of(11, 12, 13, 14, 15);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("121 -> 12 -> 169 -> 14 -> 15", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimes2() {
        IntList lst = IntList.of(8, 9, 10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("8 -> 9 -> 10", lst.toString());
        assertFalse(changed);
    }
}
