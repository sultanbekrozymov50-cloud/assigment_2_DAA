package structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {
    private DynamicArray array;

    @BeforeEach
    void setUp() {
        array = new DynamicArray();
    }

    @Test
    void testAddAndGet() {
        array.add(10);
        array.add(20);
        assertEquals(2, array.getSize());
        assertEquals(10, array.get(0));
        assertEquals(20, array.get(1));
    }

    @Test
    void testAddAtIndex() {
        array.add(10);
        array.add(30);
        array.add(1, 20); // Insert 20 at index 1
        assertEquals(3, array.getSize());
        assertEquals(10, array.get(0));
        assertEquals(20, array.get(1));
        assertEquals(30, array.get(2));
    }

    @Test
    void testAutoResizing() {
        for (int i = 0; i < 100; i++) {
            array.add(i);
        }
        assertEquals(100, array.getSize());
        assertEquals(99, array.get(99));
    }

    @Test
    void testRemove() {
        array.add(10);
        array.add(20);
        array.add(30);
        assertEquals(20, array.remove(1));
        assertEquals(2, array.getSize());
        assertEquals(30, array.get(1));
    }

    @Test
    void testContains() {
        array.add(5);
        array.add(15);
        assertTrue(array.contains(15));
        assertFalse(array.contains(99));
    }

    @Test
    void testOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        array.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(5));
    }
}