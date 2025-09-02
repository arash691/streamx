package io.streamx;

/**
 * Represents a value paired with its index in a stream
 *
 * @param <T> the type of the value
 */
public record IndexedValue<T>(T value, int index) {

    @Override
    public String toString() {
        return String.format("IndexedValue{value=%s, index=%d}", value, index);
    }
}
