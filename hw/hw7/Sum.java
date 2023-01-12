import java.util.HashMap;

/**
 * HW #7, Two-sum problem.
 *
 * @author ryan ma
 */

public class Sum {

	/**
	 * Returns true iff A[i]+B[j] = M for some i and j.
	 */
	public static boolean sumsTo(int[] A, int[] B, int m) {
		/* This two sum algorithm costs O(N). */
		HashMap<Integer, Integer> hash = new HashMap<>();
		for (int i = 0; i < A.length; i++) {
			hash.put(A[i], i);
		}
		for (int value : B) {
			if (hash.containsKey(m - value)) {
				return true;
			}
		}
		return false;
	}
}
