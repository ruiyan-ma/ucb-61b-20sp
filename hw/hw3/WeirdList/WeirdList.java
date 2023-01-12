/**
 * A WeirdList holds a sequence of integers.
 *
 * @author ryan ma
 */

public class WeirdList {

	/* The private fields. */
	private final int _head;
	private final WeirdList _tail;

	/* The empty sequence of integers. */
	/* From WeirdListTest.java, we know that a WeirdList must end with this
	 * static variable, EMPTY, so EMPTY must points to a EmptyList object and
	 * it's responsible for dealing with ending works. */
	public static final WeirdList EMPTY = new EmptyList(-1, null);

	/* A new WeirdList whose head is HEAD and tail is TAIL. */
	public WeirdList(int head, WeirdList tail) {
		this._head = head;
		this._tail = tail;
	}

	/* Returns the number of elements in the sequence that
	 * starts with THIS. */
	public int length() {
		// Do recursion.
		return 1 + this._tail.length();
	}

	/* Return a string containing my contents as a sequence of numerals
	 * each preceded by a blank.  Thus, if my list contains
	 * 5, 4, and 2, this returns " 5 4 2". */
	@Override
	public String toString() {
		// Do recursion.
		return " " + this._head + this._tail.toString();
	}

	/**
	 * Part 3b: Apply FUNC.apply to every element of THIS WeirdList in
	 * sequence, and return a WeirdList of the resulting values.
	 */
	public WeirdList map(IntUnaryFunction func) {
		return new WeirdList(func.apply(_head), _tail.map(func));
		// 头结点执行一次apply之后，将tail作为头结点循环执行map
		// 以实现对每一个结点都进行一次apply操作
		// 将全部经过map处理后的WeirdList返回(non-destructive)
	}

	static class EmptyList extends WeirdList {

		/**
		 * Constructor method.
		 */
		public EmptyList(int head, WeirdList tail) {
			super(head, tail);
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public String toString() {
			return "";
		}

		@Override
		public WeirdList map(IntUnaryFunction func) {
			return this;
		}
	}
}

