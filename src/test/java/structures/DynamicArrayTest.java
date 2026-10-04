package structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;
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
        array.add(1, 20);
        assertEquals(3, array.getSize());
        assertEquals(10, array.get(0));
        assertEquals(20, array.get(1));
        assertEquals(30, array.get(2));
    }

    // Дифференциальное тестирование: сравнение с java.util.ArrayList на Random(42)
    @Test
    void testCompareWithJavaArrayList() {
        ArrayList<Integer> expected = new ArrayList<>();
        Random rand = new Random(42);

        for (int i = 0; i < 5_000; i++) {
            int op = rand.nextInt(3);
            int val = rand.nextInt(10_000);

            if (op == 0 || expected.isEmpty()) {
                array.add(val);
                expected.add(val);
            } else if (op == 1) {
                int idx = rand.nextInt(expected.size());
                array.add(idx, val);
                expected.add(idx, val);
            } else {
                int idx = rand.nextInt(expected.size());
                assertEquals((int) expected.remove(idx), array.remove(idx));
            }

            assertEquals(expected.size(), array.getSize());
            if (!expected.isEmpty()) {
                int checkIdx = rand.nextInt(expected.size());
                assertEquals((int) expected.get(checkIdx), array.get(checkIdx));
            }
        }
    }

    @Test
    void testOutOfBoundsAndEdgeCases() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        array.add(5);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
    }
}