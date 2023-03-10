import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 * <p>
 * Class containing all the sorting algorithms from 61B to date.
 * <p>
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 * <p>
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 * <p>
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 * <p>
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 *
 * @author ryan ma
 */
public class MySortingAlgorithms {

	/**
	 * Java's Sorting Algorithm. Java uses Quicksort for ints.
	 */
	public static class JavaSort implements SortingAlgorithm {
		@Override
		public void sort(int[] array, int k) {
			Arrays.sort(array, 0, k);
		}

		@Override
		public String toString() {
			return "Built-In Sort (uses quicksort for ints)";
		}

	}

	/**
	 * Insertion sorts the provided data.
	 */
	public static class InsertionSort implements SortingAlgorithm {
		@Override
		public void sort(int[] array, int k) {
			for (int i = 1; i < k; i++) {
				int temp = array[i];
				int j = i - 1;
				while (j >= 0 && array[j] > temp) {
					array[j + 1] = array[j];
					j -= 1;
				}
				array[j + 1] = temp;
			}
		}

		@Override
		public String toString() {
			return "Insertion Sort";
		}
	}

	/**
	 * Selection Sort for small K should be more efficient
	 * than for larger K. You do not need to use a heap,
	 * though if you want an extra challenge, feel free to
	 * implement a heap based selection sort (i.e. heapsort).
	 */
	public static class SelectionSort implements SortingAlgorithm {
		@Override
		public void sort(int[] array, int k) {
			for (int i = 0; i < k; i++) {
				int minIndex = i;
				for (int j = i + 1; j < k; j++) {
					if (array[j] < array[minIndex]) {
						minIndex = j;
					}
				}
				swap(array, minIndex, i);
			}
		}

		@Override
		public String toString() {
			return "Selection Sort";
		}

	}

	/**
	 * Your mergesort implementation. An iterative merge
	 * method is easier to write than a recursive merge method.
	 * Note: I'm only talking about the merge operation here,
	 * not the entire algorithm, which is easier to do recursively.
	 */
	public static class MergeSort implements SortingAlgorithm {
		@Override
		public void sort(int[] array, int k) {
			merge_sort(array, 0, k - 1);
		}

		/**
		 * Merge sort.
		 */
		private static void merge_sort(int[] Arr, int left, int right) {
			if (left != right) {
				int middle = (left + right) / 2;
				merge_sort(Arr, left, middle);
				merge_sort(Arr, middle + 1, right);
				merge(Arr, left, middle, right);
			}
		}

		/**
		 * Merge two arrays into one.
		 */
		private static void merge(int[] Arr, int left, int middle, int right) {
			int[] leftArr = new int[middle - left + 1];
			if (leftArr.length >= 0) {
				System.arraycopy(Arr, left, leftArr, 0, leftArr.length);
			}
			int[] rightArr = new int[right - middle];
			if (rightArr.length >= 0) {
				System.arraycopy(Arr, middle + 1, rightArr, 0, rightArr.length);
			}

			int leftIndex = 0, rightIndex = 0, index = left;
			while (leftIndex < leftArr.length
						   && rightIndex < rightArr.length) {
				if (leftArr[leftIndex] < rightArr[rightIndex]) {
					Arr[index] = leftArr[leftIndex];
					leftIndex += 1;
					index += 1;
				} else {
					Arr[index] = rightArr[rightIndex];
					rightIndex += 1;
					index += 1;
				}
			}

			if (leftIndex == leftArr.length) {
				while (rightIndex < rightArr.length) {
					Arr[index] = rightArr[rightIndex];
					rightIndex += 1;
					index += 1;
				}
			} else {
				while (leftIndex < leftArr.length) {
					Arr[index] = leftArr[leftIndex];
					leftIndex += 1;
					index += 1;
				}
			}
		}

		@Override
		public String toString() {
			return "Merge Sort";
		}
	}

	/**
	 * Your Counting Sort implementation.
	 * You should create a count array that is the
	 * same size as the value of the max digit in the array.
	 */
	public static class CountingSort implements SortingAlgorithm {
		@Override
		public void sort(int[] array, int k) {
			int max = max(array, k), min = min(array, k);
			int range = max - min + 1;
			int[] count = new int[range];
			/* count?????????Arr???element????????????. */
			for (int i = 0; i < k; i++) {
				count[array[i] - min] += 1;
			}
			int index = 0;
			for (int i = min; i <= max; i++) {
				while (count[i - min] > 0) {
					array[index] = i;
					count[i - min] -= 1;
					index += 1;
				}
			}
		}

		/**
		 * Find the max value of an array.
		 */
		private static int max(int[] Arr, int k) {
			int max = Arr[0];
			for (int i = 1; i < k; i++) {
				if (Arr[i] > max) {
					max = Arr[i];
				}
			}
			return max;
		}

		/**
		 * Find the min value of an array.
		 */
		private static int min(int[] Arr, int k) {
			int min = Arr[0];
			for (int i = 1; i < k; i++) {
				if (Arr[i] < min) {
					min = Arr[i];
				}
			}
			return min;
		}

		@Override
		public String toString() {
			return "Counting Sort";
		}
	}

	/**
	 * Your Heapsort implementation.
	 */
	public static class HeapSort implements SortingAlgorithm {
		@Override
		public void sort(int[] array, int k) {
			PriorityQueue<Integer> queue = new PriorityQueue<>();
			for (int i = 0; i < k; i++) {
				queue.add(array[i]);
			}
			for (int i = 0; i < k; i++) {
				array[i] = queue.remove();
			}
		}

		@Override
		public String toString() {
			return "Heap Sort";
		}
	}

	/**
	 * Your Quicksort implementation.
	 */
	public static class QuickSort implements SortingAlgorithm {
		@Override
		public void sort(int[] array, int k) {
			quick_sort(array, 0, k - 1);
		}

		/**
		 * Get pivot.
		 */
		private static int getPivot(int left, int middle, int right) {
			int[] pivots = new int[3];
			pivots[0] = left;
			pivots[1] = middle;
			pivots[2] = right;
			insertion_sort(pivots, 0, 2);
			return pivots[1];
		}

		/**
		 * Insertion sort with START and END. Serve for quick_sort.
		 */
		private static void insertion_sort(int[] Arr, int start, int end) {
			for (int i = start + 1; i <= end; i++) {
				int temp = Arr[i];
				int j = i - 1;
				while (j >= start && Arr[j] > temp) {
					Arr[j + 1] = Arr[j];
					j -= 1;
				}
				Arr[j + 1] = temp;
			}
		}

		/**
		 * Quick sort.
		 */
		private static void quick_sort(int[] Arr, int left, int right) {
			if (right - left < 5) {
				insertion_sort(Arr, left, right);
				return;
			}

			int middle = (right - left) / 2;
			int pivot = getPivot(Arr[left], Arr[middle], Arr[right]);
			int L, R;
			int leftBound = left, rightBound = right;

			/* ???????????????????????????????????????????????????????????????????????????. */
			L = leftBound;
			R = rightBound;
			while (L < R) {
				/* ????????????????????????????????????????????????. */
				while (Arr[L] < pivot && L < R) {
					L += 1;
				}
				/* ????????????????????????????????????????????????. */
				while (Arr[R] >= pivot && L < R) {
					R -= 1;
				}
				/* ?????????????????????. */
				if (L < R) {
					swap(Arr, L, R);
				}
			}
			/* ???????????????. */
			leftBound = L - 1;

			/* ?????????????????????????????????????????????????????????????????????. */
			L = leftBound + 1;
			R = rightBound;
			while (L < R) {
				/* ??????????????????????????????????????????. */
				while (Arr[R] > pivot && L < R) {
					R -= 1;
				}
				/* ??????????????????????????????????????????????????????. */
				while (Arr[L] <= pivot && L < R) {
					L += 1;
				}
				/* ?????????????????????. */
				if (L < R) {
					swap(Arr, L, R);
				}
			}
			/* ???????????????. */
			rightBound = R + 1;

			quick_sort(Arr, left, leftBound);
			quick_sort(Arr, rightBound, right);
		}

		@Override
		public String toString() {
			return "Quicksort";
		}
	}

	/* For radix sorts, treat the integers as strings of x-bit numbers.  For
	 * example, if you take x to be 2, then the least significant digit of
	 * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
	 * and the third least would be 1.  The rest would be 0.  You can even take
	 * x to be 1 and sort one bit at a time.  It might be interesting to see
	 * how the times compare for various values of x. */

	/**
	 * LSD Sort implementation.
	 */
	public static class LSDSort implements SortingAlgorithm {
		@Override
		public void sort(int[] a, int k) {
			/* ???????????????a??????????????????????????????????????????????????????????????????????????????????????????
			 *  ?????????????????????????????????????????????????????????????????????????????????????????????????????? */
			int power = 0;
			do {
				count_sort(a, power, k);
				power += 1;
			} while (!check(a, k));
		}

		/**
		 * Check if array A is already sorted or not.
		 */
		private boolean check(int[] a, int k) {
			for (int i = 0; i < k - 1; i++) {
				if (a[i] > a[i + 1]) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Count sort serving for LSDSort.
		 */
		private void count_sort(int[] Arr, int power, int k) {
			int[] newArr = Arrays.copyOf(Arr, k);
			int[] count = new int[10];
			for (int i = 0; i < k; i++) {
				int index = (int) ((newArr[i] % Math.pow(10, power + 1)) / Math.pow(10, power));
				count[index] += 1;
			}
			/* ???count???????????????1???, ??????????????????????????????;
			 * ??????count????????????????????????????????????element?????????????????????. */
			System.arraycopy(count, 0, count, 1, count.length - 1);
			count[0] = 0;
			for (int i = 0; i < count.length - 1; i++) {
				count[i + 1] += count[i];
			}
			/* ??????????????????, ???????????????????????????count[i]???1. */
			for (int i = 0; i < k; i++) {
				int index = (int) ((newArr[i] % Math.pow(10, power + 1)) / Math.pow(10, power));
				Arr[count[index]] = newArr[i];
				count[index] += 1;
			}
		}

		@Override
		public String toString() {
			return "LSD Sort";
		}
	}

	/**
	 * MSD Sort implementation.
	 */
	public static class MSDSort implements SortingAlgorithm {
		@Override
		public void sort(int[] a, int k) {
			// FIXME
		}

		@Override
		public String toString() {
			return "MSD Sort";
		}

	}

	/**
	 * Exchange A[I] and A[J].
	 */
	private static void swap(int[] a, int i, int j) {
		int swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}

}
