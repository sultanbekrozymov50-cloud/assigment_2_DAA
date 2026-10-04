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
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Benchmark {
    private static final String OUTPUT_DIR = "results";
    private static final String CSV_FILE = "results/results.csv";
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURE_RUNS = 5;

    // Контейнер для W2: передает подготовленный список и ключи в замеряемую фазу
    private static class W2Data {
        final IntList list;
        final int[] searchKeys;

        W2Data(IntList list, int[] searchKeys) {
            this.list = list;
            this.searchKeys = searchKeys;
        }
    }

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
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                runW1(writer, n);
                runW2(writer, n);
                runW3(writer, n);
                runW4(writer, n);
            }

            System.out.println("Бенчмарк успешно завершен! Результаты сохранены в " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка записи в CSV: " + e.getMessage());
        }
    }

    // Замеряет ТОЛЬКО выполнение action. Вызов setup выполняется ДО включения секундомера!
    private static <T> RunResult executeWithWarmup(Supplier<T> setup, Consumer<T> action, Metrics metrics) {
        // Прогрев (Warmup)
        for (int i = 0; i < WARMUP_RUNS; i++) {
            T target = setup.get();
            metrics.reset();
            action.accept(target);
        }

        // Измерения (Measurement)
        List<RunResult> results = new ArrayList<>();
        for (int i = 0; i < MEASURE_RUNS; i++) {
            T target = setup.get(); // Подготовка данных происходит ДО секундомера
            metrics.reset();        // Сброс счетчиков операций

            long start = System.nanoTime();
            action.accept(target);  // Измеряем ТОЛЬКО целевую нагрузку
            long end = System.nanoTime();

            double timeMs = (end - start) / 1_000_000.0;
            results.add(new RunResult(timeMs, metrics.getSteps(), metrics.getMoves(), metrics.getComparisons()));
        }

        // Выбор медианного значения из 5 прогонов
        results.sort((a, b) -> Double.compare(a.timeMs, b.timeMs));
        return results.get(MEASURE_RUNS / 2);
    }

    // --- W1: 10 000 вызовов get со случайным индексом ---
    private static void runW1(PrintWriter writer, int n) {
        String workload = "W1";
        String variant = "-";
        int getOps = 10_000;

        // DynamicArray
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(
                    () -> populateList(new DynamicArray(n, m), n),
                    list -> {
                        Random rand = new Random(12345);
                        for (int i = 0; i < getOps; i++) {
                            list.get(rand.nextInt(n));
                        }
                    },
                    m
            );
            writeCsvRow(writer, workload, variant, "DynamicArray", n, res);
        }

        // MyLinkedList
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(
                    () -> populateList(new MyLinkedList(m), n),
                    list -> {
                        Random rand = new Random(12345);
                        for (int i = 0; i < getOps; i++) {
                            list.get(rand.nextInt(n));
                        }
                    },
                    m
            );
            writeCsvRow(writer, workload, variant, "MyLinkedList", n, res);
        }
    }

    // --- W2: 1 000 вызовов contains (ключи генерируются в setup ДО замера) ---
    private static void runW2(PrintWriter writer, int n) {
        String workload = "W2";
        String variant = "-";
        int searchOps = 1_000;

        // DynamicArray
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(
                    () -> {
                        int[] initialData = generateData(n, 42);
                        IntList list = new DynamicArray(n, m);
                        for (int val : initialData) list.add(val);
                        int[] searchKeys = generateSearchKeys(initialData, searchOps);
                        return new W2Data(list, searchKeys); // Список и ключи готовы до секундомера
                    },
                    data -> {
                        for (int key : data.searchKeys) {
                            data.list.contains(key);
                        }
                    },
                    m
            );
            writeCsvRow(writer, workload, variant, "DynamicArray", n, res);
        }

        // MyLinkedList
        {
            Metrics m = new Metrics();
            RunResult res = executeWithWarmup(
                    () -> {
                        int[] initialData = generateData(n, 42);
                        IntList list = new MyLinkedList(m);
                        for (int val : initialData) list.add(val);
                        int[] searchKeys = generateSearchKeys(initialData, searchOps);
                        return new W2Data(list, searchKeys);
                    },
                    data -> {
                        for (int key : data.searchKeys) {
                            data.list.contains(key);
                        }
                    },
                    m
            );
            writeCsvRow(writer, workload, variant, "MyLinkedList", n, res);
        }
    }

    // --- W3: 1 000 вставок и 1 000 удалений в head и middle ---
    private static void runW3(PrintWriter writer, int n) {
        String workload = "W3";
        int ops = 1_000;

        for (String variant : new String[]{"head", "middle"}) {
            // DynamicArray
            {
                Metrics m = new Metrics();
                RunResult res = executeWithWarmup(
                        () -> populateList(new DynamicArray(n, m), n),
                        list -> {
                            for (int i = 0; i < ops; i++) {
                                int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                                list.add(idx, 999_999);
                            }
                            for (int i = 0; i < ops; i++) {
                                int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                                list.remove(idx);
                            }
                        },
                        m
                );
                writeCsvRow(writer, workload, variant, "DynamicArray", n, res);
            }

            // MyLinkedList
            {
                Metrics m = new Metrics();
                RunResult res = executeWithWarmup(
                        () -> populateList(new MyLinkedList(m), n),
                        list -> {
                            for (int i = 0; i < ops; i++) {
                                int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                                list.add(idx, 999_999);
                            }
                            for (int i = 0; i < ops; i++) {
                                int idx = variant.equals("head") ? 0 : list.getSize() / 2;
                                list.remove(idx);
                            }
                        },
                        m
                );
                writeCsvRow(writer, workload, variant, "MyLinkedList", n, res);
            }
        }
    }

    // --- W4: MinHeap — n вставок и n extractMin ---
    private static void runW4(PrintWriter writer, int n) {
        String workload = "W4";
        String variant = "-";

        Metrics m = new Metrics();
        int[] data = generateData(n, 42);

        RunResult res = executeWithWarmup(
                () -> new MinHeap(n, m),
                heap -> {
                    for (int val : data) {
                        heap.insert(val);
                    }
                    int prev = Integer.MIN_VALUE;
                    for (int i = 0; i < n; i++) {
                        int curr = heap.extractMin();
                        if (curr < prev) {
                            throw new IllegalStateException("MinHeap error: current (" + curr + ") < prev (" + prev + ")");
                        }
                        prev = curr;
                    }
                },
                m
        );

        writeCsvRow(writer, workload, variant, "MinHeap", n, res);
    }

    private static IntList populateList(IntList list, int n) {
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
        int n = initialData.length;
        int[] keys = new int[count];
        Random rand = new Random(100);

        for (int i = 0; i < count / 2; i++) {
            keys[i] = initialData[rand.nextInt(n)];
        }
        for (int i = count / 2; i < count; i++) {
            keys[i] = 2_000_000 + rand.nextInt(1_000_000);
        }
        return keys;
    }

    private static void writeCsvRow(PrintWriter writer, String workload, String variant,
                                    String structure, int n, RunResult res) {
        writer.printf(Locale.US, "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload, variant, structure, n, res.timeMs, res.steps, res.moves, res.comparisons);
        writer.flush();
    }
}