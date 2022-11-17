package deque;

import net.sf.saxon.om.Item;
import org.junit.Test;
import static org.junit.Assert.*;

/** Test the performance of ArrayDeque */
public class ArrayDequeTest {

    @Test
    /** Test the Size() and isEmpty() function by adding objects to the array */
    public void addIsEmptySizeTest() {

        ArrayDeque<Integer> array = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", array.isEmpty());

        array.addFirst(1);
        assertEquals(1, array.size());
        assertFalse("lld1 should now contain 1 item", array.isEmpty());

        array.addFirst(100);
        assertEquals(2, array.size());
        assertFalse("lld1 should now contain 2 items", array.isEmpty());

        array.addLast(-9);
        assertEquals(3, array.size());
        assertFalse("lld1 should now contain 3 items", array.isEmpty());

        System.out.println("Printing out deque: ");
        array.printDeque();

    }

    @Test
    /** Test the add and remove functions */
    public void addRemoveTest() {

        ArrayDeque<String> array = new ArrayDeque<>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", array.isEmpty());

        array.addLast("Apple");
        // should not be empty
        assertFalse("lld1 should contain 1 item", array.isEmpty());

        array.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", array.isEmpty());

        array.addFirst("Banana");
        assertFalse("lld1 should contain 1 item", array.isEmpty());

        array.removeLast();
        assertTrue("lld1 should be empty after removal", array.isEmpty());

    }

    @Test
    /** Test removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<String> array = new ArrayDeque<>();

        array.addFirst("United");
        array.removeLast();
        array.removeFirst();
        array.removeFirst();

        int size = array.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /** Check if you can create ArrayDeques with different parameterized types */
    public void multipleParamTest() {

        ArrayDeque<String> array1 = new ArrayDeque<String>();
        ArrayDeque<Double> array2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> array3 = new ArrayDeque<Boolean>();

        array1.addFirst("string");
        array2.addFirst(3.14159);
        array3.addFirst(true);

        String s = array1.removeFirst();
        double d = array2.removeFirst();
        boolean b = array3.removeFirst();

        System.out.println(s + " " + d + " " + b);

    }

    @Test
    /** check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> array = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, array.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, array.removeLast());

    }

    @Test
    /** Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> array = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            array.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) array.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) array.removeLast(), 0.0);
        }
    }

}
