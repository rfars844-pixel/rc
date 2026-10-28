import java.util.Arrays;
import java.util.Random;

/**
 * Sorting Algorithms Benchmark for NetBeans.
 * Test cases: Random (unsorted) and Reverse Sorted.
 * Default N = 100,000. Use program argument 10000 for a quick test.
 */
public class SortingAlgorithmsBenchmark {
    private static final int DEFAULT_SIZE = 100_000;
    private static final long SEED = 2026L;

    @FunctionalInterface
    private interface Sorter { void sort(int[] a); }

    private static final class Algorithm {
        final String name;
        final Sorter sorter;
        Algorithm(String name, Sorter sorter) { this.name = name; this.sorter = sorter; }
    }

    public static void main(String[] args) {
        int n = DEFAULT_SIZE;
        if (args.length > 0) {
            try {
                n = Integer.parseInt(args[0]);
                if (n < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("Enter a positive array size, e.g. 10000.");
                return;
            }
        }

        Algorithm[] algorithms = {
            new Algorithm("Selection Sort", SortingAlgorithmsBenchmark::selectionSort),
            new Algorithm("Insertion Sort", SortingAlgorithmsBenchmark::insertionSort),
            new Algorithm("Bubble Sort", SortingAlgorithmsBenchmark::bubbleSort),
            new Algorithm("Merge Sort", SortingAlgorithmsBenchmark::mergeSort),
            new Algorithm("Quick Sort", SortingAlgorithmsBenchmark::quickSort)
        };
        String[] cases = {"Random (Unsorted)", "Reverse Sorted"};
        int[][] originals = {randomArray(n, SEED), reverseArray(n)};
        long[][] times = new long[algorithms.length][cases.length];

        System.out.println("SORTING ALGORITHMS BENCHMARK");
        System.out.println("Array size N = " + n);
        System.out.println("Times are in microseconds (us).");
        System.out.println("Warning: O(N^2) algorithms may take a long time at N=100,000.");
        System.out.println();

        for (int i = 0; i < algorithms.length; i++) {
            for (int j = 0; j < cases.length; j++) {
                int[] test = Arrays.copyOf(originals[j], originals[j].length);
                long start = System.nanoTime();
                algorithms[i].sorter.sort(test);
                long end = System.nanoTime();
                times[i][j] = (end - start) / 1000L;
                if (!isSorted(test)) {
                    throw new IllegalStateException(algorithms[i].name + " failed on " + cases[j]);
                }
            }
        }

        System.out.printf("%-20s | %20s | %20s%n", "Algorithm", cases[0], cases[1]);
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < algorithms.length; i++) {
            System.out.printf("%-20s | %20d | %20d%n",
                    algorithms[i].name, times[i][0], times[i][1]);
        }
        saveCsv(algorithms, cases, times);
        System.out.println("\\nResults saved to benchmark_results.csv");
    }

    private static int[] randomArray(int n, long seed) {
        int[] a = new int[n];
        Random r = new Random(seed);
        for (int i = 0; i < n; i++) a[i] = r.nextInt();
        return a;
    }

    private static int[] reverseArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }

    private static void saveCsv(Algorithm[] algorithms, String[] cases, long[][] times) {
        try (java.io.PrintWriter out = new java.io.PrintWriter("benchmark_results.csv", "UTF-8")) {
            out.println("Algorithm," + cases[0] + "," + cases[1]);
            for (int i = 0; i < algorithms.length; i++)
                out.printf("%s,%d,%d%n", algorithms[i].name, times[i][0], times[i][1]);
        } catch (java.io.IOException e) {
            System.err.println("Could not save CSV: " + e.getMessage());
        }
    }

    // Selection Sort: find the smallest remaining item. O(N^2).
    public static void selectionSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) if (a[j] < a[min]) min = j;
            int t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }

    // Insertion Sort: insert each item into the sorted left side.
    public static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i], j = i - 1;
            while (j >= 0 && a[j] > key) { a[j + 1] = a[j]; j--; }
            a[j + 1] = key;
        }
    }

    // Bubble Sort: swap adjacent out-of-order items; stop if already sorted.
    public static void bubbleSort(int[] a) {
        for (int end = a.length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int j = 0; j < end; j++) {
                if (a[j] > a[j + 1]) {
                    int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    // Merge Sort: divide, sort halves, and merge. O(N log N).
    public static void mergeSort(int[] a) {
        if (a.length < 2) return;
        mergeSort(a, new int[a.length], 0, a.length - 1);
    }

    private static void mergeSort(int[] a, int[] temp, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(a, temp, left, mid);
        mergeSort(a, temp, mid + 1, right);
        if (a[mid] <= a[mid + 1]) return;
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) temp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
        while (i <= mid) temp[k++] = a[i++];
        while (j <= right) temp[k++] = a[j++];
        System.arraycopy(temp, left, a, left, right - left + 1);
    }

    // Quick Sort: randomized pivot; expected O(N log N).
    public static void quickSort(int[] a) {
        quickSort(a, 0, a.length - 1, new Random(SEED + 1));
    }

    private static void quickSort(int[] a, int low, int high, Random random) {
        while (low < high) {
            int pivot = a[low + random.nextInt(high - low + 1)];
            int i = low, j = high;
            while (i <= j) {
                while (a[i] < pivot) i++;
                while (a[j] > pivot) j--;
                if (i <= j) {
                    int t = a[i]; a[i] = a[j]; a[j] = t; i++; j--;
                }
            }
            if (j - low < high - i) {
                if (low < j) quickSort(a, low, j, random);
                low = i;
            } else {
                if (i < high) quickSort(a, i, high, random);
                high = j;
            }
        }
    }
}
