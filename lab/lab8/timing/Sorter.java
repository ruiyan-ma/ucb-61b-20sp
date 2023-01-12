package timing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Sorter {
    /** Sort the given input array ARRAY. */
    public abstract void sort(int[] array);

    /** Returns an array of the specified size filled with
     * randomly generated values. */
    public int[] getRandomArray(int nElements) {
        int[] array = new int[nElements];
        Random random = new Random();
        for (int k = 0; k < nElements; k++) {
            array[k] = random.nextInt();
        }
        return array;
    }

    /** Conduct a timing experiment. This will involve sorting
     * NTRIALS different size arrays starting at 5 and increasing
     * in size by BY each time. The experiments will then be ran
     * NREPEATS times. */
    public List<Double> score(int by, int ntrials, int nrepeats) {
        /* Set min to reasonably small number */
        int min = 5;
        /* Calculate max on interval and number of trials */
        int max = min + by * ntrials;

        Timer t = new Timer();
        List<Double> scores = new ArrayList<>();

        /* For each size of array */
        for (int nElement = min; nElement < max; nElement += by) {
            List<Double> scorePerSize = new ArrayList<>();
            /* For each repeat of sorting of array */
            for (int i = 0; i < nrepeats; i++) {
                int[] array = getRandomArray(nElement);
                t.start();
                sort(array);
                double elapsedMs = t.stop();
                scorePerSize.add(elapsedMs);
            }
            double total = 0;
            for (Double aDouble : scorePerSize) {
                total += aDouble;
            }
            scores.add(total / ntrials);
        }
        return scores;
    }
}

class BubbleSorter extends Sorter {
    @Override
    /* A forward pass is taken and any adjacent out of order
       elements will be swapped until a forward pass is made
       and there are no swaps necessary. Worst case Θ(N^2). */
    public void sort(int[] array) {
        boolean finished = false;
        while (!finished) {
            finished = true;
            for (int j = 0; j < array.length - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                    finished = false;
                }
            }
        }
    }
}

class WipingBubbleSorter extends Sorter {
    @Override
    /* This function will alternate between taking forward passes
       and backwards passes through the array. Worst case Θ(N^2). */
    public void sort(int[] array) {
        boolean finished = false;
        boolean even = true;
        while (!finished) {
            finished = true;
            if (even) {
                for (int j = 0; j < array.length - 1; j++) {
                    if (array[j] > array[j + 1]) {
                        int temp = array[j + 1];
                        array[j + 1] = array[j];
                        array[j] = temp;
                        finished = false;
                    }
                }
                even = false;
            } else {
                for (int j = array.length - 1; j > 0; j--) {
                    if (array[j] < array[j - 1]) {
                        int temp = array[j - 1];
                        array[j - 1] = array[j];
                        array[j] = temp;
                        finished = false;
                    }
                }
                even = true;
            }
        }
    }
}

class InsertionSorter extends Sorter {
    @Override
    /* This algorithm will build a progressively larger sublist
       of sorted numbers, by continually inserting the next value
       in sorted sublist. Worst case Θ(N^2). */
    public void sort(int[] array) {
        for (int k = 1; k < array.length; k++) {
            // Insert element k into its correct position, so that
            //   array[0] <= array[1] <= ... <= array[k-1].
            int temp = array[k];
            int j;
            for (j = k - 1; j >= 0 && array[j] > temp; j--) {
                array[j + 1] = array[j];
            }
            array[j + 1] = temp;
        }
    }
}

class JavaSorter extends Sorter {
    /** Java's native sorting algorithm, quicksort for primitive
     *  values and mergesort for object values. Complexity Θ(Nlog(N)). */
    public void sort(int[] array) {
        Arrays.sort(array);
    }
}

class CountingSorter extends Sorter {
    @Override
    public int[] getRandomArray(int nElements) {
        int[] array = new int[nElements];
        Random random = new Random();
        for (int k = 0; k < nElements; k++) {
            array[k] = random.nextInt(10);
        }
        return array;
    }

    @Override
    public void sort(int[] array) {
        int[] counts = new int[10];
        for (Integer i : array) {
            counts[i] += 1;
        }
        int index = 0;
        for (int i = 0; i < 10; i += 1) {
            while (counts[i] > 0) {
                array[index] = i;
                counts[i] -= 1;
                index += 1;
            }
        }
    }
}
