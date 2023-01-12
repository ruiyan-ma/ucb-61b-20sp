import java.util.Arrays;
import java.util.Random;

/**
 * HW #7, Distribution counting for large numbers.
 *
 * @author ryan ma
 */
public class SortInts {

	/**
	 * Sort A into ascending order.  Assumes that 0 <= A[i] < n*n for all
	 * i, and that the A[i] are distinct.
	 * <p>
	 * Use Radix Sort to solve it.
	 */
	static void sort(int[] A) {
		MySortingAlgorithms.LSDSort countSort = new MySortingAlgorithms.LSDSort();
		countSort.sort(A, A.length);
	}

	public static void main(String[] args) throws Exception {
		int[] arr = new int[100];
		Random randomNum = new Random();
		randomNum.setSeed(randomNum.nextInt());
		for (int i = 0; i < arr.length; i++) {
			arr[i] = randomNum.nextInt(1000);
		}
		System.out.println("Original array: ");
		System.out.println(Arrays.toString(arr));
		sort(arr);
		System.out.println("Array after sorting: ");
		System.out.println(Arrays.toString(arr));
		for (int i = 0; i < arr.length - 1; i++) {
			if (arr[i + 1] < arr[i]) {
				throw new Exception("Wrong sort!");
			}
		}
	}
}
