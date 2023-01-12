package image;
import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class MatrixUtilsTest {
    /** FIXME
     */
    @Test
    public void testget() {
        double[][] e = new double[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        assertEquals(3.0, MatrixUtils.get(e, 0, 2), 0.01);
        assertNotEquals(3, MatrixUtils.get(e, 3, 4), 0.01);
    }

    @Test
    public void testaccVer() {
        double[][] m = new double[][] {{1000000, 1000000, 1000000, 1000000},
            {1000000, 75990, 30003, 1000000},
            {1000000, 30002, 103046, 1000000},
            {1000000, 29515, 38273, 1000000},
            {1000000, 73403, 35399, 1000000},
            {1000000, 1000000, 1000000, 1000000}
        };
        double[][] acc = new double[][] {{1000000, 1000000, 1000000, 1000000},
            {2000000, 1075990, 1030003, 2000000},
            {2075990, 1060005, 1133049, 2030003},
            {2060005, 1089520, 1098278, 2133049},
            {2089520, 1162923, 1124919, 2098278},
            {2162923, 2124919, 2124919, 2124919}
        };
        double[][] accresult = MatrixUtils.accumulateVertical(m);
        assertArrayEquals(acc, accresult);
    }

    @Test
    public void testacc() {
        double[][] m = new double[][] {{1000000, 1000000, 1000000, 1000000},
            {1000000, 75990, 30003, 1000000},
            {1000000, 30002, 103046, 1000000},
            {1000000, 29515, 38273, 1000000},
            {1000000, 73403, 35399, 1000000},
            {1000000, 1000000, 1000000, 1000000}
        };
        double[][] acc = new double[][] {{1000000, 1000000, 1000000, 1000000},
            {2000000, 1075990, 1030003, 2000000},
            {2075990, 1060005, 1133049, 2030003},
            {2060005, 1089520, 1098278, 2133049},
            {2089520, 1162923, 1124919, 2098278},
            {2162923, 2124919, 2124919, 2124919}
        };
        m = MatrixUtils.Transpose(m);
        acc = MatrixUtils.Transpose(acc);
        assertArrayEquals(acc, MatrixUtils.accumulate(m, MatrixUtils.Orientation.HORIZONTAL));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
