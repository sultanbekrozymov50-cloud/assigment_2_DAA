package structures;

import metrics.Metrics;

public class MyLinkedList implements IntList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics;

    public MyLinkedList(Metrics metrics) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.metrics = metrics != null ? metrics : new Metrics();
    }

    public MyLinkedList() {
        this(new Metrics());
    }

    @Override
    public void add(int x) {
        Node newNode = new Node(x);
        if (head == null) {
            head = newNode;
            tail = newNode;
            metrics.incMoves();
        } else {
            tail.next = newNode;
            tail = newNode;
            metrics.incMoves();
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Индекс за пределами: " + index);
        }

        if (index == size) {
            add(x);
            return;
        }

        Node newNode = new Node(x);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
            metrics.incMoves();
            if (size == 0) {
                tail = newNode;
            }
        } else {
            Node prev = getNode(index - 1);
            newNode.next = prev.next;
            prev.next = newNode;
            metrics.incMoves();
        }
        size++;
    }

    @Override
    public int remove(int index) {
        checkBounds(index);
        int removedValue;

        if (index == 0) {
            metrics.incSteps();
            removedValue = head.value;
            head = head.next;
            metrics.incMoves();
            if (head == null) {
                tail = null;
            }
        } else {
            Node prev = getNode(index - 1);
            metrics.incSteps();
            removedValue = prev.next.value;
            prev.next = prev.next.next;
            metrics.incMoves();
            if (index == size - 1) {
                tail = prev;
            }
        }

        size--;
        return removedValue;
    }

    @Override
    public int get(int index) {
        checkBounds(index);
        Node node = getNode(index);
        metrics.incSteps();
        return node.value;
    }

    @Override
    public boolean contains(int x) {
        Node current = head;
        while (current != null) {
            metrics.incSteps();
            metrics.incComparisons();
            if (current.value == x) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public int getSize() {
        return size;
    }

    private Node getNode(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            metrics.incSteps();
            current = current.next;
        }
        return current;
    }

    private void checkBounds(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс за пределами: " + index);
        }
    }
}