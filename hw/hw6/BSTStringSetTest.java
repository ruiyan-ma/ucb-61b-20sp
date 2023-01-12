import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
 * Test of a BST-based String Set.
 * @author
 */
public class BSTStringSetTest  {
    @Test
    public void TestPut() {
        BSTStringSet bstSet = new BSTStringSet();

        bstSet.put("abc");
        assertTrue(bstSet.contains("abc"));
        assertFalse(bstSet.contains("z"));

        bstSet.put("dd");
        assertTrue(bstSet.contains("dd"));
        assertFalse(bstSet.contains("d"));

        bstSet.put("dd");
        ArrayList<String> setList = new ArrayList<String>();
        setList.add("abc");
        setList.add("dd");
        assertEquals(setList, bstSet.asList());

    }

    @Test
    public void TestAsList() {
        BSTStringSet bstSet = new BSTStringSet();
        bstSet.put("c");
        bstSet.put("d");
        bstSet.put("a");
        bstSet.put("b");

        ArrayList<String> setList = new ArrayList<String>();
        setList.add("a");
        setList.add("b");
        setList.add("c");
        setList.add("d");
        assertEquals(setList, bstSet.asList());
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BSTStringSetTest.class));
    }
}
