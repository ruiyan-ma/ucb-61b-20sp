import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        /** Sample assert statement for comparing integers.
        assertEquals(0, 0); */
        assertEquals(CompoundInterest.numYears(2021),1);
        assertEquals(CompoundInterest.numYears(2030),10);

    }

    @Test
    public void testFutureValue() {
        double tolerance = 0.01;
        assertEquals(CompoundInterest.futureValue(10,12,
                2021), 11.2,tolerance);
        assertEquals(CompoundInterest.futureValue(15,10,
                2021), 16.5,tolerance);
        assertEquals(CompoundInterest.futureValue(10,-10,
                2022),8.1,tolerance);
    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertEquals(CompoundInterest.futureValueReal(10,12,
                2021,3),10.864,tolerance);
        assertEquals(CompoundInterest.futureValueReal(10,10,
                2022,5),10.92,tolerance);
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertEquals(CompoundInterest.totalSavings(5000,2021,
                10),10500, tolerance);
        assertEquals(CompoundInterest.totalSavings(10000,2021,
                -10),19000,tolerance);
    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertEquals(CompoundInterest.totalSavingsReal(10000,2021,
                10,3),20370,tolerance);
        assertEquals(CompoundInterest.totalSavingsReal(5000,2021,
                10,5),9975,tolerance);
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
