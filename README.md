# Data Structures: Dynamic Array, Linked List, and Min-Heap

## 1. Overview

This project implements a generic Dynamic Array, a generic singly Linked List, and a Min-Heap in Java. The goal was to compare their correctness and performance under the same workloads. The project also includes validation tests and benchmarks launched from `Main.java`.

The three structures can store values, but they organise those values differently. The Dynamic Array stores elements next to each other in memory, the Linked List connects separate nodes, and the Min-Heap keeps the smallest value at its root.

## 2. Complexity Analysis

The tables use `Omega` for the best case, `Theta` for the average case, and `O` for the worst case.

### Dynamic Array

| Operation | Best | Average | Worst | Auxiliary space |
|---|---:|---:|---:|---:|
| `add(x)` | Omega(1) | Theta(1) amortized | O(n) | O(1), O(n) during resize |
| `add(index, x)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `remove(index)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `get(index)` | Omega(1) | Theta(1) | O(1) | O(1) |
| `contains(x)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `len()` | Omega(1) | Theta(1) | O(1) | O(1) |

`get(index)` is constant time because the array can calculate the address of an element directly. Appending is normally constant time, but when the backing array is full all elements must be copied into a larger array. Insertion and removal at an index shift later elements, so they are linear, especially near the beginning.

### Linked List

| Operation | Best | Average | Worst | Auxiliary space |
|---|---:|---:|---:|---:|
| `add(x)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `add(index, x)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `remove(index)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `get(index)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `contains(x)` | Omega(1) | Theta(n) | O(n) | O(1) |
| `getLength()` | Omega(1) | Theta(1) | O(1) | O(1) |

A Linked List cannot jump directly to index `i`; it must follow links from the head. This makes random access and searching linear. However, adding or removing the first node is constant time because no traversal and no element shifting are needed. The implementation stores its size, so `getLength()` is constant time.

### Min-Heap

| Operation | Best | Average | Worst | Auxiliary space |
|---|---:|---:|---:|---:|
| `insert(x)` | Omega(1) | Theta(log n) | O(n) if resize occurs; otherwise O(log n) | O(1), O(n) during resize |
| `peekMin()` | Omega(1) | Theta(1) | O(1) | O(1) |
| `extractMin()` | Omega(1) | Theta(log n) | O(log n) | O(1) |

The minimum is always at the root, so `peekMin()` is constant time. Insertion may move a value up the tree and extraction may move a value down the tree. A binary heap has height Theta(log n), giving logarithmic insertion and extraction.

## 3. Correctness: Loop-Invariant Proofs

### Dynamic Array: `add(index, x)`

**Invariant.** At the start of each right-shift iteration with index `i`, all original elements from `i` through `size - 1` have already moved one place right, and elements before `i` are unchanged.

**Initialization.** The loop starts with `i = size`. The position at `size` is free after resizing if necessary, so no element has to be shifted yet. The invariant is true.

**Maintenance.** `array[i] = array[i - 1]` moves one original element to its final position without losing another element. After decreasing `i`, the shifted region is one element larger. Therefore, the invariant remains true.

**Termination.** When `i = index`, every original element from `index` to `size - 1` is one position right. Position `index` is free, so placing `x` there and increasing `size` gives the correct array order.

### Dynamic Array: `remove(index)`

**Invariant.** At the start of each left-shift iteration with index `i`, all elements originally after the removed index and before `i` have already moved one position left. Elements from `i` onward are still unchanged.

**Initialization.** The loop begins with `i = index`, so no surviving element has been moved yet. The invariant is true.

**Maintenance.** `array[i] = array[i + 1]` moves the next surviving element into the empty position on its left. After `i` increases, one more element is in its correct final position, preserving the invariant.

**Termination.** The loop ends when all elements after the removed value have shifted left once. Reducing `size` removes the duplicated final value. Thus the target element is gone and every remaining element stays in the original order.

### Linked List: `add(index, x)`

**Invariant.** At the start of each traversal-loop iteration, pointer `n` refers to the node at position `i`, where `i` is the number of links already followed from the head.

**Initialization.** Initially, `i = 0` and `n = head`. The pointer is at the first node, so the invariant holds.

**Maintenance.** Each iteration changes `n` to `n.getNext()` and increases `i` by one. The pointer therefore reaches exactly the next position, maintaining the invariant.

**Termination.** The loop ends with `n` at the node immediately before the insertion position. Connecting the new node between `n` and `n.getNext()` puts it at the required index and keeps the rest of the list connected.

### Linked List: `remove(index)`

**Invariant.** At the start of each traversal-loop iteration, pointer `n` is at position `i`, and all links before `n` are unchanged.

**Initialization.** For a non-zero index, the loop begins with `n = head` and `i = 0`, so the invariant is true.

**Maintenance.** Moving to `n.getNext()` follows exactly one link. No links are changed during traversal, so the prefix of the list is unchanged and the invariant remains true.

**Termination.** The loop stops at the node before the target. Setting `n.setNext(n.getNext().getNext())` skips the target node and joins its predecessor to its successor. For index `0`, assigning `head = head.getNext()` removes the first node directly. In both cases, exactly the requested node is removed.

### Min-Heap: `insert(x)` / heapify-up

**Invariant.** Before each heapify-up iteration, the heap property holds everywhere except possibly between the value at `index` and its parent. The subtrees below `index` are valid heaps.

**Initialization.** The new value is placed at the final free position. It has no children, so only its relation with its parent might be incorrect. The invariant holds.

**Maintenance.** If the value is smaller than its parent, they are swapped and `index` becomes the parent index. The old child position now satisfies the heap property, and any possible violation has moved one level upward. The invariant is preserved.

**Termination.** The loop ends at the root or when the value is not smaller than its parent. The possible violation is resolved, so the whole structure is a valid min-heap containing the new value.

### Min-Heap: `extractMin()` / heapify-down

**Invariant.** Before each heapify-down iteration, every heap subtree is valid except possibly the subtree rooted at the current `index`. The current value may be larger than one of its children.

**Initialization.** The last value replaces the removed root. All subtrees below the root are unchanged and valid, so only the root may violate the heap property. The invariant holds.

**Maintenance.** The smaller child is selected. If the current value is larger, it swaps with that child. The old position is then valid, and only the new position can still violate the property. Thus the invariant remains true.

**Termination.** When no child is smaller than the current value, its subtree is valid. All other subtrees were already valid, so the complete heap satisfies the heap property. The removed root was the minimum because it was no larger than all of its descendants.

## 4. Experimental Setup

The input sizes were `n = 100`, `1,000`, `10,000`, and `100,000`. Every experiment was run five times and the average execution time was recorded using `System.nanoTime()`. The same random seed, `Random(42)`, was used each time. Inputs and random indices were created before timing began, and printing was not included in the measured time.

Workload 1 performed 10,000 random `get(index)` operations. Workload 2 performed 1,000 `contains(value)` operations. Workload 3 performed 1,000 insertions and 1,000 removals at index `0` and index `n / 2`. Workload 4 inserted `n` random values into the heap and extracted the minimum value `n` times. Accesses, movements, and comparisons were counted outside the timed sections.

## 5. Results

The full benchmark tables and plots are [here](https://docs.google.com/document/d/1k1AIi-H07KuuOyrVoZj6C7zQ7fqodFkM/edit?usp=sharing&ouid=105444040156279032950&rtpof=true&sd=true). It contains average execution times, metrics, theoretical complexity, an Execution Time vs. `n` plot, and an Operations/Comparisons/Accesses vs. `n` plot.

## 6. Discussion

1. **Effect of increasing `n`.** Random Dynamic Array access stayed below 0.6 ms for 10,000 accesses, while Linked List access grew from about 1.8 ms at `n = 100` to 972 ms at `n = 100,000`. Search times grew for both structures: the array increased from 0.28 ms to 20.4 ms and the list from 0.39 ms to 156 ms. Beginning and middle updates also became more expensive for the Dynamic Array because more values had to shift. Heap processing increased as more values were inserted and extracted; extraction increased from 0.13 ms to 17.1 ms.

2. **Results that agree with theory.** The random-access experiment strongly agrees with Dynamic Array `get(index)` being Theta(1) and Linked List `get(index)` being Theta(n). Search comparisons rose from about 75,000 to about 75 million as `n` increased, supporting Theta(n) search for both structures. Linked List insertion and removal at the beginning stayed close to constant time, while middle operations grew because they require traversal. Heap insert and extraction times and comparison counts increased consistently with Theta(log n) per operation and Theta(n log n) for all priority processing.

3. **Differences from the theoretical prediction.** The measured times did not increase in a perfectly smooth curve. For example, the Dynamic Array random-access time at `n = 1,000` was lower than at `n = 100`. This does not contradict Theta(1): the work per access is still constant, but JVM warm-up, garbage collection, CPU caching, and scheduling affect small timing measurements.

4. **Same Big-O, different running time.** Big-O describes how running time grows, not the exact number of instructions. Both array and list search are Theta(n), but at `n = 100,000` the array took about 20.4 ms and the list about 156 ms. The array accesses neighbouring memory locations, while the list follows references between separate nodes.

5. **Constant factors and implementation details.** Arrays have good cache locality and no separate node allocations, so their constant factors are small. Linked Lists perform pointer dereferences and allocate nodes, which increases their real cost. Resizing an array also creates occasional expensive operations even though append is Theta(1) amortized. These details explain why practical performance can differ even when asymptotic complexity is similar.

6. **Why a Dynamic Array is preferable.** It is preferable for indexed access, iteration, searching, and append-heavy workloads. The random-access results show this clearly: it remained fast for every input size while the Linked List slowed down sharply. It is less suitable for frequent insertions or removals near the beginning because values must be shifted.

7. **When a Linked List is useful.** A Linked List is useful when additions and removals happen at the beginning, or when the needed node is already known. In the benchmark, beginning insertion for the list stayed around 0.04 ms even at `n = 100,000`, while the array needed about 124 ms because it moved many elements. It is not a good choice for repeated random access.

8. **Why a Heap fits priority processing.** A Min-Heap always keeps the smallest value at the root, so `peekMin()` is constant time and `extractMin()` is logarithmic. Every heap run returned elements in non-decreasing order. At `n = 100,000`, the heap completed all extractions in about 17.1 ms, showing that it can repeatedly process priorities without sorting all values after every insertion.

9. **How the workload determines the choice.** The choice should be based on the operations that dominate the program. Use a Dynamic Array for frequent indexing and iteration, a Linked List for frequent beginning modifications, and a Min-Heap when the next smallest or highest-priority item is repeatedly needed. The experiments show that choosing a structure only because it stores the required values is not enough; the workload determines its actual performance.

## 7. Design Recommendations

The Dynamic Array is a good choice for frequent indexed access, iteration, and append-heavy workloads. A Linked List is useful for insertion or removal at the beginning, or when the needed node is already known, but it is poor for repeated random access. A Min-Heap is appropriate for priority-based processing because it returns the smallest value efficiently without sorting the complete collection after every insertion.

The workload should decide the choice of data structure. The needed access pattern, update position, and priority requirements matter more than simply storing the same values.

## 8. Conclusion

The experiments and theory lead to the same conclusion: Dynamic Arrays are strong for direct access, Linked Lists are useful for local beginning modifications, and Min-Heaps are effective for priority processing. The validation tests, loop-invariant proofs, and benchmark results show that internal representation directly affects performance.
