import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * HW #7, Sorting ranges.
 *
 * @author ryan ma
 */

public class Intervals {

	/**
	 * Assuming that INTERVALS contains two-element arrays of integers,
	 * <x,y> with x <= y, representing intervals of ints, this returns the
	 * total length covered by the union of the intervals.
	 */
	public static int coveredLength(List<int[]> intervals) {
		/* 不知道为什么这sb list不能remove，气死我了 */
		int totalRange = 0, targetIndex = 0;
		intervals.sort(new ArrayComparator());
		for (int i = 1; targetIndex + i < intervals.size(); ) {
			if (combine(intervals.get(targetIndex),
					intervals.get(targetIndex + i))) {
				i += 1;
			} else {
				totalRange += intervals.get(targetIndex)[1] - intervals.get(targetIndex)[0];
				targetIndex += i;
				i = 1;
			}
		}
		totalRange += intervals.get(targetIndex)[1] - intervals.get(targetIndex)[0];
		return totalRange;
	}

	/**
	 * Comparator for int arrays.
	 */
	static class ArrayComparator implements Comparator<int[]> {
		@Override
		public int compare(int[] o1, int[] o2) {
			return Integer.compare(o1[0], o2[0]);
		}
	}

	/**
	 * Return true if these two intervals can combine into one.
	 */
	static private boolean combine(int[] inte1, int[] inte2) {
		if (inte2[0] <= inte1[1] + 1) {
			if (inte2[1] > inte1[1]) {
				inte1[1] = inte2[1];
			}
			return true;
		}
		return false;
	}

	/**
	 * Test intervals.
	 */
	static final int[][] INTERVALS = {
			{19, 30}, {8, 15}, {3, 10}, {6, 12}, {4, 5},
	};

	/**
	 * Covered length of INTERVALS.
	 */
	static final int CORRECT = 23;

	/**
	 * Performs a basic functionality test on the coveredLength method.
	 */
	@Test
	public void basicTest() {
		assertEquals(CORRECT, coveredLength(Arrays.asList(INTERVALS)));
		// Arrays.asList(T... a)
		//Returns a fixed-size list backed by the specified array. Changes
		// made to the array will be visible in the returned list, and changes
		// made to the list will be visible in the array. The returned list is
		// Serializable and implements RandomAccess.
		//The returned list implements the optional Collection methods,
		// except those that would change the size of the returned list. Those
		// methods leave the list unchanged and throw
		// UnsupportedOperationException.
	}

	/**
	 * Runs provided JUnit test. ARGS is ignored.
	 */
	public static void main(String[] args) {
		System.exit(ucb.junit.textui.runClasses(Intervals.class));
	}

}
