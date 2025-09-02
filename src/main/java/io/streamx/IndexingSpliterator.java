package io.streamx;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A Spliterator that adds indexing to stream elements
 * 
 * @param <T> the type of elements
 */
class IndexingSpliterator<T> implements Spliterator<IndexedValue<T>> {
    private final Spliterator<T> source;
    private int index = 0;
    
    public IndexingSpliterator(Spliterator<T> source) {
        this.source = source;
    }
    
    @Override
    public boolean tryAdvance(Consumer<? super IndexedValue<T>> action) {
        return source.tryAdvance(value -> action.accept(new IndexedValue<>(value, index++)));
    }
    
    @Override
    public Spliterator<IndexedValue<T>> trySplit() {
        // Don't split to maintain index order
        return null;
    }
    
    @Override
    public long estimateSize() {
        return source.estimateSize();
    }
    
    @Override
    public int characteristics() {
        return source.characteristics() & ~SUBSIZED; // Remove SUBSIZED since we can't split
    }
}
