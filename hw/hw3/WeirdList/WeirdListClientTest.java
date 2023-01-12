import org.junit.Test;
import ucb.junit.textui;

import static org.junit.Assert.assertEquals;

/**
 * Cursory tests of WeirdListClient.
 *
 * @author Josh Hug
 * @author P. N. Hilfinger
 */

public class WeirdListClientTest {
	@Test
	public void testSum() {
		WeirdList wl1 = new WeirdList(5, WeirdList.EMPTY);
		WeirdList wl2 = new WeirdList(6, wl1);
		WeirdList wl3 = new WeirdList(10, wl2);
		assertEquals(10 + 6 + 5, WeirdListClient.sum(wl3));
		assertEquals(5, WeirdListClient.sum(wl1));
		assertEquals(21, WeirdListClient.sum(wl3));

		WeirdList wl4 = new WeirdList(12, WeirdList.EMPTY);
		assertEquals(12, WeirdListClient.sum(wl4));
		System.out.println(WeirdListClient.sum(wl4));

		WeirdList wl5 = new WeirdList(5, WeirdList.EMPTY);
		WeirdList wl6 = new WeirdList(0, wl5);
		WeirdList wl7 = new WeirdList(55, wl6);
		WeirdList wl8 = new WeirdList(23, wl7);
		WeirdList wl9 = new WeirdList(9, wl8);
		WeirdList wl10 = new WeirdList(7, wl9);
		WeirdList wl11 = new WeirdList(3, wl10);
		WeirdList wl12 = new WeirdList(2, wl11);
		WeirdList wl13 = new WeirdList(1, wl12);
		assertEquals(105, WeirdListClient.sum(wl13));
		System.out.println(WeirdListClient.sum(wl13));
	}

	@Test
	public void testAddSum() {
		WeirdList wl1 = new WeirdList(5, WeirdList.EMPTY);
		WeirdList wl2 = new WeirdList(6, wl1);

		WeirdList nwl = WeirdListClient.add(wl2, 4);
		assertEquals((6 + 4) + (5 + 4), WeirdListClient.sum(nwl));
	}

	public static void main(String[] args) {
		System.exit(textui.runClasses(WeirdListClientTest.class));
	}

}
