package structures;

import metrics.Metrics;

public class MinHeap {
    private int[] array;
    private int size;
    private int capacity;
    private final Metrics metrics;

    public MinHeap(int initialCapacity, Metrics metrics) {
        this.capacity = initialCapacity > 0 ? initialCapacity : 10;
        this.array = new int[this.capacity];
        this.size = 0;
        this.metrics = metrics != null ? metrics : new Metrics();
    }

    public MinHeap(Metrics metrics) {
        this(10, metrics);
    }

    public MinHeap(int initialCapacity) {
        this(initialCapacity, new Metrics());
    }

    public MinHeap() {
        this(10, new Metrics());
    }

    public void insert(int x) {
        if (size == capacity) {
            grow();
        }
        array[size] = x;
        metrics.incMoves();
        siftUp(size);
        size++;
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Куча пуста");
        }
        metrics.incSteps();
        return array[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Куча пуста");
        }
        metrics.incSteps();
        int min = array[0];
        array[0] = array[size - 1];
        metrics.incSteps();
        metrics.incMoves();
        size--;
        siftDown(0);
        return min;
    }

    public int getSize() {
        return size;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            metrics.incSteps();
            metrics.incSteps();
            metrics.incComparisons();
            if (array[index] >= array[parent]) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (2 * index + 1 < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = left;

            metrics.incSteps();
            if (right < size) {
                metrics.incSteps();
                metrics.incComparisons();
                if (array[right] < array[left]) {
                    smallest = right;
                }
            }

            metrics.incSteps();
            metrics.incSteps();
            metrics.incComparisons();
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
        metrics.addMoves(3);
    }

    private void grow() {
        capacity *= 2;
        int[] newArray = new int[capacity];
        for (int i = 0; i < size; i++) {
            metrics.incSteps();
            newArray[i] = array[i];
            metrics.incMoves();
        }
        array = newArray;
    }
}