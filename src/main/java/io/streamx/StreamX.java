package io.streamx;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * StreamX - Extended Stream Utilities for Java
 * 
 * <p>A comprehensive utility library that extends Java Streams with commonly needed operations
 * that are missing from the standard library. StreamX brings functional programming features
 * from languages like Kotlin, Scala, Haskell, F#, and Clojure to Java.</p>
 * 
 * <p>This library provides:</p>
 * <ul>
 *   <li><strong>Indexed Operations:</strong> Access element indices without external counters</li>
 *   <li><strong>Scan Operations:</strong> Running folds with all intermediate results</li>
 *   <li><strong>Advanced Partitioning:</strong> Split streams in sophisticated ways</li>
 *   <li><strong>Sequence Generation:</strong> Create infinite streams and cycles</li>
 *   <li><strong>Enhanced Selectors:</strong> Find elements by custom criteria</li>
 *   <li><strong>Advanced Zipping:</strong> Combine multiple streams with different strategies</li>
 *   <li><strong>Windowing Operations:</strong> Process data in sliding windows or chunks</li>
 *   <li><strong>Enhanced Collectors:</strong> More powerful ways to collect results</li>
 *   <li><strong>Safe Operations:</strong> Graceful error handling within streams</li>
 *   <li><strong>Mathematical Operations:</strong> Built-in statistics and aggregations</li>
 *   <li><strong>String Operations:</strong> Rich formatting and joining capabilities</li>
 *   <li><strong>Debugging Tools:</strong> Better observability into stream processing</li>
 * </ul>
 * 
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Indexed operations
 * List<String> indexed = StreamX.withIndex(words.stream(), 
 *     (word, index) -> index + ": " + word).toList();
 * 
 * // Scan operations (running totals)
 * List<Integer> runningSum = StreamX.scan(numbers.stream(), 0, Integer::sum).toList();
 * 
 * // Advanced partitioning
 * Pair<List<Integer>, List<Integer>> evenOdd = StreamX.partition(numbers.stream(), n -> n % 2 == 0);
 * 
 * // Enhanced selectors
 * Optional<Person> oldest = StreamX.maxBy(people.stream(), Person::getAge);
 * }</pre>
 * 
 * <p>All operations maintain the lazy evaluation characteristics of Java Streams and are designed
 * to work seamlessly with existing Stream operations.</p>
 * 
 * @author StreamX Contributors
 * @version 1.0
 * @since 1.0
 * @see Stream
 * @see Collectors
 */
public class StreamX {
    
    // ========== Factory Methods ==========
    
    /**
     * Creates a stream from an existing Stream.
     * 
     * <p>This is a convenience method that provides a consistent entry point
     * for StreamX operations while maintaining the original stream's characteristics.</p>
     * 
     * <h3>Example:</h3>
     * <pre>{@code
     * Stream<String> words = Stream.of("hello", "world");
     * Stream<String> streamX = StreamX.of(words);
     * }</pre>
     * 
     * @param <T>    the type of stream elements
     * @param stream the input stream to wrap
     * @return the same stream instance for method chaining with StreamX operations
     * @throws NullPointerException if stream is null
     * @since 1.0
     * @see #of(Collection)
     * @see #of(Object[])
     */
    public static <T> Stream<T> of(Stream<T> stream) {
        return stream;
    }
    
    /**
     * Creates a stream from a Collection.
     * 
     * <p>This method provides a consistent way to create streams from collections
     * while maintaining access to StreamX operations.</p>
     * 
     * <h3>Example:</h3>
     * <pre>{@code
     * List<Integer> numbers = List.of(1, 2, 3, 4, 5);
     * Stream<Integer> stream = StreamX.of(numbers);
     * }</pre>
     * 
     * @param <T>        the type of collection elements
     * @param collection the collection to create a stream from
     * @return a sequential stream over the elements in the collection
     * @throws NullPointerException if collection is null
     * @since 1.0
     * @see Collection#stream()
     * @see #of(Stream)
     */
    public static <T> Stream<T> of(Collection<T> collection) {
        return collection.stream();
    }
    
    /**
     * Creates a stream from varargs elements.
     * 
     * <p>This method provides a convenient way to create streams from individual
     * elements while maintaining access to StreamX operations.</p>
     * 
     * <h3>Example:</h3>
     * <pre>{@code
     * Stream<String> colors = StreamX.of("red", "green", "blue");
     * Stream<Integer> numbers = StreamX.of(1, 2, 3, 4, 5);
     * }</pre>
     * 
     * @param <T>      the type of stream elements
     * @param elements the elements to include in the stream
     * @return a sequential ordered stream whose elements are the specified values
     * @since 1.0
     * @see Stream#of(Object[])
     */
    @SafeVarargs
    public static <T> Stream<T> of(T... elements) {
        return Stream.of(elements);
    }
    
    // ========== Indexed Operations ==========
    
    /**
     * Associates each element with its index in the stream.
     * 
     * <p>This operation pairs each stream element with its zero-based position index,
     * returning a stream of {@link IndexedValue} objects. This is particularly useful
     * when you need to know the position of elements during stream processing.</p>
     * 
     * <p><strong>Inspired by:</strong> Kotlin's {@code withIndex()}, Scala's {@code zipWithIndex}</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Basic usage
     * List<IndexedValue<String>> indexed = StreamX.zipWithIndex(Stream.of("a", "b", "c"))
     *     .toList();
     * // Result: [IndexedValue{value=a, index=0}, IndexedValue{value=b, index=1}, IndexedValue{value=c, index=2}]
     * 
     * // Process with indices
     * StreamX.zipWithIndex(words.stream())
     *     .filter(indexed -> indexed.index() % 2 == 0)  // Even indices only
     *     .map(IndexedValue::value)
     *     .toList();
     * }</pre>
     * 
     * @param <T>    the type of stream elements
     * @param stream the input stream to associate with indices
     * @return a stream of IndexedValue objects containing each element paired with its index
     * @throws NullPointerException if stream is null
     * @since 1.0
     * @see IndexedValue
     * @see #withIndex(Stream, BiFunction)
     */
    public static <T> Stream<IndexedValue<T>> zipWithIndex(Stream<T> stream) {
        return StreamSupport.stream(
            new IndexingSpliterator<>(stream.spliterator()),
            stream.isParallel()
        );
    }
    
    /**
     * Provides indexed access to stream elements with a BiFunction mapper.
     * 
     * <p>This operation applies a function that receives both the stream element and its
     * zero-based index, allowing for index-aware transformations. This eliminates the need
     * for external counter variables or complex workarounds.</p>
     * 
     * <p><strong>Inspired by:</strong> Kotlin's indexed operations, functional programming patterns</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Create numbered list
     * List<String> numbered = StreamX.withIndex(Stream.of("apple", "banana", "cherry"),
     *     (fruit, index) -> (index + 1) + ". " + fruit)
     *     .toList();
     * // Result: ["1. apple", "2. banana", "3. cherry"]
     * 
     * // Conditional processing based on index
     * List<String> processed = StreamX.withIndex(words.stream(),
     *     (word, index) -> index % 2 == 0 ? word.toUpperCase() : word.toLowerCase())
     *     .toList();
     * 
     * // Create CSV row with line numbers
     * String csv = StreamX.withIndex(dataRows.stream(),
     *     (row, index) -> index + "," + String.join(",", row))
     *     .collect(Collectors.joining("\n"));
     * }</pre>
     * 
     * @param <T>    the type of stream elements
     * @param <R>    the type of the result elements
     * @param stream the input stream to process with indices
     * @param mapper function that takes (value, index) and returns a transformed result
     * @return a stream of mapped results where each element was processed with its index
     * @throws NullPointerException if stream or mapper is null
     * @since 1.0
     * @see #zipWithIndex(Stream)
     */
    public static <T, R> Stream<R> withIndex(Stream<T> stream, BiFunction<T, Integer, R> mapper) {
        return zipWithIndex(stream)
            .map(indexed -> mapper.apply(indexed.value(), indexed.index()));
    }
    
    // ========== Zipping Operations ==========
    
    /**
     * Zips two streams together using a combiner function.
     * 
     * <p>This operation combines elements from two streams by applying a function to corresponding
     * elements. The resulting stream terminates when the shorter input stream is exhausted.
     * This is a fundamental operation missing from standard Java Streams.</p>
     * 
     * <p><strong>Inspired by:</strong> Python's {@code zip()}, Scala's {@code zip}, Haskell's {@code zipWith}</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Combine names and ages
     * Stream<String> names = Stream.of("Alice", "Bob", "Charlie");
     * Stream<Integer> ages = Stream.of(25, 30, 35);
     * List<String> nameAges = StreamX.zip(names, ages, 
     *     (name, age) -> name + " (" + age + ")")
     *     .toList();
     * // Result: ["Alice (25)", "Bob (30)", "Charlie (35)"]
     * 
     * // Create coordinate pairs
     * Stream<Integer> x = Stream.of(1, 2, 3);
     * Stream<Integer> y = Stream.of(10, 20, 30);
     * List<Point> points = StreamX.zip(x, y, Point::new).toList();
     * 
     * // Stops at shortest stream
     * Stream<String> short = Stream.of("a", "b");
     * Stream<Integer> long = Stream.of(1, 2, 3, 4, 5);
     * List<String> result = StreamX.zip(short, long, (s, i) -> s + i).toList();
     * // Result: ["a1", "b2"] - stops at shortest
     * }</pre>
     * 
     * @param <T>      the type of elements in the first stream
     * @param <U>      the type of elements in the second stream
     * @param <R>      the type of elements in the result stream
     * @param first    the first input stream
     * @param second   the second input stream  
     * @param combiner function to combine corresponding elements
     * @return a stream of combined results, terminating when the shorter input stream is exhausted
     * @throws NullPointerException if any parameter is null
     * @since 1.0
     * @see #zip(Stream, Stream, Stream, TriFunction)
     * @see #zipAll(Stream, Stream)
     * @see #zipLongest(Stream, Stream, Object, Object)
     */
    public static <T, U, R> Stream<R> zip(Stream<T> first, Stream<U> second, BiFunction<T, U, R> combiner) {
        Iterator<T> firstIter = first.iterator();
        Iterator<U> secondIter = second.iterator();
        
        Iterator<R> resultIter = new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return firstIter.hasNext() && secondIter.hasNext();
            }
            
            @Override
            public R next() {
                return combiner.apply(firstIter.next(), secondIter.next());
            }
        };
        
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(resultIter, Spliterator.ORDERED),
            false
        );
    }
    
    /**
     * Zips three streams together using a TriFunction combiner.
     * 
     * <p>This operation extends the two-stream zip to handle three streams simultaneously.
     * Like the two-stream version, it terminates when the shortest input stream is exhausted.</p>
     * 
     * <p><strong>Inspired by:</strong> Functional programming languages with multi-argument zip operations</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Combine names, ages, and cities
     * Stream<String> names = Stream.of("Alice", "Bob");
     * Stream<Integer> ages = Stream.of(25, 30);
     * Stream<String> cities = Stream.of("NYC", "LA");
     * 
     * List<String> profiles = StreamX.zip(names, ages, cities,
     *     (name, age, city) -> name + ", " + age + ", from " + city)
     *     .toList();
     * // Result: ["Alice, 25, from NYC", "Bob, 30, from LA"]
     * 
     * // Create 3D coordinates
     * List<Point3D> coordinates = StreamX.zip(xStream, yStream, zStream, Point3D::new)
     *     .toList();
     * }</pre>
     * 
     * @param <T>      the type of elements in the first stream
     * @param <U>      the type of elements in the second stream
     * @param <V>      the type of elements in the third stream
     * @param <R>      the type of elements in the result stream
     * @param first    the first input stream
     * @param second   the second input stream
     * @param third    the third input stream
     * @param combiner function to combine corresponding elements from all three streams
     * @return a stream of combined results, terminating when the shortest input stream is exhausted
     * @throws NullPointerException if any parameter is null
     * @since 1.0
     * @see #zip(Stream, Stream, BiFunction)
     * @see TriFunction
     */
    public static <T, U, V, R> Stream<R> zip(Stream<T> first, Stream<U> second, Stream<V> third, 
                                             TriFunction<T, U, V, R> combiner) {
        Stream<Pair<T, U>> pairs = zip(first, second, Pair::new);
        return zip(pairs, third, (pair, v) -> combiner.apply(pair.first(), pair.second(), v));
    }
    
    // ========== Windowing Operations ==========
    
    /**
     * Groups stream elements into fixed-size chunks.
     * 
     * <p>This operation partitions the input stream into consecutive sublists of the specified size.
     * The last chunk may contain fewer elements if the stream size is not evenly divisible by the chunk size.
     * This is particularly useful for batch processing or pagination of stream data.</p>
     * 
     * <p><strong>Inspired by:</strong> Kotlin's {@code chunked()}, Clojure's {@code partition}</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Basic chunking
     * List<List<Integer>> chunks = StreamX.chunked(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9), 3)
     *     .toList();
     * // Result: [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
     * 
     * // Last chunk may be smaller
     * List<List<String>> words = StreamX.chunked(Stream.of("a", "b", "c", "d", "e"), 2)
     *     .toList();
     * // Result: [["a", "b"], ["c", "d"], ["e"]]
     * 
     * // Process data in batches
     * StreamX.chunked(largeDataStream, 1000)
     *     .forEach(batch -> processBatch(batch));
     * 
     * // Pagination
     * List<List<Item>> pages = StreamX.chunked(items.stream(), PAGE_SIZE)
     *     .toList();
     * }</pre>
     * 
     * @param <T>    the type of stream elements
     * @param stream the input stream to chunk
     * @param size   the size of each chunk (must be positive)
     * @return a stream of lists, each containing up to 'size' elements from the input stream
     * @throws IllegalArgumentException if size is not positive
     * @throws NullPointerException if stream is null
     * @since 1.0
     * @see #windowed(Stream, int)
     * @see #sliding(Stream, int, int)
     */
    public static <T> Stream<List<T>> chunked(Stream<T> stream, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        
        Iterator<T> iterator = stream.iterator();
        Iterator<List<T>> chunkIterator = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public List<T> next() {
                List<T> chunk = new ArrayList<>(size);
                for (int i = 0; i < size && iterator.hasNext(); i++) {
                    chunk.add(iterator.next());
                }
                return chunk;
            }
        };
        
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(chunkIterator, Spliterator.ORDERED),
            false
        );
    }
    
    /**
     * Creates sliding windows of the specified size over the stream elements.
     * 
     * <p>This operation generates overlapping sublists where each window contains the specified
     * number of consecutive elements. Each window slides one position forward, creating overlapping
     * views of the data. This is useful for analyzing sequences, calculating moving averages, 
     * or processing time-series data.</p>
     * 
     * <p><strong>Inspired by:</strong> Kotlin's {@code windowed()}, signal processing sliding windows</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Basic sliding windows
     * List<List<Integer>> windows = StreamX.windowed(Stream.of(1, 2, 3, 4, 5), 3)
     *     .toList();
     * // Result: [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
     * 
     * // Moving average calculation
     * List<Double> movingAverages = StreamX.windowed(prices.stream(), 3)
     *     .map(window -> window.stream().mapToDouble(Double::doubleValue).average().orElse(0.0))
     *     .toList();
     * 
     * // Pattern detection in sequences
     * boolean hasIncreasingTrend = StreamX.windowed(values.stream(), 3)
     *     .anyMatch(window -> window.get(0) < window.get(1) && window.get(1) < window.get(2));
     * }</pre>
     * 
     * @param <T>    the type of stream elements
     * @param stream the input stream to create windows from
     * @param size   the size of each window (must be positive)
     * @return a stream of overlapping windows, each containing 'size' consecutive elements
     * @throws IllegalArgumentException if size is not positive
     * @throws NullPointerException if stream is null
     * @since 1.0
     * @see #chunked(Stream, int)
     * @see #sliding(Stream, int, int)
     */
    public static <T> Stream<List<T>> windowed(Stream<T> stream, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }
        
        List<T> elements = stream.collect(Collectors.toList());
        if (elements.size() < size) {
            return Stream.empty();
        }
        
        return Stream.iterate(0, i -> i + 1)
            .limit(elements.size() - size + 1)
            .map(i -> elements.subList(i, i + size));
    }
    
    /**
     * Creates sliding windows with custom step size.
     * 
     * <p>This operation generates overlapping or non-overlapping sublists where each window
     * contains the specified number of consecutive elements, but advances by the specified
     * step size. When step equals window size, this produces non-overlapping chunks.
     * When step is smaller than window size, windows overlap.</p>
     * 
     * <p><strong>Inspired by:</strong> Signal processing windowing, time-series analysis</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Overlapping windows (step < windowSize)
     * List<List<Integer>> overlapping = StreamX.sliding(Stream.of(1, 2, 3, 4, 5, 6), 3, 2)
     *     .toList();
     * // Result: [[1, 2, 3], [3, 4, 5]] - step by 2, overlap by 1
     * 
     * // Non-overlapping chunks (step = windowSize)
     * List<List<Integer>> chunks = StreamX.sliding(Stream.of(1, 2, 3, 4, 5, 6), 3, 3)
     *     .toList();
     * // Result: [[1, 2, 3], [4, 5, 6]] - same as chunked(3)
     * 
     * // Sparse sampling (step > windowSize)
     * List<List<Integer>> sparse = StreamX.sliding(Stream.of(1, 2, 3, 4, 5, 6, 7, 8), 2, 3)
     *     .toList();
     * // Result: [[1, 2], [4, 5], [7, 8]] - skip elements between windows
     * }</pre>
     * 
     * @param <T>        the type of stream elements
     * @param stream     the input stream to create windows from
     * @param windowSize the size of each window (must be positive)
     * @param step       the step size between windows (must be positive)
     * @return a stream of windows, each containing 'windowSize' consecutive elements, 
     *         advanced by 'step' positions
     * @throws IllegalArgumentException if windowSize or step is not positive
     * @throws NullPointerException if stream is null
     * @since 1.0
     * @see #windowed(Stream, int)
     * @see #chunked(Stream, int)
     */
    public static <T> Stream<List<T>> sliding(Stream<T> stream, int windowSize, int step) {
        if (windowSize <= 0 || step <= 0) {
            throw new IllegalArgumentException("Window size and step must be positive");
        }
        
        List<T> elements = stream.collect(Collectors.toList());
        if (elements.size() < windowSize) {
            return Stream.empty();
        }
        
        return Stream.iterate(0, i -> i + step)
            .takeWhile(i -> i + windowSize <= elements.size())
            .map(i -> elements.subList(i, i + windowSize));
    }
    
    // ========== Distinct Operations ==========
    
    /**
     * Removes duplicates based on a key extractor function.
     * 
     * <p>This operation filters the stream to keep only the first occurrence of each element
     * based on the key returned by the extractor function. This is more flexible than the
     * standard {@code distinct()} method as it allows custom uniqueness criteria.</p>
     * 
     * <p><strong>Inspired by:</strong> LINQ's {@code DistinctBy}, various functional languages</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Remove duplicates by name
     * List<Person> unique = StreamX.distinctBy(people.stream(), Person::getName)
     *     .toList();
     * 
     * // Keep first occurrence of each string length
     * List<String> byLength = StreamX.distinctBy(words.stream(), String::length)
     *     .toList();
     * }</pre>
     * 
     * @param <T>          the type of stream elements
     * @param <K>          the type of the extracted key
     * @param stream       the input stream
     * @param keyExtractor function to extract the uniqueness key from each element
     * @return a stream with duplicates removed based on the extracted key
     * @throws NullPointerException if stream or keyExtractor is null
     * @since 1.0
     */
    public static <T, K> Stream<T> distinctBy(Stream<T> stream, Function<T, K> keyExtractor) {
        Set<K> seen = new HashSet<>();
        return stream.filter(element -> seen.add(keyExtractor.apply(element)));
    }
    
    // ========== Advanced Filtering ==========
    
    /**
     * Takes elements while predicate is true, then stops
     */
    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> predicate) {
        return stream.takeWhile(predicate);
    }
    
    /**
     * Takes elements until predicate becomes true (exclusive)
     */
    public static <T> Stream<T> takeUntil(Stream<T> stream, Predicate<T> predicate) {
        return stream.takeWhile(predicate.negate());
    }
    
    /**
     * Drops elements while predicate is true, then includes the rest
     */
    public static <T> Stream<T> dropWhile(Stream<T> stream, Predicate<T> predicate) {
        return stream.dropWhile(predicate);
    }
    
    // ========== Safe Operations ==========
    
    /**
     * Maps elements safely, providing a default value if an exception occurs
     */
    public static <T, R> Stream<R> mapSafely(Stream<T> stream, Function<T, R> mapper, R defaultValue) {
        return stream.map(element -> {
            try {
                return mapper.apply(element);
            } catch (Exception e) {
                return defaultValue;
            }
        });
    }
    
    /**
     * Filters elements safely, skipping elements that throw exceptions
     */
    public static <T> Stream<T> filterSafely(Stream<T> stream, Predicate<T> predicate) {
        return stream.filter(element -> {
            try {
                return predicate.test(element);
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    // ========== Async Operations ==========
    
    /**
     * Maps elements to CompletableFutures using a custom executor
     */
    public static <T, R> Stream<CompletableFuture<R>> asyncMap(Stream<T> stream, 
                                                               Function<T, R> mapper, 
                                                               Executor executor) {
        return stream.map(element -> CompletableFuture.supplyAsync(() -> mapper.apply(element), executor));
    }
    
    /**
     * Maps elements to CompletableFutures using the common ForkJoinPool
     */
    public static <T, R> Stream<CompletableFuture<R>> asyncMap(Stream<T> stream, Function<T, R> mapper) {
        return stream.map(element -> CompletableFuture.supplyAsync(() -> mapper.apply(element)));
    }
    
    /**
     * Collects all CompletableFutures and waits for completion
     */
    public static <T> List<T> awaitAll(Stream<CompletableFuture<T>> futureStream) {
        List<CompletableFuture<T>> futures = futureStream.toList();
        return futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }
    
    // ========== Debugging and Monitoring ==========
    
    /**
     * Logs each element passing through the stream
     */
    public static <T> Stream<T> debug(Stream<T> stream, String message) {
        return stream.peek(element -> System.out.println(message + ": " + element));
    }
    
    /**
     * Performs a side effect on each element (cleaner than peek)
     */
    public static <T> Stream<T> tap(Stream<T> stream, Consumer<T> action) {
        return stream.peek(action);
    }
    
    // ========== Mathematical Operations ==========
    
    /**
     * Calculates comprehensive statistics for a numeric stream
     */
    public static StreamStatistics statistics(Stream<? extends Number> stream) {
        List<Double> values = stream.map(Number::doubleValue).collect(Collectors.toList());
        return new StreamStatistics(values);
    }
    
    /**
     * Finds the median value
     */
    public static OptionalDouble median(Stream<? extends Number> stream) {
        List<Double> sorted = stream.map(Number::doubleValue)
            .sorted()
            .toList();
        
        if (sorted.isEmpty()) {
            return OptionalDouble.empty();
        }
        
        int size = sorted.size();
        if (size % 2 == 0) {
            return OptionalDouble.of((sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0);
        } else {
            return OptionalDouble.of(sorted.get(size / 2));
        }
    }
    
    /**
     * Finds the most frequently occurring element
     */
    public static <T> Optional<T> mode(Stream<T> stream) {
        return stream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }
    
    // ========== Scan Operations (Kotlin/Scala/Haskell inspired) ==========
    
    /**
     * Performs a scan operation (running fold) returning all intermediate results.
     * 
     * <p>This is one of the most requested missing features from Java Streams. A scan operation
     * is like a reduce, but it returns all the intermediate accumulation results rather than
     * just the final result. This is extremely useful for calculating running totals, 
     * cumulative statistics, or any scenario where you need to see the progression of an accumulation.</p>
     * 
     * <p><strong>Inspired by:</strong> Kotlin's {@code scan()}, Scala's {@code scanLeft()}, 
     * Haskell's {@code scanl}</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Running sum with initial value
     * List<Integer> runningSum = StreamX.scan(Stream.of(1, 2, 3, 4, 5), 0, Integer::sum)
     *     .toList();
     * // Result: [0, 1, 3, 6, 10, 15] - includes initial value
     * 
     * // String concatenation scan
     * List<String> building = StreamX.scan(Stream.of("Hello", " ", "World"), "", String::concat)
     *     .toList();
     * // Result: ["", "Hello", "Hello ", "Hello World"]
     * 
     * // Running maximum
     * List<Integer> runningMax = StreamX.scan(Stream.of(3, 1, 4, 1, 5), Integer.MIN_VALUE, Integer::max)
     *     .toList();
     * // Result: [MIN_VALUE, 3, 3, 4, 4, 5]
     * 
     * // Factorial calculation
     * List<Integer> factorials = StreamX.scan(Stream.of(1, 2, 3, 4, 5), 1, (a, b) -> a * b)
     *     .toList();
     * // Result: [1, 1, 2, 6, 24, 120]
     * }</pre>
     * 
     * @param <T>         the type of stream elements
     * @param <R>         the type of the accumulation result
     * @param stream      the input stream to scan
     * @param identity    the initial value for the accumulation
     * @param accumulator function that combines the current accumulation with the next element
     * @return a stream containing all intermediate accumulation results, starting with the identity
     * @throws NullPointerException if stream or accumulator is null
     * @since 1.0
     * @see #runningReduce(Stream, BinaryOperator)
     */
    public static <T, R> Stream<R> scan(Stream<T> stream, R identity, BiFunction<R, T, R> accumulator) {
        List<T> elements = stream.toList();
        if (elements.isEmpty()) {
            return Stream.of(identity);
        }
        
        List<R> results = new ArrayList<>();
        R current = identity;
        results.add(current);
        
        for (T element : elements) {
            current = accumulator.apply(current, element);
            results.add(current);
        }
        
        return results.stream();
    }
    
    /**
     * Performs a running reduce operation returning all intermediate accumulation results.
     * 
     * <p>This operation is similar to {@link #scan} but does not include the initial value
     * in the output stream. It starts with the first element and shows the progression of
     * the accumulation through all subsequent elements. This is perfect for running totals,
     * progressive calculations, or analyzing how values evolve through a sequence.</p>
     * 
     * <p><strong>Inspired by:</strong> Reductions in various functional programming languages</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Running sum without initial value
     * List<Integer> runningSum = StreamX.runningReduce(Stream.of(1, 2, 3, 4, 5), Integer::sum)
     *     .toList();
     * // Result: [1, 3, 6, 10, 15] - no initial value included
     * 
     * // Running product
     * List<Integer> runningProduct = StreamX.runningReduce(Stream.of(2, 3, 4), (a, b) -> a * b)
     *     .toList();
     * // Result: [2, 6, 24]
     * 
     * // String accumulation
     * List<String> building = StreamX.runningReduce(Stream.of("a", "b", "c"), String::concat)
     *     .toList();
     * // Result: ["a", "ab", "abc"]
     * 
     * // Running minimum
     * List<Integer> runningMin = StreamX.runningReduce(Stream.of(5, 2, 8, 1, 9), Integer::min)
     *     .toList();
     * // Result: [5, 2, 2, 1, 1]
     * }</pre>
     * 
     * @param <T>         the type of stream elements
     * @param stream      the input stream to reduce
     * @param accumulator function that combines two elements
     * @return a stream containing all intermediate accumulation results, 
     *         or empty stream if input is empty
     * @throws NullPointerException if stream or accumulator is null
     * @since 1.0
     * @see #scan(Stream, Object, BiFunction)
     */
    public static <T> Stream<T> runningReduce(Stream<T> stream, BinaryOperator<T> accumulator) {
        Iterator<T> iterator = stream.iterator();
        if (!iterator.hasNext()) {
            return Stream.empty();
        }
        
        List<T> results = new ArrayList<>();
        T current = iterator.next();
        results.add(current);
        
        while (iterator.hasNext()) {
            current = accumulator.apply(current, iterator.next());
            results.add(current);
        }
        
        return results.stream();
    }
    
    // ========== Advanced Partitioning (Kotlin/Scala inspired) ==========
    
    /**
     * Partitions stream into two lists based on predicate in a single pass.
     * 
     * <p>This operation efficiently splits the input stream into two collections:
     * elements that match the predicate and elements that don't. Unlike using two
     * separate filter operations, this processes the stream only once, making it
     * more efficient for large datasets.</p>
     * 
     * <p><strong>Inspired by:</strong> Kotlin's {@code partition()}, Scala's {@code partition()}</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Separate even and odd numbers
     * Pair<List<Integer>, List<Integer>> evenOdd = StreamX.partition(
     *     Stream.of(1, 2, 3, 4, 5, 6), 
     *     n -> n % 2 == 0
     * );
     * List<Integer> evens = evenOdd.first();   // [2, 4, 6]
     * List<Integer> odds = evenOdd.second();   // [1, 3, 5]
     * 
     * // Separate valid and invalid records  
     * Pair<List<Record>, List<Record>> validInvalid = StreamX.partition(
     *     records.stream(),
     *     Record::isValid
     * );
     * // Process valid records differently from invalid ones
     * 
     * // Categorize students by passing grade
     * Pair<List<Student>, List<Student>> passFaile = StreamX.partition(
     *     students.stream(),
     *     student -> student.getGrade() >= 60
     * );
     * }</pre>
     * 
     * @param <T>       the type of stream elements
     * @param stream    the input stream to partition
     * @param predicate the condition to test elements against
     * @return a Pair where first contains matching elements, second contains non-matching
     * @throws NullPointerException if stream or predicate is null
     * @since 1.0
     * @see #span(Stream, Predicate)
     * @see Pair
     */
    public static <T> Pair<List<T>, List<T>> partition(Stream<T> stream, Predicate<T> predicate) {
        List<T> trueList = new ArrayList<>();
        List<T> falseList = new ArrayList<>();
        
        stream.forEach(element -> {
            if (predicate.test(element)) {
                trueList.add(element);
            } else {
                falseList.add(element);
            }
        });
        
        return new Pair<>(trueList, falseList);
    }
    
    /**
     * Splits stream at first element that doesn't match predicate
     * Inspired by Haskell's span and Scala's span
     * 
     * @return Pair where first contains prefix matching predicate, second contains remainder
     */
    public static <T> Pair<List<T>, List<T>> span(Stream<T> stream, Predicate<T> predicate) {
        List<T> elements = stream.toList();
        List<T> prefix = new ArrayList<>();
        List<T> remainder = new ArrayList<>();
        
        int splitPoint = 0;
        for (T element : elements) {
            if (predicate.test(element)) {
                prefix.add(element);
                splitPoint++;
            } else {
                break;
            }
        }
        
        for (int i = splitPoint; i < elements.size(); i++) {
            remainder.add(elements.get(i));
        }
        
        return new Pair<>(prefix, remainder);
    }
    
    /**
     * Groups consecutive elements by the result of the classifier function
     * Inspired by Clojure's partition-by
     */
    public static <T, K> Stream<List<T>> partitionBy(Stream<T> stream, Function<T, K> classifier) {
        List<T> elements = stream.toList();
        if (elements.isEmpty()) {
            return Stream.empty();
        }
        
        List<List<T>> groups = new ArrayList<>();
        List<T> currentGroup = new ArrayList<>();
        K currentKey = null;
        
        for (T element : elements) {
            K key = classifier.apply(element);
            
            if (currentKey == null || !Objects.equals(currentKey, key)) {
                if (!currentGroup.isEmpty()) {
                    groups.add(new ArrayList<>(currentGroup));
                    currentGroup.clear();
                }
                currentKey = key;
            }
            currentGroup.add(element);
        }
        
        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }
        
        return groups.stream();
    }
    
    // ========== Sequence Generation (Haskell/Scala inspired) ==========
    
    /**
     * Generates an infinite stream by repeatedly applying a function
     * Inspired by Haskell's iterate and Scala's iterate
     */
    public static <T> Stream<T> iterate(T seed, UnaryOperator<T> function) {
        return Stream.iterate(seed, function);
    }
    
    /**
     * Creates an infinite stream that cycles through the provided elements
     * Inspired by Haskell's cycle and Clojure's cycle
     */
    @SafeVarargs
    public static <T> Stream<T> cycle(T... elements) {
        if (elements.length == 0) {
            return Stream.empty();
        }
        List<T> elementList = Arrays.asList(elements);
        return Stream.generate(new Iterator<T>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return true; // Infinite
            }
            
            @Override
            public T next() {
                T element = elementList.get(index);
                index = (index + 1) % elementList.size();
                return element;
            }
        }::next);
    }
    
    /**
     * Creates an infinite stream of the same element
     * Inspired by Haskell's repeat
     */
    public static <T> Stream<T> repeat(T element) {
        return Stream.generate(() -> element);
    }
    
    /**
     * Creates a stream with N copies of the same element
     * Inspired by Haskell's replicate
     */
    public static <T> Stream<T> replicate(int count, T element) {
        return Stream.generate(() -> element).limit(count);
    }
    
    /**
     * Creates a range of integers with step
     * Inspired by Kotlin's range operations
     */
    public static Stream<Integer> range(int start, int end, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be zero");
        }
        
        if (step > 0) {
            return Stream.iterate(start, i -> i < end, i -> i + step);
        } else {
            return Stream.iterate(start, i -> i > end, i -> i + step);
        }
    }
    
    /**
     * Creates a range of integers (step = 1)
     */
    public static Stream<Integer> range(int start, int end) {
        return range(start, end, 1);
    }
    
    // ========== Enhanced Selectors (Kotlin inspired) ==========
    
    /**
     * Finds the maximum element by the given selector function.
     * 
     * <p>This operation finds the element that has the maximum value when the selector
     * function is applied. This is more convenient than using {@code max(Comparator.comparing(selector))}
     * and is a common pattern in functional programming languages.</p>
     * 
     * <p><strong>Inspired by:</strong> Kotlin's {@code maxBy()}, LINQ's {@code MaxBy}</p>
     * 
     * <h3>Examples:</h3>
     * <pre>{@code
     * // Find oldest person
     * Optional<Person> oldest = StreamX.maxBy(people.stream(), Person::getAge);
     * 
     * // Find longest string
     * Optional<String> longest = StreamX.maxBy(words.stream(), String::length);
     * 
     * // Find product with highest price
     * Optional<Product> mostExpensive = StreamX.maxBy(products.stream(), Product::getPrice);
     * }</pre>
     * 
     * @param <T>      the type of stream elements
     * @param <R>      the type of the selector result (must be Comparable)
     * @param stream   the input stream
     * @param selector function to extract the comparison value from each element
     * @return an Optional containing the maximum element, or empty if stream is empty
     * @throws NullPointerException if stream or selector is null
     * @since 1.0
     * @see #minBy(Stream, Function)
     * @see #sortedBy(Stream, Function)
     */
    public static <T, R extends Comparable<R>> Optional<T> maxBy(Stream<T> stream, Function<T, R> selector) {
        return stream.max(Comparator.comparing(selector));
    }
    
    /**
     * Finds the minimum element by the given selector
     * Inspired by Kotlin's minBy
     */
    public static <T, R extends Comparable<R>> Optional<T> minBy(Stream<T> stream, Function<T, R> selector) {
        return stream.min(Comparator.comparing(selector));
    }
    
    /**
     * Sorts elements by the given selector
     * Inspired by Kotlin's sortedBy
     */
    public static <T, R extends Comparable<R>> Stream<T> sortedBy(Stream<T> stream, Function<T, R> selector) {
        return stream.sorted(Comparator.comparing(selector));
    }
    
    /**
     * Sorts elements by the given selector in descending order
     * Inspired by Kotlin's sortedByDescending
     */
    public static <T, R extends Comparable<R>> Stream<T> sortedByDescending(Stream<T> stream, Function<T, R> selector) {
        return stream.sorted(Comparator.comparing(selector).reversed());
    }
    
    /**
     * Filters out null elements
     * Inspired by Kotlin's filterNotNull
     */
    public static <T> Stream<T> filterNotNull(Stream<T> stream) {
        return stream.filter(Objects::nonNull);
    }
    
    /**
     * Filters elements that are instances of the specified class
     * Inspired by Kotlin's filterIsInstance
     */
    @SuppressWarnings("unchecked")
    public static <T, R> Stream<R> filterIsInstance(Stream<T> stream, Class<R> clazz) {
        return stream.filter(clazz::isInstance).map(element -> (R) element);
    }
    
    /**
     * Returns first element or null if empty
     * Inspired by Kotlin's firstOrNull
     */
    public static <T> T firstOrNull(Stream<T> stream) {
        return stream.findFirst().orElse(null);
    }
    
    /**
     * Returns last element or null if empty
     * Inspired by Kotlin's lastOrNull
     */
    public static <T> T lastOrNull(Stream<T> stream) {
        return stream.reduce((first, second) -> second).orElse(null);
    }
    
    // ========== Advanced Stream Manipulation ==========
    
    /**
     * Unzips a stream of pairs into two separate streams
     * Inspired by Scala's unzip
     */
    public static <T, U> Pair<Stream<T>, Stream<U>> unzip(Stream<Pair<T, U>> stream) {
        List<Pair<T, U>> pairs = stream.toList();
        
        Stream<T> firsts = pairs.stream().map(Pair::first);
        Stream<U> seconds = pairs.stream().map(Pair::second);
        
        return new Pair<>(firsts, seconds);
    }
    
    /**
     * Creates pairs of consecutive elements
     * Inspired by various FP languages' pairwise operations
     */
    public static <T> Stream<Pair<T, T>> pairwise(Stream<T> stream) {
        List<T> elements = stream.toList();
        if (elements.size() < 2) {
            return Stream.empty();
        }
        
        return IntStream.range(0, elements.size() - 1)
            .mapToObj(i -> new Pair<>(elements.get(i), elements.get(i + 1)));
    }
    
    /**
     * Inserts separator between stream elements
     * Inspired by Clojure's interpose
     */
    public static <T> Stream<T> interpose(Stream<T> stream, T separator) {
        List<T> elements = stream.toList();
        if (elements.size() <= 1) {
            return elements.stream();
        }
        
        List<T> result = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            result.add(elements.get(i));
            if (i < elements.size() - 1) {
                result.add(separator);
            }
        }
        
        return result.stream();
    }
    
    /**
     * Transposes a stream of lists (flips 2D structure)
     * Inspired by Haskell's transpose
     */
    public static <T> Stream<List<T>> transpose(Stream<List<T>> stream) {
        List<List<T>> lists = stream.toList();
        if (lists.isEmpty()) {
            return Stream.empty();
        }
        
        int maxSize = lists.stream().mapToInt(List::size).max().orElse(0);
        
        return IntStream.range(0, maxSize)
            .mapToObj(i -> lists.stream()
                .filter(list -> i < list.size())
                .map(list -> list.get(i))
                .toList());
    }
    
    // ========== Enhanced Side Effects (Kotlin inspired) ==========
    
    /**
     * Performs action on each element and returns the stream
     * Inspired by Kotlin's onEach - cleaner than peek
     */
    public static <T> Stream<T> onEach(Stream<T> stream, Consumer<T> action) {
        return stream.peek(action);
    }
    
    /**
     * Performs action on the stream and returns it unchanged
     * Inspired by Kotlin's also
     */
    public static <T> Stream<T> also(Stream<T> stream, Consumer<Stream<T>> action) {
        action.accept(stream);
        return stream;
    }
    
    /**
     * Transforms the stream with the given function
     * Inspired by Kotlin's let
     */
    public static <T, R> R let(Stream<T> stream, Function<Stream<T>, R> transform) {
        return transform.apply(stream);
    }
    
    // ========== Mathematical Operations (Various FP languages) ==========
    
    /**
     * Calculates cumulative sum
     * Inspired by NumPy/scientific computing libraries
     */
    public static Stream<Integer> cumSum(Stream<Integer> stream) {
        return runningReduce(stream, Integer::sum);
    }
    
    /**
     * Calculates cumulative product
     */
    public static Stream<Integer> cumProduct(Stream<Integer> stream) {
        return runningReduce(stream, (a, b) -> a * b);
    }
    
    /**
     * Calculates differences between consecutive elements
     */
    public static <T extends Number> Stream<Double> diff(Stream<T> stream) {
        return pairwise(stream.map(Number::doubleValue))
            .map(pair -> pair.second() - pair.first());
    }
    
    /**
     * Creates frequency map of elements
     * Inspired by Python's collections.Counter and various FP languages
     */
    public static <T> Map<T, Long> frequencies(Stream<T> stream) {
        return stream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
    
    // ========== Advanced Zipping Operations ==========
    
    /**
     * Zips streams with default values for shorter streams
     * Inspired by Scala's zipAll
     */
    public static <T, U> Stream<Pair<Optional<T>, Optional<U>>> zipAll(Stream<T> first, Stream<U> second) {
        Iterator<T> firstIter = first.iterator();
        Iterator<U> secondIter = second.iterator();
        
        List<Pair<Optional<T>, Optional<U>>> result = new ArrayList<>();
        
        while (firstIter.hasNext() || secondIter.hasNext()) {
            Optional<T> firstElement = firstIter.hasNext() ? Optional.of(firstIter.next()) : Optional.empty();
            Optional<U> secondElement = secondIter.hasNext() ? Optional.of(secondIter.next()) : Optional.empty();
            result.add(new Pair<>(firstElement, secondElement));
        }
        
        return result.stream();
    }
    
    /**
     * Zips streams continuing until both are exhausted, using default values
     */
    public static <T, U> Stream<Pair<T, U>> zipLongest(Stream<T> first, Stream<U> second, T defaultFirst, U defaultSecond) {
        return zipAll(first, second)
            .map(pair -> new Pair<>(
                pair.first().orElse(defaultFirst),
                pair.second().orElse(defaultSecond)
            ));
    }

    // ========== String Operations (Kotlin inspired) ==========
    
    /**
     * Joins stream elements to string with rich formatting options
     * Inspired by Kotlin's joinToString
     */
    public static <T> String joinToString(Stream<T> stream, String separator, String prefix, String suffix, Function<T, String> transform) {
        return stream.map(transform)
            .collect(Collectors.joining(separator, prefix, suffix));
    }
    
    /**
     * Joins stream elements to string with separator
     */
    public static <T> String joinToString(Stream<T> stream, String separator) {
        return joinToString(stream, separator, "", "", Object::toString);
    }
    
    /**
     * Joins stream elements to string with default comma separator
     */
    public static <T> String joinToString(Stream<T> stream) {
        return joinToString(stream, ", ");
    }

    // ========== Stream Combination ==========
    
    /**
     * Interleaves elements from two streams
     */
    public static <T> Stream<T> interleave(Stream<T> first, Stream<T> second) {
        Iterator<T> firstIter = first.iterator();
        Iterator<T> secondIter = second.iterator();
        
        return Stream.generate(() -> {
                List<T> batch = new ArrayList<>();
                if (firstIter.hasNext()) batch.add(firstIter.next());
                if (secondIter.hasNext()) batch.add(secondIter.next());
                return batch;
            })
            .takeWhile(batch -> !batch.isEmpty())
            .flatMap(List::stream);
    }
    
    /**
     * Creates the intersection of two streams
     */
    public static <T> Stream<T> intersect(Stream<T> first, Stream<T> second) {
        Set<T> secondSet = second.collect(Collectors.toSet());
        return first.distinct().filter(secondSet::contains);
    }
    
    /**
     * Creates the union of multiple streams (with deduplication)
     */
    @SafeVarargs
    public static <T> Stream<T> union(Stream<T>... streams) {
        return Stream.of(streams)
            .flatMap(Function.identity())
            .distinct();
    }
}
