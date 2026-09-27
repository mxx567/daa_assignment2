package datatypes;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class Experiments {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int RUNS = 5;
    private static final int ACCESS_OPERATIONS = 10_000;
    private static final int SEARCH_OPERATIONS = 1_000;
    private static final int UPDATE_OPERATIONS = 1_000;
    private static volatile long blackhole;

    public static void run() throws IOException {
        Path output = Path.of("benchmark-results");
        Files.createDirectories(output);

        randomAccess(output.resolve("random_access.csv"));
        search(output.resolve("search.csv"));
        insertionRemoval(output.resolve("insertion_removal.csv"));
        priorityProcessing(output.resolve("priority_processing.csv"));

        System.out.println("Finished. Results: " + output.toAbsolutePath());
    }

    private static void randomAccess(Path file) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("n,array_avg_ns,array_accesses,list_avg_ns,list_node_accesses");
            for (int n : SIZES) {
                System.out.println("Workload 1 - n = " + n);
                Random random = new Random(42);
                Integer[] initial = randomValues(n, random);
                int[] indices = randomIndices(ACCESS_OPERATIONS, n, random);
                long arrayTime = 0;
                long listTime = 0;

                for (int run = 0; run < RUNS; run++) {
                    DynamicArray<Integer> array = new DynamicArray<>(initial.clone());
                    long sum = 0;
                    long start = System.nanoTime();
                    for (int index : indices) sum += array.get(index);
                    arrayTime += System.nanoTime() - start;
                    blackhole ^= sum;

                    LinkedList<Integer> list = makeList(initial);
                    sum = 0;
                    start = System.nanoTime();
                    for (int index : indices) sum += list.get(index).getValue();
                    listTime += System.nanoTime() - start;
                    blackhole ^= sum;
                }

                out.printf("%d,%d,%d,%d,%d%n", n, arrayTime / RUNS, indices.length,
                        listTime / RUNS, linkedGetAccesses(indices));
            }
        }
    }

    private static void search(Path file) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("n,array_avg_ns,array_comparisons,list_avg_ns,list_comparisons");
            for (int n : SIZES) {
                System.out.println("Workload 2 - n = " + n);
                Random random = new Random(42);
                Integer[] initial = randomValues(n, random);
                Integer[] targets = searchTargets(initial, random);
                long arrayComparisons = linearSearchComparisons(initial, targets, false);
                long listComparisons = linearSearchComparisons(initial, targets, true);
                long arrayTime = 0;
                long listTime = 0;

                for (int run = 0; run < RUNS; run++) {
                    DynamicArray<Integer> array = new DynamicArray<>(initial.clone());
                    long start = System.nanoTime();
                    for (Integer target : targets) blackhole ^= array.contains(target) ? 1 : 0;
                    arrayTime += System.nanoTime() - start;

                    LinkedList<Integer> list = makeList(initial);
                    start = System.nanoTime();
                    for (Integer target : targets) blackhole ^= list.contains(target) ? 1 : 0;
                    listTime += System.nanoTime() - start;
                }

                out.printf("%d,%d,%d,%d,%d%n", n, arrayTime / RUNS, arrayComparisons,
                        listTime / RUNS, listComparisons);
            }
        }
    }

    private static void insertionRemoval(Path file) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("n,operation,array_avg_ns,array_movements,list_avg_ns,list_node_accesses");
            for (int n : SIZES) {
                System.out.println("Workload 3 - n = " + n);
                Random random = new Random(42);
                Integer[] initial = randomValues(n, random);
                Integer[] inserted = randomValues(UPDATE_OPERATIONS, random);
                int middle = n / 2;

                writeUpdate(out, n, "insert_beginning", insertArray(initial, inserted, 0),
                        arrayInsertionMovements(n, 0), insertList(initial, inserted, 0),
                        listInsertionAccesses(0));
                writeUpdate(out, n, "remove_beginning", removeArray(initial, 0),
                        arrayRemovalMovements(n, 0), removeList(initial, 0),
                        listRemovalAccesses(0));
                writeUpdate(out, n, "insert_middle", insertArray(initial, inserted, middle),
                        arrayInsertionMovements(n, middle), insertList(initial, inserted, middle),
                        listInsertionAccesses(middle));
                writeUpdate(out, n, "remove_middle", removeArray(initial, middle),
                        arrayRemovalMovements(n, middle), removeList(initial, middle),
                        listRemovalAccesses(middle));
            }
        }
    }

    private static void priorityProcessing(Path file) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("n,insert_avg_ns,insert_comparisons,extract_avg_ns,extract_comparisons,sorted");
            for (int n : SIZES) {
                System.out.println("Workload 4 - n = " + n);
                Integer[] values = randomValues(n, new Random(42));
                HeapMetrics metrics = countHeapComparisons(values);
                long insertTime = 0;
                long extractTime = 0;
                boolean sorted = true;

                for (int run = 0; run < RUNS; run++) {
                    MinHeap heap = new MinHeap(n);
                    long start = System.nanoTime();
                    for (Integer value : values) heap.insert(value);
                    insertTime += System.nanoTime() - start;

                    int previous = Integer.MIN_VALUE;
                    start = System.nanoTime();
                    for (int i = 0; i < n; i++) {
                        int current = heap.extractMin();
                        if (current < previous) sorted = false;
                        previous = current;
                    }
                    extractTime += System.nanoTime() - start;
                }

                out.printf("%d,%d,%d,%d,%d,%s%n", n, insertTime / RUNS,
                        metrics.insertComparisons, extractTime / RUNS,
                        metrics.extractComparisons, sorted);
            }
        }
    }

    private static void writeUpdate(PrintWriter out, int n, String operation, long arrayTime,
                                    long arrayMetric, long listTime, long listMetric) {
        out.printf("%d,%s,%d,%d,%d,%d%n", n, operation, arrayTime, arrayMetric,
                listTime, listMetric);
    }

    private static long insertArray(Integer[] initial, Integer[] values, int index) {
        long total = 0;
        for (int run = 0; run < RUNS; run++) {
            DynamicArray<Integer> array = new DynamicArray<>(initial.clone());
            long start = System.nanoTime();
            for (Integer value : values) array.add(index, value);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long insertList(Integer[] initial, Integer[] values, int index) {
        long total = 0;
        for (int run = 0; run < RUNS; run++) {
            LinkedList<Integer> list = makeList(initial);
            long start = System.nanoTime();
            for (Integer value : values) list.add(index, value);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long removeArray(Integer[] initial, int index) {
        long total = 0;
        for (int run = 0; run < RUNS; run++) {
            DynamicArray<Integer> array = new DynamicArray<>(initial.clone());
            long time = 0;
            for (int operation = 0; operation < UPDATE_OPERATIONS; operation++) {
                long start = System.nanoTime();
                Integer removed = array.remove(index);
                time += System.nanoTime() - start;
                array.add(index, removed);
            }
            total += time;
        }
        return total / RUNS;
    }

    private static long removeList(Integer[] initial, int index) {
        long total = 0;
        for (int run = 0; run < RUNS; run++) {
            LinkedList<Integer> list = makeList(initial);
            long time = 0;
            for (int operation = 0; operation < UPDATE_OPERATIONS; operation++) {
                long start = System.nanoTime();
                Node<Integer> removed = list.remove(index);
                time += System.nanoTime() - start;
                list.add(index, removed.getValue());
            }
            total += time;
        }
        return total / RUNS;
    }

    private static LinkedList<Integer> makeList(Integer[] values) {
        LinkedList<Integer> list = new LinkedList<>();
        for (Integer value : values) list.add(0, value);
        return list;
    }

    private static Integer[] randomValues(int count, Random random) {
        Integer[] values = new Integer[count];
        for (int i = 0; i < count; i++) values[i] = random.nextInt(1_000_000);
        return values;
    }

    private static int[] randomIndices(int count, int upperBound, Random random) {
        int[] indices = new int[count];
        for (int i = 0; i < count; i++) indices[i] = random.nextInt(upperBound);
        return indices;
    }

    private static Integer[] searchTargets(Integer[] values, Random random) {
        Integer[] targets = new Integer[SEARCH_OPERATIONS];
        for (int i = 0; i < SEARCH_OPERATIONS / 2; i++) {
            targets[i] = values[random.nextInt(values.length)];
        }
        for (int i = SEARCH_OPERATIONS / 2; i < SEARCH_OPERATIONS; i++) {
            targets[i] = -1 - random.nextInt(1_000_000);
        }
        return targets;
    }

    private static long linkedGetAccesses(int[] indices) {
        long accesses = 0;
        for (int index : indices) accesses += index + 1L;
        return accesses;
    }

    private static long linearSearchComparisons(Integer[] values, Integer[] targets,
                                                boolean reverseOrder) {
        long comparisons = 0;
        for (Integer target : targets) {
            if (reverseOrder) {
                for (int i = values.length - 1; i >= 0; i--) {
                    comparisons++;
                    if (values[i].equals(target)) break;
                }
            } else {
                for (Integer value : values) {
                    comparisons++;
                    if (value.equals(target)) break;
                }
            }
        }
        return comparisons;
    }

    private static long arrayInsertionMovements(int originalSize, int index) {
        int size = originalSize;
        int capacity = originalSize;
        long movements = 0;
        for (int operation = 0; operation < UPDATE_OPERATIONS; operation++) {
            if (size == capacity) {
                movements += size;
                capacity = Math.max(1, capacity * 2);
            }
            movements += size - index;
            size++;
        }
        return movements;
    }

    private static long arrayRemovalMovements(int size, int index) {
        return (long) (size - index - 1) * UPDATE_OPERATIONS;
    }

    private static long listInsertionAccesses(int index) {
        return (long) index * UPDATE_OPERATIONS;
    }

    private static long listRemovalAccesses(int index) {
        return (long) (index + 1) * UPDATE_OPERATIONS;
    }

    private static HeapMetrics countHeapComparisons(Integer[] values) {
        int[] heap = new int[values.length];
        int size = 0;
        long insertComparisons = 0;
        long extractComparisons = 0;

        for (Integer value : values) {
            heap[size] = value;
            int index = size++;
            while (index > 0) {
                int parent = (index - 1) / 2;
                insertComparisons++;
                if (heap[index] >= heap[parent]) break;
                int temporary = heap[index];
                heap[index] = heap[parent];
                heap[parent] = temporary;
                index = parent;
            }
        }

        while (size > 0) {
            heap[0] = heap[size - 1];
            size--;
            int index = 0;
            while (true) {
                int left = 2 * index + 1;
                int right = 2 * index + 2;
                int smallest = index;
                if (left < size) {
                    extractComparisons++;
                    if (heap[left] < heap[smallest]) smallest = left;
                }
                if (right < size) {
                    extractComparisons++;
                    if (heap[right] < heap[smallest]) smallest = right;
                }
                if (smallest == index) break;
                int temporary = heap[index];
                heap[index] = heap[smallest];
                heap[smallest] = temporary;
                index = smallest;
            }
        }
        return new HeapMetrics(insertComparisons, extractComparisons);
    }

    private static class HeapMetrics {
        private final long insertComparisons;
        private final long extractComparisons;

        private HeapMetrics(long insertComparisons, long extractComparisons) {
            this.insertComparisons = insertComparisons;
            this.extractComparisons = extractComparisons;
        }
    }
}
