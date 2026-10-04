# DAA Assignment 2 — In-Memory Workload Engine

Transparent from-scratch implementations of DynamicArray, MyLinkedList and MinHeap
(no java.util collections), with exact operation counters (steps / moves / comparisons)
and a benchmark over 4 workloads.

## Author
- Name: <Sultanbek>
- Group: <se-2521>
- Assignment: Design and Analysis of Algorithms — Assignment 2



## Project Layout
src/main/java/ structures, metrics, benchmark
src/test/java/ JUnit 5 tests
results/results.csv
results/plots/
plot_results.py
README.md
REPORT.md

text

## Build
```bash
mvn clean compile
Run Tests

bash
mvn test
Run Benchmark

Run from the project root so that results/results.csv is written to the right place:

bash
mvn clean compile
java -cp target/classes benchmark.Benchmark
Produces results/results.csv with columns:
workload, variant, structure, n, time_ms, steps, moves, comparisons.

Generate Plots

bash
python plot_results.py
Reads results/results.csv and writes PNG charts to results/plots/.
Requires pandas and matplotlib.

Data Structures

Structure	Storage	Methods
DynamicArray	int[], 2x growth	add(x), add(i,x), remove(i), get(i), contains(x)
MyLinkedList	linked nodes of int	add(x), add(i,x), remove(i), get(i), contains(x)
MinHeap	int[] binary heap	insert(x), peekMin(), extractMin()
Invalid input: get, add(i,x), remove(i) throw IndexOutOfBoundsException;
peekMin and extractMin on an empty heap throw IllegalStateException.

Workloads

W1 — Random Access: 10 000 get(random) calls.
W2 — Search: 1 000 contains(x) queries (half present, half absent).
W3 — Insert & Remove: 1 000 insertions + 1 000 removals at head and middle.
W4 — Priority Processing: n inserts + n extractMin, output must be non-decreasing.
Sizes: n = 100, 1 000, 10 000, 100 000.

Randomness (so results are fully reproducible):

structure fill values — new Random(42)
W1 random indices — new Random(12345)
W2 search keys — new Random(100)
Timing: median of 5 runs after 2 warm-up runs.

Metrics

steps — one array cell read or one node move.
moves — one element shift (array) or one link update (list).
comparisons — one comparison of two elements.
Counted inside the methods, not estimated.

Results

See results/results.csv and charts in results/plots/.
Complexity table, loop-invariant proofs and cache / pointer-chasing discussion: REPORT.md.

Renaming note

If the files were committed as README.MD / REPORT.MD, rename in two steps so Git
notices the case change:

bash
git mv README.MD tmp && git mv tmp README.md
git mv REPORT.MD tmp && git mv tmp REPORT.md