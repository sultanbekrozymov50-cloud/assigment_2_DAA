package benchmark;

import metrics.Metrics;
import structures.DynamicArray;
import structures.IntList;
import structures.MyLinkedList;
import structures.MinHeap;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.function.Consumer;

public class Benchmark {
    private static final String CSV_FILE = "results.csv";
    private static final int N = 10_000;
    private static final Random random = new Random(42);

    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // CSV Header
            writer.println("Workload;DataStructure;Elements;TimeMs;Steps;Moves;Comparisons");

            runW1_MassiveInsert(writer);
            runW2_ContainsSearch(writer);
            runW3_AccessAndPeek(writer);
            runW4_MassiveDelete(writer);

            System.out.println("Бенчмарк успешно завершен. Результаты сохранены в " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в CSV: " + e.getMessage());
        }
    }

    // W1: Массовая вставка N элементов
    private static void runW1_MassiveInsert(PrintWriter writer) {
        String workload = "W1_MassiveInsert";
        int[] data = generateRandomData(N);

        // 1. DynamicArray (через IntList)
        Metrics m1 = new Metrics();
        IntList da = new DynamicArray(10, m1);
        runListWorkload(writer, workload, "DynamicArray", da, m1, list -> {
            for (int x : data) list.add(x);
        }, N);

        // 2. MyLinkedList (через IntList)
        Metrics m2 = new Metrics();
        IntList list = new MyLinkedList(m2);
        runListWorkload(writer, workload, "MyLinkedList", list, m2, l -> {
            for (int x : data) l.add(x);
        }, N);

        // 3. MinHeap
        Metrics m3 = new Metrics();
        MinHeap heap = new MinHeap(10, m3);
        runCustomWorkload(writer, workload, "MinHeap", m3, () -> {
            for (int x : data) heap.insert(x);
        }, N);
    }

    // W2: Поиск методом contains() для 1,000 элементов
    private static void runW2_ContainsSearch(PrintWriter writer) {
        String workload = "W2_ContainsSearch";
        int searchOps = 1_000;
        int[] data = generateRandomData(N);
        int[] searchKeys = generateRandomData(searchOps);

        // DynamicArray (через IntList)
        Metrics m1 = new Metrics();
        IntList da = new DynamicArray(N, m1);
        for (int x : data) da.add(x);
        runListWorkload(writer, workload, "DynamicArray", da, m1, list -> {
            for (int key : searchKeys) list.contains(key);
        }, searchOps);

        // MyLinkedList (через IntList)
        Metrics m2 = new Metrics();
        IntList list = new MyLinkedList(m2);
        for (int x : data) list.add(x);
        runListWorkload(writer, workload, "MyLinkedList", list, m2, l -> {
            for (int key : searchKeys) l.contains(key);
        }, searchOps);
    }

    // W3: Доступ по индексу (get) / чтение минимума (peekMin)
    private static void runW3_AccessAndPeek(PrintWriter writer) {
        String workload = "W3_AccessAndPeek";
        int accessOps = 5_000;
        int[] data = generateRandomData(N);

        // DynamicArray (через IntList)
        Metrics m1 = new Metrics();
        IntList da = new DynamicArray(N, m1);
        for (int x : data) da.add(x);
        runListWorkload(writer, workload, "DynamicArray", da, m1, list -> {
            for (int i = 0; i < accessOps; i++) list.get(i % N);
        }, accessOps);

        // MyLinkedList (через IntList)
        Metrics m2 = new Metrics();
        IntList list = new MyLinkedList(m2);
        for (int x : data) list.add(x);
        runListWorkload(writer, workload, "MyLinkedList", list, m2, l -> {
            for (int i = 0; i < accessOps; i++) l.get(i % N);
        }, accessOps);

        // MinHeap (peekMin)
        Metrics m3 = new Metrics();
        MinHeap heap = new MinHeap(N, m3);
        for (int x : data) heap.insert(x);
        runCustomWorkload(writer, workload, "MinHeap", m3, () -> {
            for (int i = 0; i < accessOps; i++) heap.peekMin();
        }, accessOps);
    }

    // W4: Удаление элементов с начала (remove(0) / extractMin)
    private static void runW4_MassiveDelete(PrintWriter writer) {
        String workload = "W4_MassiveDelete";
        int deleteOps = 1_000;
        int[] data = generateRandomData(N);

        // DynamicArray (через IntList)
        Metrics m1 = new Metrics();
        IntList da = new DynamicArray(N, m1);
        for (int x : data) da.add(x);
        runListWorkload(writer, workload, "DynamicArray", da, m1, list -> {
            for (int i = 0; i < deleteOps; i++) list.remove(0);
        }, deleteOps);

        // MyLinkedList (через IntList)
        Metrics m2 = new Metrics();
        IntList list = new MyLinkedList(m2);
        for (int x : data) list.add(x);
        runListWorkload(writer, workload, "MyLinkedList", list, m2, l -> {
            for (int i = 0; i < deleteOps; i++) l.remove(0);
        }, deleteOps);

        // MinHeap (extractMin)
        Metrics m3 = new Metrics();
        MinHeap heap = new MinHeap(N, m3);
        for (int x : data) heap.insert(x);
        runCustomWorkload(writer, workload, "MinHeap", m3, () -> {
            for (int i = 0; i < deleteOps; i++) heap.extractMin();
        }, deleteOps);
    }

    // Универсальный полиморфный запуск для реализаций IntList
    private static void runListWorkload(
            PrintWriter writer,
            String workloadName,
            String dsName,
            IntList list,
            Metrics metrics,
            Consumer<IntList> action,
            int opsCount
    ) {
        metrics.reset(); // Сброс счетчиков перед замерной фазой
        long start = System.nanoTime();

        action.accept(list); // Выполнение операций через интерфейс IntList

        long timeMs = (System.nanoTime() - start) / 1_000_000;
        writeRow(writer, workloadName, dsName, opsCount, timeMs, metrics);
    }

    // Запуск для кучи и действий, не привязанных к IntList
    private static void runCustomWorkload(
            PrintWriter writer,
            String workloadName,
            String dsName,
            Metrics metrics,
            Runnable action,
            int opsCount
    ) {
        metrics.reset();
        long start = System.nanoTime();

        action.run();

        long timeMs = (System.nanoTime() - start) / 1_000_000;
        writeRow(writer, workloadName, dsName, opsCount, timeMs, metrics);
    }

    private static void writeRow(PrintWriter writer, String workload, String dsName,
                                 int ops, long timeMs, Metrics metrics) {
        writer.printf("%s;%s;%d;%d;%d;%d;%d%n",
                workload,
                dsName,
                ops,
                timeMs,
                metrics.getSteps(),
                metrics.getMoves(),
                metrics.getComparisons()
        );
    }

    private static int[] generateRandomData(int count) {
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) {
            arr[i] = random.nextInt(100_000);
        }
        return arr;
    }
}