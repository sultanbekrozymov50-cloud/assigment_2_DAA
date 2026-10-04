package org.example;

import metrics.Metrics;
import structures.DynamicArray;
import structures.IntList;
import structures.MyLinkedList;
import structures.MinHeap;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Verification of corrected data structures ===");

        Metrics daMetrics = new Metrics();
        IntList da = new DynamicArray(5, daMetrics);
        da.add(10);
        da.add(20);
        da.add(30);
        System.out.println("DynamicArray get(1): " + da.get(1));
        System.out.println("DynamicArray contains(20): " + da.contains(20));
        System.out.println("Метрики DynamicArray: " + daMetrics);

        Metrics listMetrics = new Metrics();
        IntList list = new MyLinkedList(listMetrics);
        for (int i = 1; i <= 1000; i++) {
            list.add(i);
        }
        System.out.println("MyLinkedList size: " + list.getSize());
        System.out.println("MyLinkedList get(999): " + list.get(999));
        System.out.println("MyLinkedList contains(500): " + list.contains(500));
        System.out.println("Метрики MyLinkedList: " + listMetrics);

        Metrics heapMetrics = new Metrics();
        MinHeap heap = new MinHeap(10, heapMetrics);
        heap.insert(50);
        heap.insert(10);
        heap.insert(30);
        heap.insert(5);
        System.out.println("MinHeap peekMin: " + heap.peekMin());
        System.out.println("MinHeap extractMin: " + heap.extractMin());
        System.out.println("MinHeap peekMin (после extract): " + heap.peekMin());
        System.out.println("Метрики MinHeap: " + heapMetrics);

        System.out.println("\nAll basic checks completed successfully.!");
    }
}