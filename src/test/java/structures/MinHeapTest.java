package structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testEmptyHeapException() {
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());
    }
}