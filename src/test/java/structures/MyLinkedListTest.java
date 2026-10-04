package structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;
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
    void testRemoveHeadAndTail() {
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(10, list.remove(0)); // Удаление head
        assertEquals(2, list.getSize());

        assertEquals(30, list.remove(1)); // Удаление tail
        assertEquals(1, list.getSize());
        assertEquals(20, list.get(0));
    }

    // Дифференциальное тестирование: сравнение с java.util.ArrayList на Random(42)
    @Test
    void testCompareWithJavaList() {
        ArrayList<Integer> expected = new ArrayList<>();
        Random rand = new Random(42);

        for (int i = 0; i < 2_000; i++) {
            int op = rand.nextInt(3);
            int val = rand.nextInt(10_000);

            if (op == 0 || expected.isEmpty()) {
                list.add(val);
                expected.add(val);
            } else if (op == 1) {
                int idx = rand.nextInt(expected.size());
                list.add(idx, val);
                expected.add(idx, val);
            } else {
                int idx = rand.nextInt(expected.size());
                assertEquals((int) expected.remove(idx), list.remove(idx));
            }

            assertEquals(expected.size(), list.getSize());
        }
    }

    @Test
    void testOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        list.add(100);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }
}