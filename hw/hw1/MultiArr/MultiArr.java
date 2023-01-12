/** Multidimensional array
 *  @author Shaw Ma
 */

public class MultiArr {

    /**
    {{“hello”,"you",”world”} ,{“how”,”are”,”you”}} prints:
    Rows: 2
    Columns: 3

    {{1,3,4},{1},{5,6,7,8},{7,9}} prints:
    Rows: 4
    Columns: 4
    */

    /** Find the biggest column number of this 2 dimentional arr.
     *  @return cols
     *  @param arr is the array. */
    public static int findcolummns(int[][] arr) {
        int cols = arr[0].length;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i].length > cols)
                cols = arr[i].length;
        }
        return cols;
    }

    public static void printRowAndCol(int[][] arr) {
        //TODO: Your code here!
        int rows = arr.length;
        int cols = findcolummns(arr);
        System.out.println("Rows:" + rows);
        System.out.println("Columns:" + cols);
    }

    /**
    @param arr: 2d array
    @return maximal value present anywhere in the 2d array
    */
    public static int maxValue(int[][] arr) {
        //TODO: Your code here!
        int max = arr[0][0];
        int rows = arr.length;
        for (int i = 0; i < rows; i++) {
            int cols = arr[i].length;
            for (int j = 0; j < cols; j++)
                if (arr[i][j] > max)
                    max = arr[i][j];
        }
        return max;
    }

    /**Return an array where each element is the sum of the
    corresponding row of the 2d array*/
    public static int[] allRowSums(int[][] arr) {
        //TODO: Your code here!!
        int rows = arr.length;
        int[] rowsum = new int[arr.length];
        for (int i = 0; i < rows; i++) {
            int cols = arr[i].length;
            for (int j = 0; j < cols; j++)
                rowsum[i] += arr[i][j];
        }
        return rowsum;
    }
}
