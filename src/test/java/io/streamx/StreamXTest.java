package io.streamx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StreamX Utility Tests")
class StreamXTest {
    
    @Test
    @DisplayName("zipWithIndex should associate elements with their indices")
    void testZipWithIndex() {
        List<String> words = List.of("apple", "banana", "cherry");
        List<IndexedValue<String>> result = StreamX.zipWithIndex(words.stream())
            .toList();
        
        assertThat(result).hasSize(3);
        assertThat(result.get(0).value()).isEqualTo("apple");
        assertThat(result.get(0).index()).isEqualTo(0);
        assertThat(result.get(1).value()).isEqualTo("banana");
        assertThat(result.get(1).index()).isEqualTo(1);
        assertThat(result.get(2).value()).isEqualTo("cherry");
        assertThat(result.get(2).index()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("withIndex should provide indexed mapping")
    void testWithIndex() {
        List<String> words = List.of("a", "b", "c");
        List<String> result = StreamX.withIndex(words.stream(), (value, index) -> index + ":" + value)
            .toList();
        
        assertThat(result).containsExactly("0:a", "1:b", "2:c");
    }
    
    @Test
    @DisplayName("zip should combine two streams")
    void testZipTwoStreams() {
        Stream<String> names = Stream.of("Alice", "Bob", "Charlie");
        Stream<Integer> ages = Stream.of(25, 30, 35);
        
        List<String> result = StreamX.zip(names, ages, (name, age) -> name + ":" + age)
            .toList();
        
        assertThat(result).containsExactly("Alice:25", "Bob:30", "Charlie:35");
    }
    
    @Test
    @DisplayName("zip should stop at shortest stream")
    void testZipShortestStream() {
        Stream<String> names = Stream.of("Alice", "Bob");
        Stream<Integer> ages = Stream.of(25, 30, 35, 40);
        
        List<String> result = StreamX.zip(names, ages, (name, age) -> name + ":" + age)
            .toList();
        
        assertThat(result).containsExactly("Alice:25", "Bob:30");
    }
    
    @Test
    @DisplayName("zip three streams should work correctly")
    void testZipThreeStreams() {
        Stream<String> names = Stream.of("Alice", "Bob");
        Stream<Integer> ages = Stream.of(25, 30);
        Stream<String> cities = Stream.of("NYC", "LA");
        
        List<String> result = StreamX.zip(names, ages, cities, 
            (name, age, city) -> name + ":" + age + ":" + city)
            .toList();
        
        assertThat(result).containsExactly("Alice:25:NYC", "Bob:30:LA");
    }
    
    @Test
    @DisplayName("chunked should group elements into fixed-size chunks")
    void testChunked() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<List<Integer>> chunks = StreamX.chunked(numbers, 3).toList();
        
        assertThat(chunks).hasSize(3);
        assertThat(chunks.get(0)).containsExactly(1, 2, 3);
        assertThat(chunks.get(1)).containsExactly(4, 5, 6);
        assertThat(chunks.get(2)).containsExactly(7, 8, 9);
    }
    
    @Test
    @DisplayName("chunked should handle incomplete last chunk")
    void testChunkedIncompleteChunk() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7);
        List<List<Integer>> chunks = StreamX.chunked(numbers, 3).toList();
        
        assertThat(chunks).hasSize(3);
        assertThat(chunks.get(0)).containsExactly(1, 2, 3);
        assertThat(chunks.get(1)).containsExactly(4, 5, 6);
        assertThat(chunks.get(2)).containsExactly(7);
    }
    
    @Test
    @DisplayName("windowed should create sliding windows")
    void testWindowed() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
        List<List<Integer>> windows = StreamX.windowed(numbers, 3).toList();
        
        assertThat(windows).hasSize(3);
        assertThat(windows.get(0)).containsExactly(1, 2, 3);
        assertThat(windows.get(1)).containsExactly(2, 3, 4);
        assertThat(windows.get(2)).containsExactly(3, 4, 5);
    }
    
    @Test
    @DisplayName("sliding should create windows with custom step")
    void testSliding() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> windows = StreamX.sliding(numbers, 3, 2).toList();
        
        assertThat(windows).hasSize(3);
        assertThat(windows.get(0)).containsExactly(1, 2, 3);
        assertThat(windows.get(1)).containsExactly(3, 4, 5);
        assertThat(windows.get(2)).containsExactly(5, 6, 7);
    }
    
    @Test
    @DisplayName("distinctBy should remove duplicates by key")
    void testDistinctBy() {
        record Person(String name, int age) {}
        
        Stream<Person> people = Stream.of(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Alice", 35) // Same name, different age
        );
        
        List<Person> unique = StreamX.distinctBy(people, Person::name).toList();
        
        assertThat(unique).hasSize(2);
        assertThat(unique.get(0).name()).isEqualTo("Alice");
        assertThat(unique.get(1).name()).isEqualTo("Bob");
    }
    
    @Test
    @DisplayName("takeUntil should take elements until condition is true")
    void testTakeUntil() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6);
        List<Integer> result = StreamX.takeUntil(numbers, n -> n > 3).toList();
        
        assertThat(result).containsExactly(1, 2, 3);
    }
    
    @Test
    @DisplayName("mapSafely should handle exceptions with default values")
    void testMapSafely() {
        Stream<String> numbers = Stream.of("1", "2", "invalid", "4");
        List<Integer> result = StreamX.mapSafely(numbers, Integer::parseInt, -1).toList();
        
        assertThat(result).containsExactly(1, 2, -1, 4);
    }
    
    @Test
    @DisplayName("filterSafely should skip elements that throw exceptions")
    void testFilterSafely() {
        Stream<String> values = Stream.of("1", "2", "invalid", "4");
        List<String> result = StreamX.filterSafely(values, s -> Integer.parseInt(s) > 0).toList();
        
        assertThat(result).containsExactly("1", "2", "4");
    }
    
    @Test
    @DisplayName("asyncMap should process elements asynchronously")
    void testAsyncMap() {
        Stream<Integer> numbers = Stream.of(1, 2, 3);
        
        List<Integer> result = StreamX.awaitAll(
            StreamX.asyncMap(numbers, n -> n * 2, Executors.newFixedThreadPool(2))
        );
        
        assertThat(result).containsExactlyInAnyOrder(2, 4, 6);
    }
    
    @Test
    @DisplayName("statistics should calculate comprehensive stats")
    void testStatistics() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
        StreamStatistics stats = StreamX.statistics(numbers);
        
        assertThat(stats.getCount()).isEqualTo(5);
        assertThat(stats.getSum()).isEqualTo(15.0);
        assertThat(stats.getAverage()).isEqualTo(3.0);
        assertThat(stats.getMin()).isEqualTo(1.0);
        assertThat(stats.getMax()).isEqualTo(5.0);
    }
    
    @Test
    @DisplayName("median should find middle value")
    void testMedian() {
        // Odd number of elements
        OptionalDouble median1 = StreamX.median(Stream.of(1, 2, 3, 4, 5));
        assertThat(median1).isPresent();
        assertThat(median1.getAsDouble()).isEqualTo(3.0);
        
        // Even number of elements
        OptionalDouble median2 = StreamX.median(Stream.of(1, 2, 3, 4));
        assertThat(median2).isPresent();
        assertThat(median2.getAsDouble()).isEqualTo(2.5);
    }
    
    @Test
    @DisplayName("mode should find most frequent element")
    void testMode() {
        Stream<String> values = Stream.of("a", "b", "a", "c", "a", "b");
        Optional<String> mode = StreamX.mode(values);
        
        assertThat(mode).isPresent();
        assertThat(mode.get()).isEqualTo("a");
    }
    
    @Test
    @DisplayName("interleave should alternate between streams")
    void testInterleave() {
        Stream<String> first = Stream.of("a", "c", "e");
        Stream<String> second = Stream.of("b", "d", "f");
        
        List<String> result = StreamX.interleave(first, second).toList();
        
        assertThat(result).containsExactly("a", "b", "c", "d", "e", "f");
    }
    
    @Test
    @DisplayName("intersect should find common elements")
    void testIntersect() {
        Stream<Integer> first = Stream.of(1, 2, 3, 4);
        Stream<Integer> second = Stream.of(3, 4, 5, 6);
        
        List<Integer> result = StreamX.intersect(first, second).toList();
        
        assertThat(result).containsExactlyInAnyOrder(3, 4);
    }
    
    @Test
    @DisplayName("union should combine streams without duplicates")
    void testUnion() {
        Stream<Integer> first = Stream.of(1, 2, 3);
        Stream<Integer> second = Stream.of(3, 4, 5);
        Stream<Integer> third = Stream.of(5, 6, 7);
        
        List<Integer> result = StreamX.union(first, second, third).toList();
        
        assertThat(result).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7);
    }
    
    @Test
    @DisplayName("chunked should throw exception for invalid size")
    void testChunkedInvalidSize() {
        Stream<Integer> numbers = Stream.of(1, 2, 3);
        
        assertThatThrownBy(() -> StreamX.chunked(numbers, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Chunk size must be positive");
    }
    
    @Test
    @DisplayName("windowed should return empty stream for insufficient elements")
    void testWindowedInsufficientElements() {
        Stream<Integer> numbers = Stream.of(1, 2);
        List<List<Integer>> windows = StreamX.windowed(numbers, 3).toList();
        
        assertThat(windows).isEmpty();
    }
    
    @Test
    @DisplayName("debug should log elements")
    void testDebug() {
        // This test mainly ensures the debug method doesn't break the stream
        List<Integer> result = StreamX.debug(Stream.of(1, 2, 3), "Testing")
            .toList();
        
        assertThat(result).containsExactly(1, 2, 3);
    }
}
