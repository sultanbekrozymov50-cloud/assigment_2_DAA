package metrics;

public class Metrics {
    private long steps;
    private long moves;
    private long comparisons;

    public Metrics() {
        reset();
    }

    public void reset() {
        this.steps = 0;
        this.moves = 0;
        this.comparisons = 0;
    }

    public void incSteps() { this.steps++; }
    public void incMoves() { this.moves++; }
    public void incComparisons() { this.comparisons++; }

    public void addSteps(long delta) { this.steps += delta; }
    public void addMoves(long delta) { this.moves += delta; }
    public void addComparisons(long delta) { this.comparisons += delta; }

    public long getSteps() { return steps; }
    public long getMoves() { return moves; }
    public long getComparisons() { return comparisons; }

    @Override
    public String toString() {
        return String.format("Metrics{steps=%d, moves=%d, comparisons=%d}", steps, moves, comparisons);
    }
}