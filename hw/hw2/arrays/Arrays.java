package arrays;

import java.util.ArrayList;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/**
 * Array utilities.
 *
 * @author ryan ma
 */

class Arrays {

	/* C1. */

	/**
	 * Returns a new array consisting of the elements of A followed by the
	 * the elements of B.
	 */
	static int[] catenate(int[] A, int[] B) {
		/* *Replace this body with the solution. */
		int[] C = new int[A.length + B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);
		return C;
	}

	/* C2. */

	/**
	 * Returns the array formed by removing LEN items from A,
	 * beginning with item #START.
	 */
	static int[] remove(int[] A, int start, int len) {
		/* *Replace this body with the solution. */
		int[] B = new int[A.length - len];
		System.arraycopy(A, 0, B, 0, start);
		System.arraycopy(A, start + len, B, start, A.length - len - start);
		return B;
	}

	/* C3. */

	/**
	 * Returns the array of arrays formed by breaking up A into
	 * maximal ascending lists, without reordering.
	 * For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
	 * returns the three-element array
	 * {{1, 3, 7}, {5}, {4, 6, 9, 10}}.
	 */
	static int[][] naturalRuns(int[] A) {
		// index记录每个subArray的起点在哪
		ArrayList<Integer> index = new ArrayList<>();
		index.add(0);
		for (int i = 0; i < A.length - 1; i++) {
			if (A[i] > A[i + 1]) {
				index.add(i + 1);
			}
		}
		index.add(A.length);
		//最后一位添加A.length以方便计算距离

		int[][] result = new int[index.size() - 1][];
		for (int i = 0, k = 0; i < index.size(); i++) {
			int length = index.get(i + 1) - index.get(i);
			result[i] = new int[length];
			System.arraycopy(A, k, result[i], 0, length);
			k = index.get(i + 1);
		}
		return result;
	}

}
