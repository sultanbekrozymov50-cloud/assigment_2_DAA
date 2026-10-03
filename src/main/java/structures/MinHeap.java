package structures;

public class MinHeap {
    private int[] array;
    private int size;
    private int capacity;

    public MinHeap(int initialCapacity) {
        this.capacity = initialCapacity > 0 ? initialCapacity : 10;
        this.array = new int[this.capacity];
        this.size = 0;
    }

    public MinHeap() {
        this(10);
    }

    // Вставка элемента в кучу — O(log n)
    public void insert(int x) {
        if (size == capacity) {
            grow();
        }
        array[size] = x;
        siftUp(size);
        size++;
    }

    // Просмотр минимального элемента — O(1)
    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Куча пуста");
        }
        return array[0];
    }

    // Извлечение минимального элемента — O(log n)
    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Куча пуста");
        }
        int min = array[0];
        array[0] = array[size - 1];
        size--;
        siftDown(0);
        return min;
    }

    public int getSize() {
        return size;
    }

    // Подъем элемента вверх для восстановления свойств кучи
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (array[index] >= array[parent]) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    // Опускание элемента вниз для восстановления свойств кучи
    private void siftDown(int index) {
        while (2 * index + 1 < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = left;

            if (right < size && array[right] < array[left]) {
                smallest = right;
            }

            if (array[index] <= array[smallest]) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private void grow() {
        capacity *= 2;
        int[] newArray = new int[capacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = array[i];
        }
        array = newArray;
    }
}
