/**
 * Scheme-like pairs that can be used to form a list of integers.
 *
 * @author P. N. Hilfinger; updated by Vivant Sakore (1/29/2020)
 */

public class IntDList {

	/**
	 * First and last nodes of list.
	 */
	protected DNode _front, _back;

	/**
	 * An empty list.
	 */
	public IntDList() {
		_front = _back = null;
	}

	/**
	 * @param values the ints to be placed in the IntDList.
	 */
	public IntDList(Integer... values) {
		_front = _back = null;
		for (int val : values) {
			insertBack(val);
		}
	}

	/**
	 * @return The first value in this list.
	 * Throws a NullPointerException if the list is empty.
	 */
	public int getFront() {
		return this._front._val;
	}

	/**
	 * @return The last value in this list.
	 * Throws a NullPointerException if the list is empty.
	 */
	public int getBack() {
		return this._back._val;
	}

	/**
	 * @return The number of elements in this list.
	 */
	public int size() {
		int num = 0;
		for (DNode head = this._front; head != null; head = head._next) {
			num += 1;
		}
		return num;
	}

	/**
	 * @param i index of element to return,
	 *          where i = 0 returns the first element,
	 *          i = 1 returns the second element,
	 *          i = -1 returns the last element,
	 *          i = -2 returns the second to last element, and so on.
	 *          You can assume i will always be a valid index, i.e 0 <= i < size for positive indices
	 *          and -size <= i <= -1 for negative indices.
	 * @return The integer value at index i
	 */
	public int get(int i) {
		if (i < 0) {
			i += 1;
			DNode search;
			for (search = this._back; i != 0; i++) {
				search = search._prev;
			}
			return search._val;
		} else {
			DNode search;
			for (search = this._front; i != 0; i--) {
				search = search._next;
			}
			return search._val;
		}
	}

	/**
	 * @param d value to be inserted in the front
	 */
	public void insertFront(int d) {
		if (this._front == null) {
			DNode head = new DNode(null, d, null);
			this._front = this._back = head;
		} else {
			DNode head = new DNode(null, d, this._front);
			this._front._prev = head;
			this._front = head;
		}
	}

	/**
	 * @param d value to be inserted in the back
	 */
	public void insertBack(int d) {
		if (this._front == null) {
			DNode tail = new DNode(null, d, null);
			this._front = this._back = tail;
		} else {
			DNode tail = new DNode(this._back, d, null);
			this._back._next = tail;
			this._back = tail;
		}
	}

	/**
	 * @param d     value to be inserted
	 * @param index index at which the value should be inserted
	 *              where index = 0 inserts at the front,
	 *              index = 1 inserts at the second position,
	 *              index = -1 inserts at the back,
	 *              index = -2 inserts at the second to last position, and so on.
	 *              You can assume index will always be a valid index,
	 *              i.e 0 <= index <= size for positive indices (including insertions at front and back)
	 *              and -(size+1) <= index <= -1 for negative indices (including insertions at front and back).
	 */
	public void insertAtIndex(int d, int index) {
		/* For positive, 0 <= index <= size;
		 * For negative, -(size+1) <= index <= -1. */
		if (index >= 0) {
			if (index == 0) {
				this.insertFront(d);
			} else if (index == this.size()) {
				this.insertBack(d);
			} else {
				DNode search;
				DNode insert = new DNode(null, d, null);
				for (search = this._front; index != 0; index--) {
					search = search._next;
				}
				search._prev._next = insert;
				insert._prev = search._prev;
				search._prev = insert;
				insert._next = search;
			}
		} else {
			index += 1;
			if (index == 0) {
				this.insertBack(d);
			} else if (index == -this.size()) {
				this.insertFront(d);
			} else {
				DNode search;
				DNode insert = new DNode(null, d, null);
				for (search = this._back; index != 0; index++) {
					search = search._prev;
				}
				search._next._prev = insert;
				insert._next = search._next;
				search._next = insert;
				insert._prev = search;
			}
		}
	}

	/**
	 * Removes the first item in the IntDList and returns it.
	 *
	 * @return the item that was deleted
	 */
	public int deleteFront() {
		assert this._front != null;
		int head_val = this._front._val;
		if (this.size() == 1) {
			this._front = this._back = null;
		} else {
			this._front = this._front._next;
			this._front._prev = null;
		}
		return head_val;
	}

	/**
	 * Removes the last item in the IntDList and returns it.
	 *
	 * @return the item that was deleted
	 */
	public int deleteBack() {
		assert this._back != null;
		int tail_val = this._back._val;
		if (this.size() == 1) {
			this._front = this._back = null;
		} else {
			this._back = this._back._prev;
			this._back._next = null;
		}
		return tail_val;
	}

	/**
	 * @param index index of element to be deleted,
	 *              where index = 0 returns the first element,
	 *              index = 1 will delete the second element,
	 *              index = -1 will delete the last element,
	 *              index = -2 will delete the second to last element, and so on.
	 *              You can assume index will always be a valid index,
	 *              i.e 0 <= index < size for positive indices (including deletions at front and back)
	 *              and -size <= index <= -1 for negative indices (including deletions at front and back).
	 * @return the item that was deleted
	 */
	public int deleteAtIndex(int index) {
		/* For positive, 0 <= index <= size;
		 * For negative, -(size+1) <= index <= -1. */
		if (index >= 0) {
			if (index == 0) {
				return this.deleteFront();
			} else if (index == this.size() - 1) {
				return this.deleteBack();
			} else {
				DNode search;
				for (search = this._front; index != 0; index--) {
					search = search._next;
				}
				search._prev._next = search._next;
				search._next._prev = search._prev;
				return search._val;
			}
		} else {
			index += 1;
			if (index == 0) {
				return this.deleteBack();
			} else if (index == -this.size() + 1) {
				return this.deleteFront();
			} else {
				DNode search;
				for (search = this._back; index != 0; index++) {
					search = search._prev;
				}
				search._prev._next = search._next;
				search._next._prev = search._prev;
				return search._val;
			}
		}
	}

	/**
	 * @return a string representation of the IntDList in the form
	 * [] (empty list) or [1, 2], etc.
	 * Hint:
	 * String a = "a";
	 * a += "b";
	 * System.out.println(a); //prints ab
	 */
	public String toString() {
		if (this.size() == 0) {
			return "[]";
		}
		String str = "[";
		DNode traver;
		for (traver = this._front; traver._next != null; traver = traver._next) {
			str += traver._val + ", ";
		}
		str += traver._val + "]";
		return str;
	}

	/**
	 * DNode is a "static nested class", because we're only using it inside
	 * IntDList, so there's no need to put it outside (and "pollute the
	 * namespace" with it. This is also referred to as encapsulation.
	 * Look it up for more information!
	 */
	static class DNode {

		/**
		 * Previous DNode.
		 */
		protected DNode _prev;

		/**
		 * Next DNode.
		 */
		protected DNode _next;

		/**
		 * Value contained in DNode.
		 */
		protected int _val;

		/**
		 * @param val the int to be placed in DNode.
		 */
		protected DNode(int val) {
			this(null, val, null);
		}

		/**
		 * @param prev previous DNode.
		 * @param val  value to be stored in DNode.
		 * @param next next DNode.
		 */
		protected DNode(DNode prev, int val, DNode next) {
			_prev = prev;
			_val = val;
			_next = next;
		}
	}
}
