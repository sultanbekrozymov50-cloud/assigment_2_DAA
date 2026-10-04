package benchmark;

import metrics.Metrics;
import structures.DynamicArray;
import structures.IntList;
import structures.MyLinkedList;
import structures.MinHeap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final String OUTPUT_DIR = "results";
    private static final String CSV_FILE = "results/results.csv";
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURE_RUNS = 5;

    private static class RunResult {
        double timeMs;
        long steps;
        long moves;
        long comparisons;

        RunResult(double timeMs, long steps, long moves, long comparisons) {
            this.timeMs = timeMs;
            this.steps = steps;
            this.moves = moves;
            this.comparisons = comparisons;
        }
    }

    public static void main(String[] args) {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Заголовок CSV с разделителем-запятой согласно ТЗ
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            System.out.println("Запуск нагрузочного бенчмарка...");

            for (int n : SIZES) {
                System.out.println("--- Запуск для N = " + n + " ---");
                runW1(writer, n);
                runW2(writer, n);
                runW3(writer, n);
                runW4(writer, n);
            }

            System.out.println("Бенчмарк успешно завершен! Результаты сохранены в " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка записи CSV: " + e.getMessage());
        }
    }

    // Выполнение нагрузки с прогревом и вычислением медианы из 5 замеров
    private static RunResult executeWithWarmup(Runnable benchmarkTask, Metrics metrics) {
        // 1. Фаза прогрева (Warmup)
        for (int i = 0; i < WARMUP_RUNS; i++) {
            metrics.reset();
            benchmarkTask.run();
        }

        // 2. Фаза измерений
        List<RunResult> results = new ArrayList<>();
        for (int i = 0; i < MEASURE_RUNS; i++) {
            metrics.reset();
            long start = System.nanoTime();
            benchmarkTask.run();
            long end = System.nanoTime();
            double timeMs = (end - start) / 1_000_000.0;
            results.add(new RunResult(timeMs, metrics.getSteps(), metrics.getMoves(), metrics.getComparisons()));
        }

        // 3. Сортировка и выбор медианы (индекс 2 из 5)
        results.sort((a, b) -> Double.compare(a.timeMs, b.timeMs));
        return results.get(MEASURE_RUNS / 2);
    }

    // --- W1: 10 000 вызовов get со случайным индексом ---
    private static void runW1(PrintWriter writer, int n) {
        String workload = "W1";
        String variant = "default";
        int getOps = 10_000;

        // DynamicArray
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(() -> {
                IntList list = createAndPopulateList(new DynamicArray(n, m), n);
                m.reset();
                Random rand = new Random(12345);
                for (int i = 0; i < getOps; i++) {
                    list.get(rand.nextInt(n));
                }
            }, m);
            writeCsvRow(writer, workload, variant, "DynamicArray", n, res);
        }

        // MyLinkedList
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(() -> {
                IntList list = createAndPopulateList(new MyLinkedList(m), n);
                m.reset();
                Random rand = new Random(12345);
                for (int i = 0; i < getOps; i++) {
                    list.get(rand.nextInt(n));
                }
            }, m);
            writeCsvRow(writer, workload, variant, "MyLinkedList", n, res);
        }
    }

    // --- W2: 1 000 вызовов contains (50% есть в структуре, 50% нет) ---
    private static void runW2(PrintWriter writer, int n) {
        String workload = "W2";
        String variant = "default";
        int searchOps = 1_000;

        // DynamicArray
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(() -> {
                int[] initialData = generateData(n, 42);
                IntList list = new DynamicArray(n, m);
                for (int val : initialData) list.add(val);

                int[] searchKeys = generateSearchKeys(initialData, searchOps);
                m.reset();

                for (int key : searchKeys) {
                    list.contains(key);
                }
            }, m);
            writeCsvRow(writer, workload, variant, "DynamicArray", n, res);
        }

        // MyLinkedList
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(() -> {
                int[] initialData = generateData(n, 42);
                IntList list = new MyLinkedList(m);
                for (int val : initialData) list.add(val);

                int[] searchKeys = generateSearchKeys(initialData, searchOps);
                m.reset();

                for (int key : searchKeys) {
                    list.contains(key);
                }
            }, m);
            writeCsvRow(writer, workload, variant, "MyLinkedList", n, res);
        }
    }

    // --- W3: 1 000 вставок и 1 000 удалений в индексе 0 (head) и n/2 (middle) ---
    private static void runW3(PrintWriter writer, int n) {
        String workload = "W3";
        int ops = 1_000;
        String[] variants = {"head", "middle"};

        for (String variant : variants) {
            // DynamicArray
            {
                Metrics m = new Metrics();
                RunResult res = executeWithWarmup(() -> {
                    IntList list = createAndPopulateList(new DynamicArray(n, m), n);
                    m.reset();

                    int insertVal = 999_999;
                    for (int i = 0; i < ops; i++) {
                        int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                        list.add(idx, insertVal);
                    }
                    for (int i = 0; i < ops; i++) {
                        int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                        list.remove(idx);
                    }
                }, m);
                writeCsvRow(writer, workload, variant, "DynamicArray", n, res);
            }

            // MyLinkedList
            {
                Metrics m = new Metrics();
                RunResult res = executeWithWarmup(() -> {
                    IntList list = createAndPopulateList(new MyLinkedList(m), n);
                    m.reset();

                    int insertVal = 999_999;
                    for (int i = 0; i < ops; i++) {
                        int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                        list.add(idx, insertVal);
                    }
                    for (int i = 0; i < ops; i++) {
                        int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                        list.remove(idx);
                    }
                }, m);
                writeCsvRow(writer, workload, variant, "MyLinkedList", n, res);
            }
        }
    }

    // --- W4: MinHeap — n вставок, n extractMin с проверкой неубывания ---
    private static void runW4(PrintWriter writer, int n) {
        String workload = "W4";
        String variant = "default";

        Metrics m = new Metrics();
        RunResult res = executeWithWarmup(() -> {
            int[] data = generateData(n, 42);
            MinHeap heap = new MinHeap(n, m);
            m.reset();

            // Вставка n элементов
            for (int val : data) {
                heap.insert(val);
            }

            // Извлечение n элементов и проверка сортировки
            int prev = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                int current = heap.extractMin();
                if (current < prev) {
                    throw new IllegalStateException("Нарушение неубывающего порядка кучи: " + current + " < " + prev);
                }
                prev = current;
            }
        }, m);

        writeCsvRow(writer, workload, variant, "MinHeap", n, res);
    }

    // --- Вспомогательные методы ---

    private static IntList createAndPopulateList(IntList list, int n) {
        int[] data = generateData(n, 42);
        for (int val : data) {
            list.add(val);
        }
        return list;
    }

    private static int[] generateData(int n, long seed) {
        Random rand = new Random(seed);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rand.nextInt(1_000_000);
        }
        return data;
    }

    private static int[] generateSearchKeys(int[] initialData, int count) {
        int half = count / 2;
        int[] keys = new int[count];
        Random rand = new Random(100);

        // 50% гарантированно присутствующих элементов
        for (int i = 0; i < half; i++) {
            keys[i] = initialData[rand.nextInt(initialData.length)];
        }

        // 50% гарантированно отсутствующих элементов (диапазон > 2_000_000)
        for (int i = half; i < count; i++) {
            keys[i] = 2_000_000 + rand.nextInt(1_000_000);
        }

        // Перемешивание ключей
        for (int i = keys.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = keys[i];
            keys[i] = keys[j];
            keys[j] = temp;
        }

        return keys;
    }

    private static void writeCsvRow(PrintWriter writer, String workload, String variant,
                                    String structure, int n, RunResult res) {
        writer.printf(Locale.US, "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload,
                variant,
                structure,
                n,
                res.timeMs,
                res.steps,
                res.moves,
                res.comparisons
        );
        writer.flush();
    }}