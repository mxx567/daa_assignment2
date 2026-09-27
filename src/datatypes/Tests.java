package datatypes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;

public class Tests {
    private static int passed;
    private static int failed;

    public static void run() {
        runTest("DynamicArray: empty structure", Tests::testDynamicArrayEmpty);
        runTest("DynamicArray: multiple values and boundaries", Tests::testDynamicArrayOperations);
        runTest("DynamicArray: duplicates and invalid indices", Tests::testDynamicArrayDuplicatesAndInvalidIndices);
        runTest("DynamicArray: large input", Tests::testDynamicArrayLargeInput);

        runTest("LinkedList: empty structure", Tests::testLinkedListEmpty);
        runTest("LinkedList: multiple values and boundaries", Tests::testLinkedListOperations);
        runTest("LinkedList: duplicates and invalid indices", Tests::testLinkedListDuplicatesAndInvalidIndices);
        runTest("LinkedList: large input", Tests::testLinkedListLargeInput);

        runTest("MinHeap: empty and one element", Tests::testHeapEmptyAndOneElement);
        runTest("MinHeap: values, duplicates, and heap property", Tests::testHeapOperations);
        runTest("MinHeap: large input and sorted extraction", Tests::testHeapLargeInput);

        System.out.println("\nPassed: " + passed + ", Failed: " + failed);
        if (failed > 0) {
            throw new AssertionError("Some validation tests failed.");
        }
    }

    private static void testDynamicArrayEmpty() {
        DynamicArray<Integer> array = new DynamicArray<>(new Integer[0]);
        check(!array.contains(10), "Empty array must not contain a value");
        array.add(10);
        checkEquals(10, array.get(0), "Value added to an empty array");
    }

    private static void testDynamicArrayOperations() {
        DynamicArray<Integer> array = new DynamicArray<>(new Integer[]{1, 2, 3});
        List<Integer> expected = new ArrayList<>(List.of(1, 2, 3));

        array.add(0, 0);
        expected.add(0, 0);
        array.add(2, 9);
        expected.add(2, 9);
        array.add(expected.size(), 4);
        expected.add(4);
        assertDynamicArrayEquals(array, expected);

        checkEquals(expected.remove(0), array.remove(0), "Remove first element");
        checkEquals(expected.remove(2), array.remove(2), "Remove middle element");
        assertDynamicArrayEquals(array, expected);
    }

    private static void testDynamicArrayDuplicatesAndInvalidIndices() {
        DynamicArray<String> array = new DynamicArray<>(new String[]{"A", "A", "B"});
        check(array.contains(new String("A")), "contains must compare duplicate values with equals");
        expectThrows(IndexOutOfBoundsException.class, () -> array.add(-1, "X"));
        expectThrows(IndexOutOfBoundsException.class, () -> array.add(4, "X"));
        expectThrows(IndexOutOfBoundsException.class, () -> array.get(3));
        expectThrows(IndexOutOfBoundsException.class, () -> array.remove(3));
    }

    private static void testDynamicArrayLargeInput() {
        int n = 100_000;
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) values[i] = i;
        DynamicArray<Integer> array = new DynamicArray<>(values);
        checkEquals(0, array.get(0), "First large-input value");
        checkEquals(n - 1, array.get(n - 1), "Last large-input value");
        array.add(n, n);
        checkEquals(n, array.get(n), "Append after large input");
    }

    private static void testLinkedListEmpty() {
        LinkedList<Integer> list = new LinkedList<>();
        checkEquals(0, list.getLength(), "Empty list length");
        check(!list.contains(10), "Empty list must not contain a value");
        expectThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        expectThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    private static void testLinkedListOperations() {
        LinkedList<Integer> list = new LinkedList<>();
        List<Integer> expected = new ArrayList<>();

        list.add(1); expected.add(1);
        list.add(2); expected.add(2);
        list.add(0, 0); expected.add(0, 0);
        list.add(2, 9); expected.add(2, 9);
        list.add(expected.size(), 4); expected.add(4);
        assertLinkedListEquals(list, expected);

        checkEquals(expected.remove(0), list.remove(0).getValue(), "Remove first list element");
        checkEquals(expected.remove(2), list.remove(2).getValue(), "Remove middle list element");
        assertLinkedListEquals(list, expected);
    }

    private static void testLinkedListDuplicatesAndInvalidIndices() {
        LinkedList<String> list = new LinkedList<>();
        list.add("A");
        list.add("A");
        list.add("B");
        check(list.contains(new String("A")), "contains must compare duplicate values with equals");
        expectThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "X"));
        expectThrows(IndexOutOfBoundsException.class, () -> list.add(4, "X"));
        expectThrows(IndexOutOfBoundsException.class, () -> list.get(3));
        expectThrows(IndexOutOfBoundsException.class, () -> list.remove(3));
    }

    private static void testLinkedListLargeInput() {
        LinkedList<Integer> list = new LinkedList<>();
        int n = 100_000;
        for (int i = 0; i < n; i++) list.add(0, i);
        checkEquals(n, list.getLength(), "Large list length");
        checkEquals(n - 1, list.get(0).getValue(), "Large list first value");
        checkEquals(0, list.get(n - 1).getValue(), "Large list last value");
    }

    private static void testHeapEmptyAndOneElement() {
        MinHeap heap = new MinHeap(1);
        expectThrows(IllegalStateException.class, heap::peekMin);
        expectThrows(IllegalStateException.class, heap::extractMin);
        heap.insert(7);
        assertHeapProperty(heap);
        checkEquals(7, heap.peekMin(), "Peek one heap value");
        checkEquals(7, heap.extractMin(), "Extract one heap value");
        assertHeapProperty(heap);
    }

    private static void testHeapOperations() {
        int[] values = {5, 1, 4, 1, 9, 2, 6, 5, 3};
        MinHeap heap = new MinHeap(2);
        PriorityQueue<Integer> reference = new PriorityQueue<>();

        for (int value : values) {
            heap.insert(value);
            reference.add(value);
            assertHeapProperty(heap);
            checkEquals(reference.peek(), heap.peekMin(), "peekMin must match PriorityQueue");
        }

        int previous = Integer.MIN_VALUE;
        while (!reference.isEmpty()) {
            int actual = heap.extractMin();
            int expected = reference.poll();
            checkEquals(expected, actual, "extractMin must match PriorityQueue");
            check(actual >= previous, "Extracted values must be non-decreasing");
            previous = actual;
            assertHeapProperty(heap);
        }
    }

    private static void testHeapLargeInput() {
        int n = 100_000;
        Random random = new Random(42);
        MinHeap heap = new MinHeap(n);
        PriorityQueue<Integer> reference = new PriorityQueue<>();

        for (int i = 0; i < n; i++) {
            int value = random.nextInt();
            heap.insert(value);
            reference.add(value);
        }
        assertHeapProperty(heap);

        int previous = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int actual = heap.extractMin();
            checkEquals(reference.poll(), actual, "Large heap extraction must match PriorityQueue");
            check(actual >= previous, "Large heap output must remain sorted");
            previous = actual;
            if (i % 1_000 == 0) assertHeapProperty(heap);
        }
    }

    private static void assertDynamicArrayEquals(DynamicArray<Integer> actual, List<Integer> expected) {
        for (int i = 0; i < expected.size(); i++) {
            checkEquals(expected.get(i), actual.get(i), "DynamicArray value at index " + i);
        }
    }

    private static void assertLinkedListEquals(LinkedList<Integer> actual, List<Integer> expected) {
        checkEquals(expected.size(), actual.getLength(), "LinkedList length");
        for (int i = 0; i < expected.size(); i++) {
            checkEquals(expected.get(i), actual.get(i).getValue(), "LinkedList value at index " + i);
        }
    }

    private static void assertHeapProperty(MinHeap heap) {
        try {
            Field heapField = MinHeap.class.getDeclaredField("heap");
            Field sizeField = MinHeap.class.getDeclaredField("size");
            heapField.setAccessible(true);
            sizeField.setAccessible(true);
            int[] values = (int[]) heapField.get(heap);
            int size = sizeField.getInt(heap);

            for (int child = 1; child < size; child++) {
                int parent = (child - 1) / 2;
                check(values[parent] <= values[child], "Heap property violated at index " + child);
            }
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not inspect MinHeap", exception);
        }
    }

    private static void expectThrows(Class<? extends Throwable> expected, CheckedRunnable operation) {
        try {
            operation.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) return;
            throw new AssertionError("Expected " + expected.getSimpleName() + " but got " + actual, actual);
        }
        throw new AssertionError("Expected " + expected.getSimpleName() + " but nothing was thrown");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void checkEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    private static void runTest(String name, CheckedRunnable test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS: " + name);
        } catch (Throwable error) {
            failed++;
            System.out.println("FAIL: " + name + " -> " + error.getMessage());
        }
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
