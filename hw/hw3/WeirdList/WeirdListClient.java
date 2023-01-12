/**
 * Functions to increment and sum the elements of a WeirdList.
 *
 * @author ryan ma.
 */

class WeirdListClient {

	/* Return the result of adding N to each element of L. */
	static WeirdList add(WeirdList L, int n) {
		Add add = new Add(n);
		return L.map(add);
	}

	/* Return the sum of all the elements in L. */
	static int sum(WeirdList L) {
		Sum sum = new Sum();
		L.map(sum);
		return sum.getTotal();
	}

	static class Add implements IntUnaryFunction {
		/**
		 * The thing to add.
		 */
		int toAdd;

		/**
		 * Constructor method.
		 */
		public Add(int n) {
			this.toAdd = n;
		}

		@Override
		public int apply(int _head) {
			/* This method is extended from IntUnaryFunction interface, as
			 * subclass, we must give apply a public modifier. */
			_head += toAdd;
			return _head;
		}
	}

	static class Sum implements IntUnaryFunction {
		/**
		 * The sum of all nodes.
		 */
		int total = 0;

		/**
		 * Return total.
		 */
		int getTotal() {
			return total;
		}

		@Override
		public int apply(int _head) {
			total += _head;
			return _head;
		}
	}

}
