# Sorting Algorithms Benchmark (NetBeans)

Compares Selection Sort, Insertion Sort, Bubble Sort, Merge Sort, and Quick Sort on:
1. Random (unsorted) integers
2. Reverse-sorted integers (largest to smallest)

## Open in NetBeans
1. Extract the ZIP.
2. In NetBeans, choose **File > Open Project**.
3. Select the `SortingBenchmarkNetBeans` folder containing `pom.xml`.
4. Open `Source Packages` and run `SortingAlgorithmsBenchmark.java`.

Default size is N=100,000. For a quick test, set program argument `10000` under Project Properties > Run > Arguments.

The program prints timing results in microseconds and saves `benchmark_results.csv`.

At N=100,000, the O(N^2) algorithms can take a long time, especially on reverse-sorted data. Run with 10000 first to check the program, then run the required full-size benchmark. Use actual measured results in your report.
