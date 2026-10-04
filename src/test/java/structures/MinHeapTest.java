package structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {
    private MinHeap heap;

    @BeforeEach
    void setUp() {
        heap = new MinHeap();
    }

    @Test
    void testInsertAndPeek() {
        heap.insert(15);
        heap.insert(10);
        heap.insert(20);
        assertEquals(10, heap.peekMin());
    }

    @Test
    void testExtractMin() {
        heap.insert(30);
        heap.insert(10);
        heap.insert(20);
        heap.insert(5);

        assertEquals(5, heap.extractMin());
        assertEquals(10, heap.extractMin());
        assertEquals(20, heap.extractMin());
        assertEquals(30, heap.extractMin());
        assertEquals(0, heap.getSize());
    }

    @Test
    void testHeapPropertyAfterEveryOperation() {
        Random rand = new Random(42);
        for (int i = 0; i < 200; i++) {
            heap.insert(rand.nextInt(1_000_000));
            assertHeapInvariant();
        }

        for (int i = 0; i < 100; i++) {
            heap.extractMin();
            assertHeapInvariant();
        }
    }

    @Test
    void testExtractMinSortedOutput() {
        int n = 1_000;
        Random rand = new Random(42);
        for (int i = 0; i < n; i++) {
            heap.insert(rand.nextInt());
        }

        int prev = heap.extractMin();
        for (int i = 1; i < n; i++) {
            int curr = heap.extractMin();
            assertTrue(curr >= prev, "Инвариант порядка кучи нарушен: " + curr + " < " + prev);
            prev = curr;
        }
        assertEquals(0, heap.getSize());
    }

    @Test
    void testEdgeCasesAndDuplicates() {
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());

        heap.insert(7);
        heap.insert(7);
        heap.insert(7);
        assertEquals(7, heap.extractMin());
        assertEquals(7, heap.extractMin());
        assertEquals(7, heap.extractMin());
    }

    private void assertHeapInvariant() {
        int size = heap.getSize();
        for (int i = 0; i < size; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < size) {
                assertTrue(heap.get(i) <= heap.get(left),
                        "Инвариант кучи нарушен: parent (" + heap.get(i) + ") > left (" + heap.get(left) + ")");
            }
            if (right < size) {
                assertTrue(heap.get(i) <= heap.get(right),
                        "Инвариант кучи нарушен: parent (" + heap.get(i) + ") > right (" + heap.get(right) + ")");
            }
        }
    }
}