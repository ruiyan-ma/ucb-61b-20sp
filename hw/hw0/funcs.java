public class funcs {

	/**
	 * return the max value in an array
	 */
	public static int max(int[] a) {
		if (a.length <= 0) {
			System.out.println("Please input the right array.");
		} else {
			int flag = 0;
			for (int i = 1; i < a.length; i++) {
				if (a[i] > a[flag]) {
					flag = i;
				}
			}
			return a[flag];
		}
		return 0;
	}

	/**
	 * solve 3sum problem using 3 pointer method, complexity O(n**2)
	 */
	public static boolean threeSum(int[] a) {
		sort(a);
		int limit = 0;
		double limit_value = a[a.length - 1] >> 1;
		for (int i = a.length - 1; i > 0; i--) {
			if (a[i] < limit_value) {
				limit = i;
				break;
			}
		}
		for (int i = 0; i <= limit; i++) {
			for (int j = i, k = a.length - 1; k >= j; ) {
				if (a[i] + a[j] + a[k] > 0) {
					k--;
				} else if (a[i] + a[j] + a[k] < 0) {
					j++;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * sort the array
	 */
	public static void sort(int[] a) {
		int temp;
		for (int i = 0; i < a.length; i++) {
			int flag = i;
			for (int j = i; j < a.length; j++) {
				if (a[j] < a[flag]) {
					flag = j;
				}
			}
			temp = a[i];
			a[i] = a[flag];
			a[flag] = temp;
		}
	}

	/**
	 * solve 3sum problem with 3 distinct number in the array, using 3
	 * pointer method, complexity O(n**2)
	 */
	public static boolean threeSumDistinct(int[] a) {
		sort(a);
		int limit = 0;
		double limit_value = a[a.length - 1] >> 1;
		for (int i = a.length - 1; i > 0; i--) {
			if (a[i] < limit_value) {
				limit = i;
				break;
			}
		}
		for (int i = 0; i <= limit; i++) {
			for (int j = i + 1, k = a.length - 1; j < k; ) {
				if (a[i] + a[j] + a[k] > 0) {
					k--;
				} else if (a[i] + a[j] + a[k] < 0) {
					j++;
				} else {
					return true;
				}
			}
		}
		return false;
	}

}
