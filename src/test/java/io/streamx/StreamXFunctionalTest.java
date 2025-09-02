package io.streamx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StreamX Functional Programming Features Tests")
class StreamXFunctionalTest {
    
    // ========== Scan Operations Tests ==========
    
    @Test
    @DisplayName("scan should return all intermediate results of accumulation")
    void testScan() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
        List<Integer> result = StreamX.scan(numbers, 0, Integer::sum).toList();
        
        assertThat(result).containsExactly(0, 1, 3, 6, 10, 15);
    }
    
    @Test
    @DisplayName("scan with BiFunction should work with different types")
    void testScanBiFunction() {
        Stream<String> words = Stream.of("Hello", " ", "World");
        List<String> result = StreamX.scan(words, "", String::concat).toList();
        
        assertThat(result).containsExactly("", "Hello", "Hello ", "Hello World");
    }
    
    @Test
    @DisplayName("scan on empty stream should return only identity")
    void testScanEmpty() {
        Stream<Integer> empty = Stream.empty();
        List<Integer> result = StreamX.scan(empty, 42, Integer::sum).toList();
        
        assertThat(result).containsExactly(42);
    }
    
    @Test
    @DisplayName("runningReduce should show progressive reduction results")
    void testRunningReduce() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
        List<Integer> result = StreamX.runningReduce(numbers, Integer::sum).toList();
        
        assertThat(result).containsExactly(1, 3, 6, 10, 15);
    }
    
    @Test
    @DisplayName("runningReduce on empty stream should return empty")
    void testRunningReduceEmpty() {
        Stream<Integer> empty = Stream.empty();
        List<Integer> result = StreamX.runningReduce(empty, Integer::sum).toList();
        
        assertThat(result).isEmpty();
    }
    
    // ========== Advanced Partitioning Tests ==========
    
    @Test
    @DisplayName("partition should split stream into two lists based on predicate")
    void testPartition() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Pair<List<Integer>, List<Integer>> result = StreamX.partition(numbers, n -> n % 2 == 0);
        
        assertThat(result.first()).containsExactlyInAnyOrder(2, 4, 6, 8, 10);
        assertThat(result.second()).containsExactlyInAnyOrder(1, 3, 5, 7, 9);
    }
    
    @Test
    @DisplayName("span should split at first non-matching element")
    void testSpan() {
        Stream<Integer> numbers = Stream.of(1, 3, 5, 2, 4, 6, 7);
        Pair<List<Integer>, List<Integer>> result = StreamX.span(numbers, n -> n % 2 == 1);
        
        assertThat(result.first()).containsExactly(1, 3, 5);
        assertThat(result.second()).containsExactly(2, 4, 6, 7);
    }
    
    @Test
    @DisplayName("partitionBy should group consecutive elements with same classifier result")
    void testPartitionBy() {
        Stream<Integer> numbers = Stream.of(1, 1, 2, 2, 2, 1, 3, 3);
        List<List<Integer>> result = StreamX.partitionBy(numbers, n -> n).toList();
        
        assertThat(result).hasSize(4);
        assertThat(result.get(0)).containsExactly(1, 1);
        assertThat(result.get(1)).containsExactly(2, 2, 2);
        assertThat(result.get(2)).containsExactly(1);
        assertThat(result.get(3)).containsExactly(3, 3);
    }
    
    @Test
    @DisplayName("partitionBy with string length classifier")
    void testPartitionByStringLength() {
        Stream<String> words = Stream.of("a", "b", "cat", "dog", "elephant", "x", "y", "z");
        List<List<String>> result = StreamX.partitionBy(words, String::length).toList();
        
        assertThat(result).hasSize(4);
        assertThat(result.get(0)).containsExactly("a", "b");
        assertThat(result.get(1)).containsExactly("cat", "dog");
        assertThat(result.get(2)).containsExactly("elephant");
        assertThat(result.get(3)).containsExactly("x", "y", "z");
    }
    
    // ========== Sequence Generation Tests ==========
    
    @Test
    @DisplayName("replicate should create N copies of element")
    void testReplicate() {
        List<String> result = StreamX.replicate(5, "hello").toList();
        
        assertThat(result).hasSize(5);
        assertThat(result).allMatch("hello"::equals);
    }
    
    @Test
    @DisplayName("range with step should generate correct sequence")
    void testRangeWithStep() {
        List<Integer> result = StreamX.range(0, 10, 2).toList();
        
        assertThat(result).containsExactly(0, 2, 4, 6, 8);
    }
    
    @Test
    @DisplayName("range with negative step should work backwards")
    void testRangeNegativeStep() {
        List<Integer> result = StreamX.range(10, 0, -2).toList();
        
        assertThat(result).containsExactly(10, 8, 6, 4, 2);
    }
    
    @Test
    @DisplayName("range should throw exception for zero step")
    void testRangeZeroStep() {
        assertThatThrownBy(() -> StreamX.range(0, 10, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Step cannot be zero");
    }
    
    @Test
    @DisplayName("cycle should repeat elements infinitely")
    void testCycle() {
        List<String> result = StreamX.cycle("a", "b", "c").limit(7).toList();
        
        assertThat(result).containsExactly("a", "b", "c", "a", "b", "c", "a");
    }
    
    @Test
    @DisplayName("cycle with empty array should return empty stream")
    void testCycleEmpty() {
        @SuppressWarnings("unchecked")
        List<String> result = StreamX.<String>cycle().limit(5).toList();
        
        assertThat(result).isEmpty();
    }
    
    // ========== Enhanced Selectors Tests ==========
    
    @Test
    @DisplayName("maxBy should find element with maximum selector value")
    void testMaxBy() {
        record Person(String name, int age) {}
        
        Stream<Person> people = Stream.of(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 35)
        );
        
        Optional<Person> oldest = StreamX.maxBy(people, Person::age);
        
        assertThat(oldest).isPresent();
        assertThat(oldest.get().name()).isEqualTo("Charlie");
        assertThat(oldest.get().age()).isEqualTo(35);
    }
    
    @Test
    @DisplayName("minBy should find element with minimum selector value")
    void testMinBy() {
        Stream<String> words = Stream.of("elephant", "cat", "a", "dog");
        Optional<String> shortest = StreamX.minBy(words, String::length);
        
        assertThat(shortest).isPresent();
        assertThat(shortest.get()).isEqualTo("a");
    }
    
    @Test
    @DisplayName("sortedBy should sort by selector")
    void testSortedBy() {
        record Person(String name, int age) {}
        
        Stream<Person> people = Stream.of(
            new Person("Charlie", 35),
            new Person("Alice", 25),
            new Person("Bob", 30)
        );
        
        List<Person> sorted = StreamX.sortedBy(people, Person::age).toList();
        
        assertThat(sorted).hasSize(3);
        assertThat(sorted.get(0).name()).isEqualTo("Alice");
        assertThat(sorted.get(1).name()).isEqualTo("Bob");
        assertThat(sorted.get(2).name()).isEqualTo("Charlie");
    }
    
    @Test
    @DisplayName("sortedByDescending should sort in reverse order")
    void testSortedByDescending() {
        Stream<String> words = Stream.of("cat", "elephant", "a", "dog");
        List<String> sorted = StreamX.sortedByDescending(words, String::length).toList();
        
        assertThat(sorted).containsExactly("elephant", "cat", "dog", "a");
    }
    
    @Test
    @DisplayName("filterNotNull should remove null elements")
    void testFilterNotNull() {
        Stream<String> mixed = Stream.of("hello", null, "world", null, "!");
        List<String> filtered = StreamX.filterNotNull(mixed).toList();
        
        assertThat(filtered).containsExactly("hello", "world", "!");
    }
    
    @Test
    @DisplayName("filterIsInstance should filter by type")
    void testFilterIsInstance() {
        Stream<Object> mixed = Stream.of("hello", 42, "world", 3.14, true);
        List<String> strings = StreamX.filterIsInstance(mixed, String.class).toList();
        
        assertThat(strings).containsExactly("hello", "world");
    }
    
    @Test
    @DisplayName("firstOrNull should return first element or null")
    void testFirstOrNull() {
        String first = StreamX.firstOrNull(Stream.of("a", "b", "c"));
        assertThat(first).isEqualTo("a");
        
        String empty = StreamX.firstOrNull(Stream.empty());
        assertThat(empty).isNull();
    }
    
    @Test
    @DisplayName("lastOrNull should return last element or null")
    void testLastOrNull() {
        String last = StreamX.lastOrNull(Stream.of("a", "b", "c"));
        assertThat(last).isEqualTo("c");
        
        String empty = StreamX.lastOrNull(Stream.empty());
        assertThat(empty).isNull();
    }
    
    // ========== Advanced Stream Manipulation Tests ==========
    
    @Test
    @DisplayName("unzip should split pairs into two streams")
    void testUnzip() {
        Stream<Pair<String, Integer>> pairs = Stream.of(
            new Pair<>("Alice", 25),
            new Pair<>("Bob", 30),
            new Pair<>("Charlie", 35)
        );
        
        Pair<Stream<String>, Stream<Integer>> result = StreamX.unzip(pairs);
        
        assertThat(result.first().toList()).containsExactly("Alice", "Bob", "Charlie");
        assertThat(result.second().toList()).containsExactly(25, 30, 35);
    }
    
    @Test
    @DisplayName("pairwise should create consecutive pairs")
    void testPairwise() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
        List<Pair<Integer, Integer>> pairs = StreamX.pairwise(numbers).toList();
        
        assertThat(pairs).hasSize(4);
        assertThat(pairs.get(0).first()).isEqualTo(1);
        assertThat(pairs.get(0).second()).isEqualTo(2);
        assertThat(pairs.get(1).first()).isEqualTo(2);
        assertThat(pairs.get(1).second()).isEqualTo(3);
        assertThat(pairs.get(2).first()).isEqualTo(3);
        assertThat(pairs.get(2).second()).isEqualTo(4);
        assertThat(pairs.get(3).first()).isEqualTo(4);
        assertThat(pairs.get(3).second()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("pairwise on stream with less than 2 elements should be empty")
    void testPairwiseSmallStream() {
        List<Pair<Integer, Integer>> single = StreamX.pairwise(Stream.of(1)).toList();
        assertThat(single).isEmpty();
        
        List<Pair<Integer, Integer>> empty = StreamX.<Integer>pairwise(Stream.empty()).toList();
        assertThat(empty).isEmpty();
    }
    
    @Test
    @DisplayName("interpose should insert separator between elements")
    void testInterpose() {
        Stream<String> words = Stream.of("apple", "banana", "cherry");
        List<String> result = StreamX.interpose(words, ",").toList();
        
        assertThat(result).containsExactly("apple", ",", "banana", ",", "cherry");
    }
    
    @Test
    @DisplayName("interpose on single element should not add separator")
    void testInterposeSingle() {
        Stream<String> single = Stream.of("alone");
        List<String> result = StreamX.interpose(single, ",").toList();
        
        assertThat(result).containsExactly("alone");
    }
    
    @Test
    @DisplayName("transpose should flip 2D structure")
    void testTranspose() {
        Stream<List<Integer>> matrix = Stream.of(
            List.of(1, 2, 3),
            List.of(4, 5, 6),
            List.of(7, 8, 9)
        );
        
        List<List<Integer>> transposed = StreamX.transpose(matrix).toList();
        
        assertThat(transposed).hasSize(3);
        assertThat(transposed.get(0)).containsExactly(1, 4, 7);
        assertThat(transposed.get(1)).containsExactly(2, 5, 8);
        assertThat(transposed.get(2)).containsExactly(3, 6, 9);
    }
    
    @Test
    @DisplayName("transpose should handle jagged arrays")
    void testTransposeJagged() {
        Stream<List<Integer>> jagged = Stream.of(
            List.of(1, 2),
            List.of(3, 4, 5, 6),
            List.of(7)
        );
        
        List<List<Integer>> transposed = StreamX.transpose(jagged).toList();
        
        assertThat(transposed).hasSize(4);
        assertThat(transposed.get(0)).containsExactly(1, 3, 7);
        assertThat(transposed.get(1)).containsExactly(2, 4);
        assertThat(transposed.get(2)).containsExactly(5);
        assertThat(transposed.get(3)).containsExactly(6);
    }
    
    // ========== Enhanced Side Effects Tests ==========
    
    @Test
    @DisplayName("onEach should perform action and return stream")
    void testOnEach() {
        List<String> sideEffects = new ArrayList<>();
        
        List<Integer> result = StreamX.onEach(Stream.of(1, 2, 3),
            n -> sideEffects.add("processed: " + n))
            .toList();
        
        assertThat(result).containsExactly(1, 2, 3);
        assertThat(sideEffects).containsExactly("processed: 1", "processed: 2", "processed: 3");
    }
    
    @Test
    @DisplayName("let should transform stream with function")
    void testLet() {
        Integer result = StreamX.let(Stream.of(1, 2, 3, 4, 5),
            stream -> stream.mapToInt(Integer::intValue).sum());
        
        assertThat(result).isEqualTo(15);
    }
    
    // ========== Mathematical Operations Tests ==========
    
    @Test
    @DisplayName("cumSum should calculate cumulative sum")
    void testCumSum() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
        List<Integer> result = StreamX.cumSum(numbers).toList();
        
        assertThat(result).containsExactly(1, 3, 6, 10, 15);
    }
    
    @Test
    @DisplayName("cumProduct should calculate cumulative product")
    void testCumProduct() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4);
        List<Integer> result = StreamX.cumProduct(numbers).toList();
        
        assertThat(result).containsExactly(1, 2, 6, 24);
    }
    
    @Test
    @DisplayName("diff should calculate differences between consecutive elements")
    void testDiff() {
        Stream<Integer> numbers = Stream.of(1, 3, 6, 10, 15);
        List<Double> result = StreamX.diff(numbers).toList();
        
        assertThat(result).containsExactly(2.0, 3.0, 4.0, 5.0);
    }
    
    @Test
    @DisplayName("frequencies should count element occurrences")
    void testFrequencies() {
        Stream<String> words = Stream.of("apple", "banana", "apple", "cherry", "banana", "apple");
        Map<String, Long> result = StreamX.frequencies(words);
        
        assertThat(result).containsOnly(
            entry("apple", 3L),
            entry("banana", 2L),
            entry("cherry", 1L)
        );
    }
    
    // ========== Advanced Zipping Tests ==========
    
    @Test
    @DisplayName("zipAll should zip with Optionals for different length streams")
    void testZipAll() {
        Stream<String> shortStream = Stream.of("a", "b");
        Stream<Integer> longStream = Stream.of(1, 2, 3, 4);
        
        List<Pair<Optional<String>, Optional<Integer>>> result = StreamX.zipAll(shortStream, longStream).toList();
        
        assertThat(result).hasSize(4);
        assertThat(result.get(0)).isEqualTo(new Pair<>(Optional.of("a"), Optional.of(1)));
        assertThat(result.get(1)).isEqualTo(new Pair<>(Optional.of("b"), Optional.of(2)));
        assertThat(result.get(2)).isEqualTo(new Pair<>(Optional.empty(), Optional.of(3)));
        assertThat(result.get(3)).isEqualTo(new Pair<>(Optional.empty(), Optional.of(4)));
    }
    
    @Test
    @DisplayName("zipLongest should zip with default values")
    void testZipLongest() {
        Stream<String> shortStream = Stream.of("a", "b");
        Stream<Integer> longStream = Stream.of(1, 2, 3, 4);
        
        List<Pair<String, Integer>> result = StreamX.zipLongest(shortStream, longStream, "default", -1).toList();
        
        assertThat(result).hasSize(4);
        assertThat(result.get(0)).isEqualTo(new Pair<>("a", 1));
        assertThat(result.get(1)).isEqualTo(new Pair<>("b", 2));
        assertThat(result.get(2)).isEqualTo(new Pair<>("default", 3));
        assertThat(result.get(3)).isEqualTo(new Pair<>("default", 4));
    }
    
    // ========== String Operations Tests ==========
    
    @Test
    @DisplayName("joinToString should format with prefix, suffix, and transform")
    void testJoinToStringFull() {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
        String result = StreamX.joinToString(numbers, ", ", "[", "]", n -> "num:" + n);
        
        assertThat(result).isEqualTo("[num:1, num:2, num:3, num:4, num:5]");
    }
    
    @Test
    @DisplayName("joinToString with separator only")
    void testJoinToStringSeparator() {
        Stream<String> words = Stream.of("apple", "banana", "cherry");
        String result = StreamX.joinToString(words, " | ");
        
        assertThat(result).isEqualTo("apple | banana | cherry");
    }
    
    @Test
    @DisplayName("joinToString with default comma separator")
    void testJoinToStringDefault() {
        Stream<String> words = Stream.of("a", "b", "c");
        String result = StreamX.joinToString(words);
        
        assertThat(result).isEqualTo("a, b, c");
    }
}
