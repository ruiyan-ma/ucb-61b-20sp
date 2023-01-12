package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {
    /** FIXME
     */

    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.
    @Test
    public void testLists() {
        int[] testArr1 = new int[] {7, 8, 9, 4, 5, 6, 1, 2, 3};
        int[][] testArr2 = new int[][] {{7, 8, 9}, {4, 5, 6}, {1, 2, 3}};
        IntList testIL = IntList.list(testArr1);
        IntListList testILL = IntListList.list(testArr2);
        IntListList testnRuns = Lists.naturalRuns(testIL);
        assertEquals(testILL, testnRuns);

        testArr1 = new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1};
        testArr2 = new int[][] {{9}, {8}, {7}, {6}, {5}, {4}, {3}, {2}, {1}};
        testIL = IntList.list(testArr1);
        testILL = IntListList.list(testArr2);
        testnRuns = Lists.naturalRuns(testIL);
        assertEquals(testILL, testnRuns);
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
