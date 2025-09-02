package io.streamx;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StreamXCollectors Tests")
class StreamXCollectorsTest {
    
    @Test
    @DisplayName("toLinkedMap should preserve insertion order")
    void testToLinkedMap() {
        record Person(String name, int age) {}
        
        Stream<Person> people = Stream.of(
            new Person("Charlie", 35),
            new Person("Alice", 25),
            new Person("Bob", 30)
        );
        
        LinkedHashMap<String, Integer> result = people.collect(
            StreamXCollectors.toLinkedMap(Person::name, Person::age)
        );
        
        assertThat(result).containsExactly(
            entry("Charlie", 35),
            entry("Alice", 25),
            entry("Bob", 30)
        );
        
        // Verify it's actually a LinkedHashMap and maintains order
        assertThat(result.keySet()).containsExactly("Charlie", "Alice", "Bob");
    }
    
    @Test
    @DisplayName("toLinkedMap with merge function should handle duplicates")
    void testToLinkedMapWithMerge() {
        record Sale(String product, int amount) {}
        
        Stream<Sale> sales = Stream.of(
            new Sale("Apple", 100),
            new Sale("Banana", 50),
            new Sale("Apple", 75),
            new Sale("Cherry", 25)
        );
        
        LinkedHashMap<String, Integer> result = sales.collect(
            StreamXCollectors.toLinkedMap(Sale::product, Sale::amount, Integer::sum)
        );
        
        assertThat(result).containsExactly(
            entry("Apple", 175),    // 100 + 75
            entry("Banana", 50),
            entry("Cherry", 25)
        );
    }
    
    @Test
    @DisplayName("toMultiMap should group multiple values per key")
    void testToMultiMap() {
        record Person(String department, String name) {}
        
        Stream<Person> people = Stream.of(
            new Person("Engineering", "Alice"),
            new Person("Sales", "Bob"),
            new Person("Engineering", "Charlie"),
            new Person("Sales", "Diana"),
            new Person("Engineering", "Eve")
        );
        
        Map<String, List<String>> result = people.collect(
            StreamXCollectors.toMultiMap(Person::department, Person::name)
        );
        
        assertThat(result).hasSize(2);
        assertThat(result.get("Engineering")).containsExactly("Alice", "Charlie", "Eve");
        assertThat(result.get("Sales")).containsExactly("Bob", "Diana");
    }
    
    @Test
    @DisplayName("topN should collect top N elements")
    void testTopN() {
        Stream<Integer> numbers = Stream.of(5, 2, 8, 1, 9, 3, 7, 4, 6);
        List<Integer> top3 = numbers.collect(StreamXCollectors.topN(3));
        
        assertThat(top3).containsExactly(9, 8, 7);
    }
    
    @Test
    @DisplayName("topN with custom comparator")
    void testTopNWithComparator() {
        record Person(String name, int age) {}
        
        Stream<Person> people = Stream.of(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 35),
            new Person("Diana", 20),
            new Person("Eve", 40)
        );
        
        List<Person> oldest2 = people.collect(
            StreamXCollectors.topN(2, Comparator.comparing(Person::age))
        );
        
        assertThat(oldest2).hasSize(2);
        assertThat(oldest2.get(0).name()).isEqualTo("Eve");
        assertThat(oldest2.get(0).age()).isEqualTo(40);
        assertThat(oldest2.get(1).name()).isEqualTo("Charlie");
        assertThat(oldest2.get(1).age()).isEqualTo(35);
    }
    
    @Test
    @DisplayName("histogram should count frequencies")
    void testHistogram() {
        Stream<String> words = Stream.of("apple", "banana", "apple", "cherry", "banana", "apple");
        Map<String, Long> frequencies = words.collect(StreamXCollectors.histogram(w -> w));
        
        assertThat(frequencies).containsOnly(
            entry("apple", 3L),
            entry("banana", 2L),
            entry("cherry", 1L)
        );
    }
    
    @Test
    @DisplayName("histogram with classifier should group and count")
    void testHistogramWithClassifier() {
        Stream<String> words = Stream.of("apple", "apricot", "banana", "blueberry", "cherry", "coconut");
        Map<Character, Long> firstLetterCount = words.collect(
            StreamXCollectors.histogram(word -> word.charAt(0))
        );
        
        assertThat(firstLetterCount).containsOnly(
            entry('a', 2L),
            entry('b', 2L),
            entry('c', 2L)
        );
    }
    
    @Test
    @DisplayName("sampling should randomly select elements")
    void testSampling() {
        // Create a predictable random sample
        Random fixedRandom = new Random(42); // Fixed seed for predictable results
        
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> sample = numbers.collect(StreamXCollectors.sampling(3, fixedRandom));
        
        assertThat(sample).hasSize(3);
        // All elements should be from the original stream
        assertThat(sample).allMatch(n -> n >= 1 && n <= 10);
    }
    
    @Test
    @DisplayName("sampling should handle stream smaller than sample size")
    void testSamplingSmallStream() {
        Stream<Integer> numbers = Stream.of(1, 2);
        List<Integer> sample = numbers.collect(StreamXCollectors.sampling(5));
        
        assertThat(sample).hasSize(2);
        assertThat(sample).containsExactlyInAnyOrder(1, 2);
    }
    
    @Test
    @DisplayName("partitioningBy should group by classifier")
    void testPartitioningBy() {
        record Person(String name, String department) {}
        
        Stream<Person> people = Stream.of(
            new Person("Alice", "Engineering"),
            new Person("Bob", "Sales"),
            new Person("Charlie", "Engineering"),
            new Person("Diana", "Marketing")
        );
        
        Map<String, List<Person>> byDepartment = people.collect(
            StreamXCollectors.partitioningBy(Person::department)
        );
        
        assertThat(byDepartment).hasSize(3);
        assertThat(byDepartment.get("Engineering")).hasSize(2);
        assertThat(byDepartment.get("Sales")).hasSize(1);
        assertThat(byDepartment.get("Marketing")).hasSize(1);
    }
    
    @Test
    @DisplayName("topN should handle empty stream")
    void testTopNEmptyStream() {
        Stream<Integer> empty = Stream.empty();
        List<Integer> result = empty.collect(StreamXCollectors.topN(3));
        
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("topN should handle stream smaller than N")
    void testTopNSmallStream() {
        Stream<Integer> numbers = Stream.of(5, 2);
        List<Integer> result = numbers.collect(StreamXCollectors.topN(5));
        
        assertThat(result).containsExactly(5, 2);
    }
}
