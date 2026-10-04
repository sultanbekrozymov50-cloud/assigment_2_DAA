package structures;

import metrics.Metrics;

public class DynamicArray implements IntList {
    private int[] array;
    private int size;
    private int capacity;
    private final Metrics metrics;

    public DynamicArray(int initialCapacity, Metrics metrics) {
        this.capacity = initialCapacity > 0 ? initialCapacity : 10;
        this.array = new int[this.capacity];
        this.size = 0;
        this.metrics = metrics != null ? metrics : new Metrics();
    }

    public DynamicArray(int initialCapacity) {
        this(initialCapacity, new Metrics());
    }

    public DynamicArray() {
        this(10, new Metrics());
    }

    @Override
    public void add(int x) {
        if (size == capacity) {
            grow();
        }
        array[size++] = x;
        metrics.incMoves();
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        if (size == capacity) {
            grow();
        }
        for (int i = size; i > index; i--) {
            metrics.incSteps();
            array[i] = array[i - 1];
            metrics.incMoves();
        }
        array[index] = x;
        metrics.incMoves();
        size++;
    }

    @Override
    public int remove(int index) {
        checkBounds(index);
        metrics.incSteps();
        int removed = array[index];
        for (int i = index; i < size - 1; i++) {
            metrics.incSteps();
            array[i] = array[i + 1];
            metrics.incMoves();
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        checkBounds(index);
        metrics.incSteps();
        return array[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.incSteps();
            metrics.incComparisons();
            if (array[i] == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
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

    private void checkBounds(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }
}