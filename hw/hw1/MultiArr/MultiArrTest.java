import static org.junit.Assert.*;
import org.junit.Test;

public class MultiArrTest {

    @Test
    public void testMaxValue() {
        //TODO: Your code here!
        int[][] arr={{0},{1,2},{2,3,6},{4,5}};
        assertEquals(MultiArr.maxValue(arr),6);
    }

    @Test
    public void testAllRowSums() {
        //TODO: Your code here!
        int[][] arr={{0},{1,2},{2,3,6},{3,5}};
        int raws=arr.length;
        int[] rowsum = {0,3,11,8};
        int[] rowsumans = MultiArr.allRowSums(arr);
        for(int i=0;i<raws;i++) {
            assertEquals(rowsum[i],rowsumans[i]);
        }
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(MultiArrTest.class));
    }
}
