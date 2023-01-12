package arrays;

import com.sun.tools.corba.se.idl.Util;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.junit.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {
    /** this is the test file for Arrays.java
     */
    @Test
    public void test_catenate() {
        int[] A = new int[] {1, 2, 3};
        int[] B = new int[] {4, 5, 6};
        int[] C = new int[] {1, 2, 3, 4, 5, 6};
        assertTrue(Utils.equals(C,Arrays.catenate(A,B)));

        A = new int[] {1, 2};
        B = new int[] {5, 6};
        C = new int[] {1, 2, 5, 6};
        assertTrue(Utils.equals(C,Arrays.catenate(A,B)));
    }

    @Test
    public void test_remove() {
        int[] A = new int[] {1, 2, 3, 4, 5, 6};
        int[] B = new int[] {1, 2, 6};
        assertTrue(Utils.equals(B,Arrays.remove(A,2,3)));

        B = new int[] {4, 5, 6};
        assertTrue(Utils.equals(B, Arrays.remove(A,0,3)));
    }

    @Test
    public void test_naturalRuns() {
        int[] A = new int[] {1, 3, 7, 5, 4, 6, 9, 10};
        int[][] B = new int[][] {{1, 3, 7}, {5}, {4, 6, 9, 10}};
        Utils.print(Arrays.naturalRuns(A));
        assertTrue(Utils.equals(B,Arrays.naturalRuns(A)));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
