package lists;

/* NOTE: The file Utils.java contains some functions that may be
 * useful in testing your answers. */

/**
 * List problem.
 *
 * @author ryan ma
 */

class Lists {
	/*  Return the list of lists formed by breaking up L into "natural runs":
	 *  that is, maximal strictly ascending sublists, in the same order as
	 *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
	 *  then result is the four-item list
	 *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
	 *  Destructive: creates no new IntList items, and may modify the
	 *  original list pointed to by L. */
	static IntListList naturalRuns(IntList L) {
		IntListList result = new IntListList(L, null);
		IntListList traverse = result;
		for (IntList p = L; p.tail != null; ) {
			if (p.head > p.tail.head) {
				IntList nextList = p.tail;
				p.tail = null;
				traverse.tail = new IntListList(nextList, null);
				traverse = traverse.tail;
				p = nextList;
			} else {
				p = p.tail;
			}
		}
		return result;
	}
}
