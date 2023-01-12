import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author
 */
public class ECHashStringSetTest  {

    @Test
    public void testHashTable() {
        ECHashStringSet set = new ECHashStringSet();
        set.put("c");
        set.put("abe");
        set.put("e");
        assertTrue(set.contains("abe"));
        assertTrue(set.contains("e"));
        assertFalse(set.contains("ab"));

        List<String> list = set.asList();
        assertEquals(list.size(), 3);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ECHashStringSetTest.class));
    }
}
