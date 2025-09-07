# StreamX - Enhanced Java Stream Utilities

**StreamX** is a comprehensive utility library that addresses the most common pain points and missing features in Java Streams. Inspired by functional programming languages like **Kotlin**, **Scala**, **Haskell**, **F#**, and **Clojure**, StreamX brings their powerful stream operations to Java while maintaining type safety and performance.

## Why StreamX?

Java Streams are powerful, but they're missing many operations that developers from other functional programming backgrounds expect. StreamX fills these gaps by implementing features from:

- **Kotlin**: Enhanced selectors, safe operations, string formatting  
- **Scala**: Scan operations, advanced partitioning, unzip functionality
- **Haskell**: Infinite sequences, transpose, span operations
- **F#**: Mathematical operations, windowing, pipelining
- **Clojure**: Partition-by, interpose, frequency operations

### Core Features:
- **Indexed Operations**: Access element indices without external counters
- **Scan Operations**: Running folds with all intermediate results  
- **Advanced Partitioning**: Split streams in sophisticated ways
- **Sequence Generation**: Create infinite streams and cycles
- **Enhanced Selectors**: Find elements by custom criteria
- **Advanced Zipping**: Combine multiple streams with different strategies
- **Windowing Operations**: Process data in sliding windows or chunks
- **Enhanced Collectors**: More powerful ways to collect results
- **Safe Operations**: Graceful error handling within streams
- **Mathematical Operations**: Built-in statistics and aggregations
- **String Operations**: Rich formatting and joining capabilities
- **Debugging Tools**: Better observability into stream processing

## Features

## **Scan Operations** (Kotlin/Scala/Haskell inspired)

One of the most requested missing features from Java Streams - the ability to see all intermediate results of a reduction operation.

**Before (Standard Java):**
```java
// No direct way to get intermediate results of reduction
// Must use external collection or complex custom collector
List<Integer> numbers = List.of(1, 2, 3, 4, 5);
List<Integer> runningSum = new ArrayList<>();
int sum = 0;
for (int num : numbers) {
    sum += num;
    runningSum.add(sum);  // [1, 3, 6, 10, 15]
}
```

**After (StreamX):**
```java
// Elegant scan operations
List<Integer> runningSum = StreamX.scan(numbers.stream(), 0, Integer::sum).toList();
// Result: [0, 1, 3, 6, 10, 15] - includes initial value

// Or without initial value in output
List<Integer> runningReduce = StreamX.runningReduce(numbers.stream(), Integer::sum).toList();
// Result: [1, 3, 6, 10, 15]
```

## **Advanced Partitioning** (Kotlin/Scala/Clojure inspired)

Split streams in sophisticated ways beyond simple filtering.

**Before (Standard Java):**
```java
// Partition into two groups - requires two passes or complex collector
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
List<Integer> evens = numbers.stream().filter(n -> n % 2 == 0).toList();
List<Integer> odds = numbers.stream().filter(n -> n % 2 != 0).toList();

// Splitting at first non-matching element is very complex
List<Integer> ascending = List.of(1, 3, 5, 2, 4, 6, 7);
// Complex logic needed to split at first non-odd number
```

**After (StreamX):**
```java
// Single pass partitioning
Pair<List<Integer>, List<Integer>> evenOdd = StreamX.partition(numbers.stream(), n -> n % 2 == 0);
List<Integer> evens = evenOdd.first();   // [2, 4, 6, 8, 10]
List<Integer> odds = evenOdd.second();   // [1, 3, 5, 7, 9]

// Span - split at first non-matching element  
Pair<List<Integer>, List<Integer>> spans = StreamX.span(ascending.stream(), n -> n % 2 == 1);
// First: [1, 3, 5], Second: [2, 4, 6, 7]

// Partition by consecutive groups
List<List<Integer>> groups = StreamX.partitionBy(Stream.of(1,1,2,2,2,1,3,3), n -> n).toList();
// Result: [[1,1], [2,2,2], [1], [3,3]]
```

## **Sequence Generation** (Haskell/Scala inspired)

Create infinite streams and sequences programmatically.

**Before (Standard Java):**
```java
// Creating repetitive sequences is verbose
List<String> hellos = Collections.nCopies(5, "hello");

// Infinite sequences require Stream.generate with external state
// Cycling through values is complex
List<String> cycle = new ArrayList<>();
String[] values = {"a", "b", "c"};
for (int i = 0; i < 10; i++) {
    cycle.add(values[i % values.length]);
}
```

**After (StreamX):**
```java
// Clean sequence generation
List<String> hellos = StreamX.replicate(5, "hello").toList();

// Infinite cycle - inspired by Haskell
List<String> cycled = StreamX.cycle("a", "b", "c").limit(10).toList();
// Result: ["a", "b", "c", "a", "b", "c", "a", "b", "c", "a"]

// Range with custom step - like Kotlin ranges
List<Integer> stepped = StreamX.range(0, 10, 2).toList();  // [0, 2, 4, 6, 8]

// Infinite repeat
List<String> repeated = StreamX.repeat("hello").limit(3).toList();  // ["hello", "hello", "hello"]
```

## **Enhanced Selectors** (Kotlin inspired)

Find and sort elements by custom criteria more elegantly.

**Before (Standard Java):**
```java
// Finding max/min by custom criteria
Optional<Person> oldest = people.stream()
    .max(Comparator.comparing(Person::getAge));

// Sorting by custom criteria
List<Person> sortedByAge = people.stream()
    .sorted(Comparator.comparing(Person::getAge))
    .toList();

// Filtering by type requires instanceof checks
List<String> strings = objects.stream()
    .filter(obj -> obj instanceof String)
    .map(obj -> (String) obj)
    .toList();
```

**After (StreamX):**
```java
// Clean selector operations - inspired by Kotlin
Optional<Person> oldest = StreamX.maxBy(people.stream(), Person::getAge);
Optional<Person> youngest = StreamX.minBy(people.stream(), Person::getAge);

// Sorting variants
List<Person> sortedByAge = StreamX.sortedBy(people.stream(), Person::getAge).toList();
List<Person> sortedByAgeDesc = StreamX.sortedByDescending(people.stream(), Person::getAge).toList();

// Type-safe filtering
List<String> strings = StreamX.filterIsInstance(objects.stream(), String.class).toList();

// Null-safe operations
List<String> nonNulls = StreamX.filterNotNull(strings.stream()).toList();

// Safe element access
String first = StreamX.firstOrNull(stream);  // null if empty
String last = StreamX.lastOrNull(stream);   // null if empty
```

## **Advanced Stream Manipulation** (Scala/Haskell inspired)

Sophisticated stream transformations from functional programming languages.

**Before (Standard Java):**
```java
// Unzipping pairs requires manual iteration
List<Pair<String, Integer>> pairs = getPairs();
List<String> names = new ArrayList<>();
List<Integer> ages = new ArrayList<>();
for (Pair<String, Integer> pair : pairs) {
    names.add(pair.getFirst());
    ages.add(pair.getSecond());
}

// Consecutive pairs require complex indexing
List<Integer> numbers = List.of(1, 2, 3, 4, 5);
List<Pair<Integer, Integer>> pairs = new ArrayList<>();
for (int i = 0; i < numbers.size() - 1; i++) {
    pairs.add(new Pair<>(numbers.get(i), numbers.get(i + 1)));
}
```

**After (StreamX):**
```java
// Unzip - inspired by Scala
Pair<Stream<String>, Stream<Integer>> unzipped = StreamX.unzip(pairs.stream());
List<String> names = unzipped.first().toList();
List<Integer> ages = unzipped.second().toList();

// Pairwise consecutive elements
List<Pair<Integer, Integer>> consecutive = StreamX.pairwise(numbers.stream()).toList();
// Result: [(1,2), (2,3), (3,4), (4,5)]

// Interpose - insert separators (Clojure inspired)
List<String> withCommas = StreamX.interpose(words.stream(), ",").toList();
// ["apple", ",", "banana", ",", "cherry"]

// Transpose - flip 2D structure (Haskell inspired)
Stream<List<Integer>> matrix = Stream.of(List.of(1,2,3), List.of(4,5,6), List.of(7,8,9));
List<List<Integer>> transposed = StreamX.transpose(matrix).toList();
// Result: [[1,4,7], [2,5,8], [3,6,9]]
```

## **Mathematical Operations** (Various FP languages inspired)

Rich mathematical operations missing from Java Streams.

**Before (Standard Java):**
```java
// Cumulative operations require manual state management
List<Integer> numbers = List.of(1, 2, 3, 4, 5);
List<Integer> cumSum = new ArrayList<>();
int sum = 0;
for (int num : numbers) {
    sum += num;
    cumSum.add(sum);
}

// Differences between consecutive elements
List<Double> diffs = new ArrayList<>();
for (int i = 1; i < numbers.size(); i++) {
    diffs.add(numbers.get(i).doubleValue() - numbers.get(i-1).doubleValue());
}

// Frequency counting requires groupingBy
Map<String, Long> freq = words.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
```

**After (StreamX):**
```java
// Cumulative operations
List<Integer> cumSum = StreamX.cumSum(numbers.stream()).toList();        // [1, 3, 6, 10, 15]
List<Integer> cumProduct = StreamX.cumProduct(numbers.stream()).toList(); // [1, 2, 6, 24, 120]

// Differences between consecutive elements
List<Double> diffs = StreamX.diff(numbers.stream()).toList();  // [1.0, 1.0, 1.0, 1.0]

// Frequency counting - inspired by Python Counter
Map<String, Long> freq = StreamX.frequencies(words.stream());
```

## 🔗 **Advanced Zipping** (Scala inspired)

Handle streams of different lengths gracefully.

**Before (Standard Java):**
```java
// Standard zip stops at shortest stream
// No built-in way to handle different length streams
Iterator<String> names = nameStream.iterator();
Iterator<Integer> ages = ageStream.iterator();
List<Pair<String, Integer>> pairs = new ArrayList<>();

// If streams have different lengths, no easy way to continue with defaults
while (names.hasNext() && ages.hasNext()) {
    pairs.add(new Pair<>(names.next(), ages.next()));
}
// Remaining elements in longer stream are lost
```

**After (StreamX):**
```java
// Zip all with Optionals - handles different lengths
List<Pair<Optional<String>, Optional<Integer>>> allPairs = 
    StreamX.zipAll(names, ages).toList();
// Continues until both streams are exhausted, using Optional.empty() for missing values

// Zip with default values
List<Pair<String, Integer>> withDefaults = 
    StreamX.zipLongest(names, ages, "Unknown", -1).toList();
// Uses "Unknown" for missing names, -1 for missing ages
```

## **String Operations** (Kotlin inspired)

Rich string formatting and processing.

**Before (Standard Java):**
```java
// Joining with custom formatting is verbose
String result = stream.map(item -> "Item: " + item)
    .collect(Collectors.joining(", ", "[", "]"));

// Limited joining options
String simple = stream.map(Object::toString)
    .collect(Collectors.joining(", "));
```

**After (StreamX):**
```java
// Rich joining options - inspired by Kotlin's joinToString
String formatted = StreamX.joinToString(items.stream(), 
    ", ",           // separator
    "[",            // prefix  
    "]",            // suffix
    item -> "Item: " + item  // transform function
);

// Simple variants
String withSeparator = StreamX.joinToString(items.stream(), " | ");
String withCommas = StreamX.joinToString(items.stream());  // default comma separation
```

### 1. Indexed Operations
**Before (Standard Java):**
```java
// Awkward external counter approach
AtomicInteger counter = new AtomicInteger(0);
List<String> result = stream
    .map(value -> counter.getAndIncrement() + ": " + value)
    .collect(toList());

// Or complex workaround with IntStream
List<String> result = IntStream.range(0, list.size())
    .mapToObj(i -> i + ": " + list.get(i))
    .collect(toList());
```

**After (StreamX):**
```java
// Clean and intuitive
List<String> result = StreamX.withIndex(stream, (value, index) -> index + ": " + value)
    .collect(toList());

// Or get IndexedValue objects
List<IndexedValue<String>> indexed = StreamX.zipWithIndex(stream)
    .collect(toList());
```

### 2. Stream Zipping
**Before (Standard Java):**
```java
// No built-in way to zip streams - requires complex Iterator logic
Iterator<String> names = nameStream.iterator();
Iterator<Integer> ages = ageStream.iterator();
List<Person> people = new ArrayList<>();
while (names.hasNext() && ages.hasNext()) {
    people.add(new Person(names.next(), ages.next()));
}
```

**After (StreamX):**
```java
// Elegant and functional
List<Person> people = StreamX.zip(nameStream, ageStream, Person::new)
    .collect(toList());

// Three streams? No problem!
List<String> result = StreamX.zip(names, ages, cities, 
    (name, age, city) -> name + " (" + age + ") from " + city)
    .collect(toList());
```

### 3. Windowing Operations
**Before (Standard Java):**
```java
// Complex chunking logic
List<T> source = stream.collect(toList());
List<List<T>> chunks = new ArrayList<>();
for (int i = 0; i < source.size(); i += chunkSize) {
    chunks.add(source.subList(i, Math.min(i + chunkSize, source.size())));
}

// Sliding windows are even more complex...
List<List<T>> windows = new ArrayList<>();
for (int i = 0; i <= source.size() - windowSize; i++) {
    windows.add(source.subList(i, i + windowSize));
}
```

**After (StreamX):**
```java
// Simple and readable
List<List<Integer>> chunks = StreamX.chunked(numbers, 3).collect(toList());
// [[1,2,3], [4,5,6], [7,8,9]]

List<List<Integer>> windows = StreamX.windowed(numbers, 3).collect(toList());
// [[1,2,3], [2,3,4], [3,4,5], [4,5,6]]

List<List<Integer>> sliding = StreamX.sliding(numbers, 3, 2).collect(toList());
// [[1,2,3], [3,4,5], [5,6,7]] - window size 3, step 2
```

### 4. Distinct by Custom Key
**Before (Standard Java):**
```java
// Awkward workaround with external Set
Set<String> seen = new HashSet<>();
List<Person> unique = people.stream()
    .filter(person -> seen.add(person.getName()))
    .collect(toList());
```

**After (StreamX):**
```java
// Clean and expressive
List<Person> unique = StreamX.distinctBy(people.stream(), Person::getName)
    .collect(toList());
```

### 5. Enhanced Collectors
**Before (Standard Java):**
```java
// Top N requires complex sorting and limiting
List<Integer> top3 = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .limit(3)
    .collect(toList()); // But this sorts ALL elements!

// LinkedHashMap requires verbose collector
LinkedHashMap<String, Integer> ordered = people.stream()
    .collect(toMap(Person::getName, Person::getAge, 
             (e1, e2) -> e1, LinkedHashMap::new));

// MultiMap is very verbose
Map<String, List<String>> multiMap = people.stream()
    .collect(groupingBy(Person::getDepartment, 
             mapping(Person::getName, toList())));
```

**After (StreamX):**
```java
// Efficient top N without full sorting
List<Integer> top3 = numbers.stream()
    .collect(StreamXCollectors.topN(3));

// Simple LinkedHashMap
LinkedHashMap<String, Integer> ordered = people.stream()
    .collect(StreamXCollectors.toLinkedMap(Person::getName, Person::getAge));

// Clean MultiMap
Map<String, List<String>> multiMap = people.stream()
    .collect(StreamXCollectors.toMultiMap(Person::getDepartment, Person::getName));

// Bonus: Random sampling
List<Item> sample = items.stream()
    .collect(StreamXCollectors.sampling(100));
```

### 6. Safe Operations with Error Handling
**Before (Standard Java):**
```java
// Exception handling breaks the stream flow
List<Integer> parsed = strings.stream()
    .map(s -> {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1; // Default value scattered in logic
        }
    })
    .collect(toList());

// Filtering with exceptions is even messier
List<String> valid = strings.stream()
    .filter(s -> {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    })
    .collect(toList());
```

**After (StreamX):**
```java
// Clean error handling with defaults
List<Integer> parsed = StreamX.mapSafely(strings.stream(), 
    Integer::parseInt, -1)
    .collect(toList());

// Safe filtering - skip invalid elements
List<String> valid = StreamX.filterSafely(strings.stream(),
    s -> Integer.parseInt(s) > 0)
    .collect(toList());
```

### 7. Mathematical Operations
**Before (Standard Java):**
```java
// Statistics require multiple passes or complex collectors
DoubleSummaryStatistics stats = numbers.stream()
    .mapToDouble(Integer::doubleValue)
    .summaryStatistics(); // Limited statistics

// Median requires manual sorting
List<Integer> sorted = numbers.stream().sorted().collect(toList());
double median = sorted.size() % 2 == 0 
    ? (sorted.get(sorted.size()/2-1) + sorted.get(sorted.size()/2)) / 2.0
    : sorted.get(sorted.size()/2);

// Mode is complex
Map<Integer, Long> frequency = numbers.stream()
    .collect(groupingBy(identity(), counting()));
Integer mode = frequency.entrySet().stream()
    .max(Map.Entry.comparingByValue())
    .map(Map.Entry::getKey)
    .orElse(null);
```

**After (StreamX):**
```java
// Comprehensive statistics in one call
StreamStatistics stats = StreamX.statistics(numbers.stream());
// stats.getStandardDeviation(), .getVariance(), etc.

// Simple median
OptionalDouble median = StreamX.median(numbers.stream());

// Easy mode
Optional<Integer> mode = StreamX.mode(numbers.stream());
```

### 8. Async Processing
**Before (Standard Java):**
```java
// Manual CompletableFuture handling
List<CompletableFuture<Result>> futures = items.stream()
    .map(item -> CompletableFuture.supplyAsync(() -> process(item)))
    .collect(toList());

List<Result> results = futures.stream()
    .map(CompletableFuture::join)
    .collect(toList());
```

**After (StreamX):**
```java
// Streamlined async processing
List<Result> results = StreamX.awaitAll(
    StreamX.asyncMap(items.stream(), this::process)
);

// With custom thread pool
List<Result> results = StreamX.awaitAll(
    StreamX.asyncMap(items.stream(), this::process, customExecutor)
);
```

### 9. Stream Combination
**Before (Standard Java):**
```java
// Interleaving streams requires Iterator juggling
Iterator<String> iter1 = stream1.iterator();
Iterator<String> iter2 = stream2.iterator();
List<String> interleaved = new ArrayList<>();
while (iter1.hasNext() || iter2.hasNext()) {
    if (iter1.hasNext()) interleaved.add(iter1.next());
    if (iter2.hasNext()) interleaved.add(iter2.next());
}

// Union with deduplication
Set<String> union = Stream.concat(stream1, stream2)
    .collect(toSet());

// Intersection requires Set conversion
Set<String> set2 = stream2.collect(toSet());
List<String> intersection = stream1
    .filter(set2::contains)
    .collect(toList());
```

**After (StreamX):**
```java
// Clean interleaving
List<String> interleaved = StreamX.interleave(stream1, stream2)
    .collect(toList());

// Simple union
List<String> union = StreamX.union(stream1, stream2, stream3)
    .collect(toList());

// Easy intersection
List<String> intersection = StreamX.intersect(stream1, stream2)
    .collect(toList());
```

### 10. Debugging Support
**Before (Standard Java):**
```java
// Debugging requires breaking the stream flow
List<String> result = items.stream()
    .peek(item -> System.out.println("Processing: " + item)) // Limited
    .map(this::transform)
    .peek(item -> System.out.println("Transformed: " + item)) // Cluttered
    .filter(this::isValid)
    .collect(toList());
```

**After (StreamX):**
```java
// Clean debugging with context
Stream<String> input = StreamX.debug(items.stream(), "Input");
Stream<String> transformed = input.map(this::transform);
List<String> result = StreamX.debug(transformed, "After transform")
    .filter(this::isValid)
    .collect(toList());

// Or use standard peek() for side effects
List<String> result = items.stream()
    .peek(this::logProcessing)
    .map(this::transform)
    .collect(toList());
```

## Real-World Example

Here's a complete example showing StreamX in action:

**Traditional Java Streams:**
```java
// Process customer orders - the hard way
List<Customer> customers = getCustomers();
List<OrderSummary> summaries = new ArrayList<>();

// Group by region manually
Map<String, List<Customer>> byRegion = new HashMap<>();
for (Customer customer : customers) {
    byRegion.computeIfAbsent(customer.getRegion(), k -> new ArrayList<>()).add(customer);
}

// Process each region
for (Map.Entry<String, List<Customer>> entry : byRegion.entrySet()) {
    String region = entry.getKey();
    List<Customer> regionCustomers = entry.getValue();
    
    // Calculate statistics manually
    double totalAmount = 0;
    int count = 0;
    for (Customer customer : regionCustomers) {
        for (Order order : customer.getOrders()) {
            totalAmount += order.getAmount();
            count++;
        }
    }
    
    summaries.add(new OrderSummary(region, totalAmount, count, totalAmount / count));
}

// Sort by total amount
summaries.sort((a, b) -> Double.compare(b.getTotalAmount(), a.getTotalAmount()));
```

**StreamX Approach:**
```java
// Process customer orders - the StreamX way
Stream<Customer> debuggedCustomers = StreamX.debug(StreamX.of(customers), "Processing customers");
Stream<RegionOrder> regionOrders = debuggedCustomers
    .flatMap(customer -> customer.getOrders().stream()
        .map(order -> new RegionOrder(customer.getRegion(), order.getAmount())));

Stream<RegionOrder> distinctRegions = StreamX.distinctBy(regionOrders, RegionOrder::getRegion);
Map<String, List<RegionOrder>> groupedByRegion = distinctRegions.collect(groupingBy(RegionOrder::getRegion));

List<OrderSummary> summaries = groupedByRegion.entrySet().stream()
    .map(entry -> {
        String region = entry.getKey();
        StreamStatistics stats = StreamX.statistics(
            entry.getValue().stream().map(RegionOrder::getAmount)
        );
        return new OrderSummary(region, stats.getSum(), 
                              stats.getCount(), stats.getAverage());
    })
    .collect(StreamXCollectors.topN(10, comparing(OrderSummary::getTotalAmount)));
```

## Running Tests

StreamX comes with comprehensive unit tests demonstrating all features:

```bash
mvn test
```

The test suite includes:
- **35 test cases** covering all major features
- **Edge case handling** (empty streams, invalid inputs, etc.)
- **Performance considerations** (parallel processing, memory efficiency)
- **Error handling scenarios** (safe operations, exception recovery)

## Key Benefits

1. **Reduced Boilerplate**: Common stream operations become one-liners
2. **Better Readability**: Code expresses intent more clearly
3. **Type Safety**: Full generic type support with compile-time checking
4. **Performance**: Optimized implementations (e.g., topN doesn't sort all elements)
5. **Composability**: All operations work seamlessly together
6. **Error Resilience**: Graceful handling of edge cases and exceptions

## Integration

StreamX is designed to work alongside standard Java Streams, not replace them. You can mix and match:

```java
// Standard streams + StreamX enhancements
Stream<Data> filtered = data.stream().filter(standardFilter);
Stream<List<Data>> chunked = StreamX.chunked(filtered, 100);
Stream<Data> flattened = chunked.flatMap(List::stream);
Stream<Result> mapped = flattened.map(standardMapper);
List<Result> results = StreamX.distinctBy(mapped, Result::getKey)
    .collect(toList());
```

## **Test Coverage & Reliability**

StreamX is thoroughly tested with **76 comprehensive unit tests**:
- **23 tests** for core StreamX features  
- **41 tests** for functional programming features
- **12 tests** for enhanced collectors
- **100% pass rate** with edge case handling

## **Functional Programming Language Influences**

| Feature | Inspired By | Description |
|---------|-------------|-------------|
| `scan`, `runningReduce` | **Kotlin, Scala, Haskell** | Running folds with intermediate results |
| `partition`, `span` | **Kotlin, Scala, Haskell** | Advanced stream splitting |
| `partitionBy` | **Clojure** | Group consecutive elements by classifier |
| `maxBy`, `minBy`, `sortedBy` | **Kotlin** | Enhanced element selection |
| `filterNotNull`, `filterIsInstance` | **Kotlin** | Type-safe filtering |
| `firstOrNull`, `lastOrNull` | **Kotlin** | Safe element access |
| `cycle`, `repeat`, `replicate` | **Haskell, Clojure** | Infinite sequence generation |
| `range` with step | **Kotlin** | Flexible number sequences |
| `unzip` | **Scala** | Split paired streams |
| `pairwise` | **Various FP languages** | Consecutive element pairs |
| `interpose` | **Clojure** | Insert separators |
| `transpose` | **Haskell** | Matrix transposition |
| `frequencies` | **Clojure, Python** | Element frequency counting |
| `cumSum`, `cumProduct`, `diff` | **NumPy, R, scientific libraries** | Mathematical operations |
| `zipAll`, `zipLongest` | **Scala** | Advanced stream zipping |
| `joinToString` | **Kotlin** | Rich string formatting |

## **Performance Notes**

StreamX operations are designed with performance in mind:

- **Lazy Evaluation**: Operations are as lazy as standard streams
- **Memory Efficient**: Minimal intermediate collections  
- **Parallel Friendly**: Most operations support parallel streams
- **Optimized Algorithms**: E.g., topN uses heap for O(n log k) complexity
- **Zero External Dependencies**: Pure Java implementation

## **Design Philosophy**

StreamX follows these principles:

1. **Familiarity**: If you know Kotlin, Scala, or Haskell, you'll recognize these operations
2. **Type Safety**: Full generic type support with compile-time checking
3. **Composability**: All operations work seamlessly together
4. **Performance**: Don't sacrifice performance for convenience
5. **Java Idioms**: Feels natural to Java developers while adding FP power

## **Contributing**

This library represents a Java champion's vision of what streams could be, inspired by the best of functional programming languages. Contributions welcome for:

- Additional FP operations from other languages (F#, Clojure, etc.)
- Performance optimizations  
- More comprehensive error handling
- Extended mathematical functions

## **What Java Streams Should Have Been**

StreamX demonstrates what Java Streams could have been with inspiration from the rich ecosystem of functional programming languages. Every feature addresses real-world pain points that developers face when switching from languages like Kotlin, Scala, or Haskell to Java.

---

**StreamX**: Bringing the power of functional programming to Java Streams!
