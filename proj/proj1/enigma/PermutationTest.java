package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author shaw ma
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testCase1() {
        Alphabet alp = new Alphabet("ABCDEFGHI");
        Permutation p = new Permutation("(ABC) (DEF) (HI)", alp);
        assertEquals(p.size(), 9);
        assertEquals(1, p.permute(0));
        assertEquals(0, p.permute(2));
        assertEquals(6, p.permute(6));
        assertEquals(0, p.invert(1));
        assertEquals(5, p.invert(3));
        assertEquals(6, p.invert(6));
        assertEquals('B', p.permute('A'));
        assertEquals('D', p.permute('F'));
        assertEquals('G', p.permute('G'));
        assertEquals('A', p.invert('B'));
        assertEquals('C', p.invert('A'));
        assertEquals('G', p.invert('G'));
        assertEquals(p.alphabet(), alp);
        assertFalse(p.derangement());
    }

    @Test
    public void testCase2() {
        Alphabet alp = new Alphabet("ABCDEFGHI");
        Permutation p = new Permutation("", alp);
        assertEquals(1, p.permute(1));
        assertEquals(6, p.permute(6));
        assertEquals(0, p.invert(0));
        assertEquals(6, p.invert(6));
        assertEquals('B', p.permute('B'));
        assertEquals('G', p.permute('G'));
        assertEquals('A', p.invert('A'));
        assertEquals('G', p.invert('G'));
        assertFalse(p.derangement());
    }

    @Test
    public void testCase3() {
        Alphabet alp = new Alphabet("ABCDEF");
        Permutation p = new Permutation("(ABCDEF)", alp);
        assertEquals(p.size(), 6);
        assertTrue(p.derangement());
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet1() {
        Alphabet alp = new Alphabet("ABCD");
        Permutation p = new Permutation("(ABCD)", alp);
        p.permute('F');
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet2() {
        Alphabet alp = new Alphabet("ABCD");
        Permutation p = new Permutation("(ABCD)", alp);
        p.invert('F');
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet3() {
        Alphabet alp = new Alphabet("ABCD");
        Permutation p = new Permutation("(ABCD)", alp);
        p.permute(5);
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet4() {
        Alphabet alp = new Alphabet("ABCD");
        Permutation p = new Permutation("(ABCD)", alp);
        p.invert(5);
    }
}
