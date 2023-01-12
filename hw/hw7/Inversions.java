import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HW #7, Count inversions.
 *
 * @author ryan ma
 */

public class Inversions {

	/**
	 * A main program for testing purposes.  Prints the number of inversions
	 * in the sequence ARGS.
	 */
	public static void main(String[] args) {
		System.out.println(inversions(Arrays.asList(args)));
	}

	/**
	 * Return the number of inversions of T objects in ARGS.
	 *
	 * Use a variation of Merge Sort to solve it.
	 */
	public static <T extends Comparable<? super T>> int inversions(List<T> list) {
		if (list.size() > 1) {
			List<T> sub1 = list.subList(0, list.size() / 2);
			List<T> sub2 = list.subList(list.size() / 2, list.size());
			return inversions(sub1) + inversions(sub2) + merge(list, sub1, sub2);
		}
		return 0;
	}

	private static <T extends Comparable<? super T>> int merge(List<T> list,
															   List<T> sub1,
															   List<T> sub2) {
		int inversions = 0;
		ArrayList<T> leftSub = new ArrayList<>(sub1);
		ArrayList<T> rightSub = new ArrayList<>(sub2);
		int leftIndex = leftSub.size() - 1, rightIndex = rightSub.size() - 1;
		int index = list.size() - 1;
		assert leftSub.size() + rightSub.size() == list.size();
		while (leftIndex >= 0 || rightIndex >= 0) {
			if (rightIndex == -1 ||
						(leftIndex >= 0 &&
								 leftSub.get(leftIndex).compareTo(rightSub.get(rightIndex)) > 0)) {
				list.set(index, leftSub.get(leftIndex));
				leftIndex -= 1;
				inversions += rightIndex + 1;
			} else {
				rightIndex -= 1;
			}
			index -= 1;
		}
		return inversions;
	}

}
