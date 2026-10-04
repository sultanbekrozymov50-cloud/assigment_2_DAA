package structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {
    private MyLinkedList list;

    @BeforeEach
    void setUp() {
        list = new MyLinkedList();
    }

    @Test
    void testAddAndGet() {
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(3, list.getSize());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
    }

    @Test
    void testAddAtIndex() {
        list.add(10);
        list.add(30);
        list.add(1, 20);

        assertEquals(3, list.getSize());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
    }

    @Test
    void testRemove() {
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(10, list.remove(0)); // Remove head
        assertEquals(2, list.getSize());
        assertEquals(20, list.get(0));

        assertEquals(30, list.remove(1)); // Remove tail
        assertEquals(1, list.getSize());
        assertEquals(20, list.get(0));
    }

    @Test
    void testContains() {
        list.add(100);
        list.add(200);
        assertTrue(list.contains(200));
        assertFalse(list.contains(300));
    }

    @Test
    void testOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        list.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(5));
    }
}