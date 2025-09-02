package io.streamx;

import java.util.List;

/**
 * Comprehensive statistics for numeric streams
 */
public class StreamStatistics {
    private final long count;
    private final double sum;
    private final double average;
    private final double min;
    private final double max;
    private final double standardDeviation;
    private final double variance;
    
    public StreamStatistics(List<Double> values) {
        this.count = values.size();
        
        if (values.isEmpty()) {
            this.sum = 0.0;
            this.average = 0.0;
            this.min = Double.NaN;
            this.max = Double.NaN;
            this.standardDeviation = Double.NaN;
            this.variance = Double.NaN;
        } else {
            this.sum = values.stream().mapToDouble(Double::doubleValue).sum();
            this.average = sum / count;
            this.min = values.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
            this.max = values.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
            
            // Calculate variance and standard deviation
            double varianceSum = values.stream()
                .mapToDouble(x -> Math.pow(x - average, 2))
                .sum();
            this.variance = varianceSum / count;
            this.standardDeviation = Math.sqrt(variance);
        }
    }
    
    public long getCount() { return count; }
    public double getSum() { return sum; }
    public double getAverage() { return average; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStandardDeviation() { return standardDeviation; }
    public double getVariance() { return variance; }
    
    @Override
    public String toString() {
        return String.format(
            "StreamStatistics{count=%d, sum=%.2f, avg=%.2f, min=%.2f, max=%.2f, stdDev=%.2f}",
            count, sum, average, min, max, standardDeviation
        );
    }
}
