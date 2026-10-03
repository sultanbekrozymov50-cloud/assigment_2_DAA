package structures;

import metrics.Metrics;

public class MyLinkedList implements IntList {
    private static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node head;
    private int size;
    private final Metrics metrics;

    public MyLinkedList(Metrics metrics) {
        this.metrics = metrics != null ? metrics : new Metrics();
    }

    @Override
    public void add(int x) {
        metrics.incSteps();
        Node newNode = new Node(x);
        metrics.incMoves();
        if (head == null) {
            head = newNode;
            metrics.incMoves();
        } else {
            Node current = head;
            while (current.next != null) {
                metrics.incSteps();
                current = current.next;
                metrics.incMoves();
            }
            current.next = newNode;
            metrics.incMoves();
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Индекс за пределами: " + index);
        }
        metrics.incSteps();
        Node newNode = new Node(x);
        metrics.incMoves();

        if (index == 0) {
            newNode.next = head;
            head = newNode;
            metrics.addMoves(2);
        } else {
            Node prev = getNode(index - 1);
            newNode.next = prev.next;
            prev.next = newNode;
            metrics.addMoves(2);
        }
        size++;
    }

    @Override
    public int remove(int index) {
        checkBounds(index);
        metrics.incSteps();
        int removedValue;

        if (index == 0) {
            removedValue = head.value;
            head = head.next;
            metrics.incMoves();
        } else {
            Node prev = getNode(index - 1);
            removedValue = prev.next.value;
            prev.next = prev.next.next;
            metrics.incMoves();
        }

        size--;
        return removedValue;
    }

    @Override
    public int get(int index) {
        checkBounds(index);
        metrics.incSteps();
        return getNode(index).value;
    }

    @Override
    public boolean contains(int x) {
        metrics.incSteps();
        Node current = head;
        while (current != null) {
            metrics.incSteps();
            metrics.incComparisons();
            if (current.value == x) {
                return true;
            }
            current = current.next;
            metrics.incMoves();
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
            metrics.incMoves();
        }
        return current;
    }

    private void checkBounds(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс за пределами: " + index);
        }
    }
}