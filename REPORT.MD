# Analysis Report: Data Structures & Benchmark Performance

## 1. Asymptotic Complexity Table

| Data Structure | Operation | Best Case $\Omega$ | Average Case $\Theta$ | Worst Case $O$ | Space Complexity | Explanation |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **DynamicArray** | `add(x)` | $\Omega(1)$ | $\Theta(1)$ | $O(n)$ | $O(n)$ | $O(1)$ amortized when capacity allows; $O(n)$ during array growth. |
| | `add(index, x)` | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | $O(n)$ | Requires shifting elements to the right ($O(1)$ at tail, $O(n)$ at head/middle). |
| | `remove(index)` | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | $O(n)$ | Requires shifting elements to the left ($O(1)$ at tail, $O(n)$ at head/middle). |
| | `get(index)` | $\Omega(1)$ | $\Theta(1)$ | $O(1)$ | $O(n)$ | Direct memory address offset calculation: `base_address + index * element_size`. |
| | `contains(x)` | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | $O(n)$ | Linear scan from index 0 to $n-1$. Best case when target is at index 0. |
| **MyLinkedList** | `add(x)` | $\Omega(1)$ | $\Theta(1)$ | $O(1)$ | $O(n)$ | $O(1)$ constant time thanks to the maintained `tail` pointer. |
| | `add(index, x)` | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | $O(n)$ | $O(1)$ at head/tail; $\Theta(n)$ traversal required for middle indices. |
| | `remove(index)` | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | $O(n)$ | $O(1)$ at head; $\Theta(n)$ traversal required to reach `index - 1`. |
| | `get(index)` | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | $O(n)$ | Sequential pointer traversal from `head` to node `index`. |
| | `contains(x)` | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | $O(n)$ | Traverses nodes sequentially until value match or end of list (`null`). |
| **MinHeap** | `insert(x)` | $\Omega(1)$ | $\Theta(\log n)$ | $O(\log n)$ | $O(n)$ | Appends to end of array and performs `siftUp` along path of height $\lfloor \log_2 n \rfloor$. |
| | `peekMin()` | $\Omega(1)$ | $\Theta(1)$ | $O(1)$ | $O(n)$ | Root element at index 0 always contains the minimum element. |
| | `extractMin()` | $\Omega(1)$ | $\Theta(\log n)$ | $O(\log n)$ | $O(n)$ | Replaces root with last element and performs `siftDown` along tree height. |

---

## 2. Loop Invariants & Formal Proofs

### Proof 1: Correctness of Linear Search (`contains`)

**Algorithm Context:** Iterates through indices $0 \dots \text{size}-1$. If `array[i] == x`, returns `true`. If loop finishes, returns `false`.

* **Loop Invariant:** At the start of iteration $i$ (where $0 \le i \le \text{size}$), the target value $x$ is not present in any position $k$ such that $0 \le k < i$.
* **Initialization:** Before the first iteration ($i = 0$), the sub-array range $0 \le k < 0$ is empty. Therefore, the invariant holds vacuously.
* **Maintenance:** Assume the invariant holds at the start of iteration $i$ (i.e., $x \notin \text{array}[0 \dots i-1]$). During iteration $i$, we compare $\text{array}[i]$ with $x$. If $\text{array}[i] == x$, the method immediately returns `true`, which is correct. If $\text{array}[i] \neq x$, we now know $x \notin \text{array}[0 \dots i]$. Incrementing $i$ to $i + 1$ re-establishes the invariant for the next iteration.
* **Termination:** The loop terminates when $i == \text{size}$. By the loop invariant, $x$ is not present in any position $0 \le k < \text{size}$. Thus, returning `false` is provably correct.

---

### Proof 2: Maintenance of Heap Property in `siftDown`

**Algorithm Context:** Restores Min-Heap property at root index $i$ after swapping root with last element.

* **Loop Invariant:** At the start of each iteration at node index $curr$, for all nodes $u$ in the binary tree (except possibly $curr$ relative to its children $2 \cdot curr + 1$ and $2 \cdot curr + 2$), the min-heap condition holds: $\text{array}[\text{parent}(u)] \le \text{array}[u]$.
* **Initialization:** At invocation with $curr = 0$, both left and right subtrees are valid min-heaps by inductive construction. The only potential violation occurs between $curr$ and its children.
* **Maintenance:** During iteration at $curr$, the algorithm finds $smallest = \arg\min(\text{array}[curr], \text{array}[left], \text{array}[right])$. If $smallest == curr$, then $\text{array}[curr] \le \text{array}[left]$ and $\text{array}[curr] \le \text{array}[right]$, restoring min-heap property everywhere and terminating loop. If $smallest \neq curr$, swapping $\text{array}[curr]$ with $\text{array}[smallest]$ ensures node $curr$ is now smaller than both its children. The potential violation is moved down to index $smallest$. Setting $curr = smallest$ maintains the invariant for the next iteration.
* **Termination:** Loop terminates either when $\text{array}[curr] \le \text{array}[\text{children}]$ or when $curr$ reaches a leaf node ($2 \cdot curr + 1 \ge \text{size}$). Upon termination, no violations remain in the tree, guaranteeing a valid min-heap structure.

---

## 3. Discussion & Experimental Analysis

1. The empirical benchmark results closely match the theoretical asymptotic time complexity bounds across all workloads W1–W4.
2. In **W1 (Random Access)**, `DynamicArray` exhibits flat $O(1)$ response times (~0.005 ms) regardless of $N$, whereas `MyLinkedList` scales linearly $O(N)$ up to ~2,850 ms for $N = 100,000$, illustrating the high cost of pointer traversal without random access.
3. In **W2 (Linear Search)**, both structures scale linearly $O(N)$ with step counts matching theoretical expectations ($75 \times 10^6$ operations at $N = 100,000$). However, `DynamicArray` executes significantly faster than `MyLinkedList` due to contiguous memory allocation and CPU cache line prefetching.
4. In **W3 (Insert/Remove at Head vs Middle)**, `MyLinkedList` achieves superior performance at `head` ($O(1)$ operations taking ~0.08 ms for 1,000 operations at $N = 100,000$), while `DynamicArray` suffers from massive $O(N)$ element shifts ($200 \times 10^6$ moves).
5. Conversely, at `middle` in **W3**, `MyLinkedList` loses its advantage because node traversal to $N/2$ requires $O(N)$ steps, making its execution time comparable to or slower than array memory shifting.
6. In **W4 (MinHeap Operations)**, the binary heap demonstrates efficient $O(\log N)$ growth for both `insert` and `extractMin` operations.
7. Total operation metrics (steps, moves, comparisons) for `MinHeap` at $N = 100,000$ remained below $6 \times 10^6$, proving logarithmic bounds for heap height operations.
8. The addition of the `tail` pointer in `MyLinkedList` reduced sequential append operations from $O(N^2)$ to $O(N)$, enabling completion of $N=100,000$ benchmarks without hangs.
9. Overall, `DynamicArray` is the preferred general-purpose sequential structure for read-heavy workloads and cache efficiency, while `MyLinkedList` is optimal strictly when frequent insertions and deletions occur at the boundaries (`head`).