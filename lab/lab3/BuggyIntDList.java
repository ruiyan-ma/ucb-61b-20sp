/**
 * @author Vivant Sakore on 1/29/2020
 */

public class BuggyIntDList extends IntDList {

	/**
	 * @param values creates a BuggyIntDList with ints values.
	 */
	public BuggyIntDList(Integer... values) {
		super(values);
	}

	/**
	 * Merge IntDList `l` into the calling IntDList
	 * Assume that the two IntDLists being merged are sorted individually before merge.
	 * The resulting IntDList must also be sorted in ascending order.
	 *
	 * @param l Sorted IntDList to merge
	 */
	public void mergeIntDList(IntDList l) {
		this._front = sortedMerge(this._front, l._front);
		DNode searchBack = this._back;
		while (searchBack._next != null) {
			searchBack = searchBack._next;
		}
		this._back = searchBack;
	}

	/**
	 * Recursively merge nodes after value comparison
	 *
	 * @param d1 Node 1
	 * @param d2 Node 2
	 * @return Nodes arranged in ascending sorted order
	 */
	private DNode sortedMerge(DNode d1, DNode d2) {
		if (d1 == null) {
			return d2;
		} else if (d2 == null) {
			return d1;
		}
		if (d1._val <= d2._val) {
			d1._next = sortedMerge(d1._next, d2);
			d1._next._prev = d1;
			return d1;
		} else {
			d2._next = sortedMerge(d1, d2._next);
			d2._next._prev = d2;
			return d2;
		}
	}

	/**
	 * Reverses IntDList in-place (destructive). Does not create a new IntDList.
	 */
	public void reverse() {
		DNode temp = null;
		DNode p = this._front;
		while (p != null) {
			temp = p._prev;
			p._prev = p._next;
			p._next = temp;
			p = p._prev;
			// Here we have already exchanged _prev and _next, if you want to
			// go _next now, you need to go _prev.
		}
		if (temp != null) {
			// temp == null indicates that there is only one node in IntDList.
			// When temp != null, exchange this._back and this._front.
			temp = this._back;
			this._back = this._front;
			this._front = temp;
		}
	}
}
