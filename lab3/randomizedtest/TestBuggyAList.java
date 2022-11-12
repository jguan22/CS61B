package randomizedtest;

import afu.org.checkerframework.checker.units.qual.A;
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
        /** Create two new lists for testing */
        AListNoResizing<Integer> A = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        /** Add integers to both list using addLast function */
        for (int i = 0; i < 3; i++) {
            int newInteger = i + 4;
            A.addLast(newInteger);
            B.addLast(newInteger);
        }

        /** Remove the item from both list and compare to see if they are equal */
        for (int i = 0; i < 3; i++) {
            assertEquals(A.removeLast(), B.removeLast());
        }
    }


    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> M = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                M.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int sizeL = L.size();
                int sizeM = M.size();
                System.out.println("size L: " + sizeL);
                System.out.println("size M: " + sizeM);
                assertEquals(sizeL, sizeM);
            } else if (operationNumber == 2) {
                if (L.size() == 0 || M.size() == 0) {
                    continue;
                }
                int lastL = L.getLast();
                int lastM = M.getLast();
                System.out.println("Last L: " + lastL);
                System.out.println("Last M: " + lastM);
                assertEquals(lastL, lastM);
            } else if (operationNumber == 3) {
                if (L.size() == 0 || M.size() == 0) {
                    continue;
                }
                int lastL = L.removeLast();
                int lastM = M.removeLast();
                System.out.println("Last L: " + lastL);
                System.out.println("Last M: " + lastM);
                assertEquals(lastL, lastM);
            }
        }
    }

}
