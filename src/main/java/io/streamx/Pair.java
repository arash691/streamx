package io.streamx;

import java.util.Objects;

/**
 * A simple pair of two values
 * 
 * @param <T> type of first value
 * @param <U> type of second value  
 */
public record Pair<T, U>(T first, U second) {
    
    public Pair {
        Objects.requireNonNull(first, "first cannot be null");
        Objects.requireNonNull(second, "second cannot be null");
    }
    
    /**
     * Creates a new pair
     */
    public static <T, U> Pair<T, U> of(T first, U second) {
        return new Pair<>(first, second);
    }
}
