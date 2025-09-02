# StreamX Visual Documentation

This document provides visual representations of StreamX features using Mermaid diagrams to help you understand how each operation works.

## Table of Contents
- [Scan Operations](#scan-operations)
- [Partitioning Operations](#partitioning-operations)
- [Windowing Operations](#windowing-operations)
- [Zipping Operations](#zipping-operations)
- [Sequence Generation](#sequence-generation)
- [Enhanced Selectors](#enhanced-selectors)
- [Stream Transformations](#stream-transformations)
- [Mathematical Operations](#mathematical-operations)
- [Safe Operations](#safe-operations)
- [Async Processing](#async-processing)

---

## Scan Operations

**Scan operations** provide all intermediate results of a reduction, inspired by Kotlin, Scala, and Haskell.

### How `scan()` Works

```mermaid
flowchart LR
    A["[1, 2, 3, 4, 5]"] --> B["scan(0, Integer::sum)"]
    B --> C["[0, 1, 3, 6, 10, 15]"]
    
    subgraph "Step by Step Process"
        D["Initial: 0"] --> E["0 + 1 = 1"]
        E --> F["1 + 2 = 3"]  
        F --> G["3 + 3 = 6"]
        G --> H["6 + 4 = 10"]
        H --> I["10 + 5 = 15"]
    end
    
    style A fill:#e1f5fe
    style C fill:#c8e6c9
    style B fill:#fff3e0
```

**Key Benefits:**
- See all intermediate results, not just the final one
- Perfect for running totals, cumulative products, etc.
- More efficient than multiple reduce operations

---

## Partitioning Operations

**Partitioning operations** split streams in sophisticated ways beyond simple filtering.

### How `partition()` Works

```mermaid
flowchart TB
    A["[1, 2, 3, 4, 5, 6, 7, 8]"] --> B["partition(n -> n % 2 == 0)"]
    
    B --> C["Pair<br/>First: Even Numbers<br/>Second: Odd Numbers"]
    
    subgraph "Single Pass Processing"
        D["1 → Odd"] --> E["2 → Even"]
        E --> F["3 → Odd"]
        F --> G["4 → Even"]
        G --> H["5 → Odd"]
        H --> I["6 → Even"]
        I --> J["7 → Odd"]
        J --> K["8 → Even"]
    end
    
    C --> L["Even: [2, 4, 6, 8]"]
    C --> M["Odd: [1, 3, 5, 7]"]
    
    style A fill:#e1f5fe
    style C fill:#fff3e0
    style L fill:#c8e6c9
    style M fill:#ffcdd2
```

### How `span()` Works

```mermaid
flowchart TB
    A["[1, 3, 5, 2, 4, 6, 7]"] --> B["span(n -> n % 2 == 1)"]
    
    B --> C["Split at first non-matching element"]
    
    subgraph "Evaluation Process"
        D["1 → Odd ✓"] --> E["3 → Odd ✓"]
        E --> F["5 → Odd ✓"]
        F --> G["2 → Even ✗<br/><b>STOP HERE</b>"]
        G --> H["Remaining: [2, 4, 6, 7]"]
    end
    
    C --> I["First: [1, 3, 5]<br/><i>Elements while predicate was true</i>"]
    C --> J["Second: [2, 4, 6, 7]<br/><i>Remaining elements</i>"]
    
    style A fill:#e1f5fe
    style C fill:#fff3e0
    style I fill:#c8e6c9
    style J fill:#ffcdd2
    style G fill:#ffeb3b
```

**Key Differences:**
- `partition()` - Separates ALL elements by predicate (like filter + filterNot)
- `span()` - Takes elements UNTIL first non-match, then stops

---

## Windowing Operations

**Windowing operations** process data in sliding windows or fixed-size chunks.

### How `windowed()` Works

```mermaid
flowchart TB
    A["[1, 2, 3, 4, 5, 6, 7, 8, 9]"] --> B["windowed(3)"]
    
    subgraph "Sliding Window Process"
        C["Window 1: [1, 2, 3]"] --> D["Window 2: [2, 3, 4]"]
        D --> E["Window 3: [3, 4, 5]"]
        E --> F["Window 4: [4, 5, 6]"]
        F --> G["Window 5: [5, 6, 7]"]
        G --> H["Window 6: [6, 7, 8]"]
        H --> I["Window 7: [7, 8, 9]"]
    end
    
    B --> J["[[1,2,3], [2,3,4], [3,4,5], [4,5,6], [5,6,7], [6,7,8], [7,8,9]]"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style J fill:#c8e6c9
```

### How `chunked()` Works

```mermaid
flowchart TB
    A["[1, 2, 3, 4, 5, 6, 7, 8, 9]"] --> B["chunked(3)"]
    
    subgraph "Non-overlapping Chunks"
        C["Chunk 1: [1, 2, 3]"] --> D["Chunk 2: [4, 5, 6]"]
        D --> E["Chunk 3: [7, 8, 9]"]
    end
    
    B --> F["[[1, 2, 3], [4, 5, 6], [7, 8, 9]]"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style F fill:#c8e6c9
    
    subgraph "Key Difference"
        G["chunked() = Non-overlapping groups"]
        H["windowed() = Overlapping windows"]
    end
    
    style G fill:#ffeb3b
    style H fill:#ff9800
```

**Use Cases:**
- `windowed()` - Moving averages, trend analysis, pattern detection
- `chunked()` - Batch processing, pagination, data segmentation

---

## Zipping Operations

**Zipping operations** combine multiple streams element-by-element.

### How `zip()` Works

```mermaid
flowchart TB
    A["Stream 1: ['Alice', 'Bob', 'Charlie']"] --> C["zip()"]
    B["Stream 2: [25, 30, 35]"] --> C
    
    C --> D["Zip Process"]
    
    subgraph "Pairing Elements"
        E["Alice + 25 → Pair(Alice, 25)"]
        F["Bob + 30 → Pair(Bob, 30)"]
        G["Charlie + 35 → Pair(Charlie, 35)"]
    end
    
    D --> H["[(Alice, 25), (Bob, 30), (Charlie, 35)]"]
    
    style A fill:#e1f5fe
    style B fill:#e1f5fe
    style C fill:#fff3e0
    style H fill:#c8e6c9
```

### How `zipAll()` Works with Different Lengths

```mermaid
flowchart TB
    A["Stream 1: ['A', 'B', 'C', 'D']"] --> C["zipAll()"]
    B["Stream 2: [1, 2]"] --> C
    
    C --> D["Handle Different Lengths"]
    
    subgraph "Pairing with Optionals"
        E["A + 1 → (Optional[A], Optional[1])"]
        F["B + 2 → (Optional[B], Optional[2])"]
        G["C + empty → (Optional[C], Optional.empty)"]
        H["D + empty → (Optional[D], Optional.empty)"]
    end
    
    D --> I["Continues until BOTH streams exhausted"]
    I --> J["[(A,1), (B,2), (C,empty), (D,empty)]"]
    
    style A fill:#e1f5fe
    style B fill:#e1f5fe  
    style C fill:#fff3e0
    style J fill:#c8e6c9
    style I fill:#ffeb3b
```

**Key Benefits:**
- `zip()` - Fast pairing for equal-length streams  
- `zipAll()` - Handles different stream lengths gracefully
- `zipLongest()` - Uses default values instead of Optionals

---

## Sequence Generation

**Sequence generation** creates infinite streams and programmatic sequences inspired by Haskell and Clojure.

### How `cycle()` Works

```mermaid
flowchart LR
    A["['A', 'B', 'C']"] --> B["cycle()"]
    B --> C["Infinite Stream"]
    
    subgraph "Cyclic Pattern"
        D["A"] --> E["B"] 
        E --> F["C"]
        F --> G["A (repeat)"]
        G --> H["B (repeat)"]
        H --> I["C (repeat)"]
        I --> J["A ..."]
    end
    
    C --> K["limit(8)"]
    K --> L["['A', 'B', 'C', 'A', 'B', 'C', 'A', 'B']"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style C fill:#ffeb3b
    style L fill:#c8e6c9
```

### How `repeat()` vs `replicate()` Work

```mermaid
flowchart TB
    A["'Hello'"] --> B["repeat()"]
    B --> C["Infinite Stream of 'Hello'"]
    
    A --> D["replicate(5)"] 
    D --> E["Finite Stream: ['Hello', 'Hello', 'Hello', 'Hello', 'Hello']"]
    
    subgraph "Key Difference"
        F["repeat() = Infinite repetition"]
        G["replicate(n) = Finite n repetitions"]
    end
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style C fill:#ffeb3b
    style D fill:#fff3e0
    style E fill:#c8e6c9
    style F fill:#ff9800
    style G fill:#4caf50
```

**Use Cases:**
- `cycle()` - Round-robin processing, infinite patterns
- `repeat()` - Default values, infinite padding
- `replicate()` - Fixed-size initialization, test data
- `range()` - Numeric sequences with custom steps

---

## Enhanced Selectors

**Enhanced selectors** find and sort elements by custom criteria, inspired by Kotlin.

### How `minBy()` Works

```mermaid
flowchart TB
    A["[Person('Alice', 25), Person('Bob', 30), Person('Charlie', 20)]"] --> B["minBy(Person::getAge)"]
    
    subgraph "Selection Process"
        C["Alice: age 25"] --> D["Compare"]
        E["Bob: age 30"] --> D
        F["Charlie: age 20"] --> D
        D --> G["Minimum: Charlie (20)"]
    end
    
    B --> H["Optional[Person('Charlie', 20)]"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style H fill:#c8e6c9
    style G fill:#ffeb3b
```

### How `distinctBy()` Works

```mermaid
flowchart TB
    A["[Person('Alice', 'Engineering'), Person('Bob', 'Sales'), Person('Charlie', 'Engineering')]"] --> B["distinctBy(Person::getDepartment)"]
    
    subgraph "Deduplication Process"
        C["Alice: Engineering → Keep (first)"] --> D["Seen: {Engineering}"]
        E["Bob: Sales → Keep (new)"] --> F["Seen: {Engineering, Sales}"]
        G["Charlie: Engineering → Skip (duplicate)"] --> H["Already seen Engineering"]
    end
    
    B --> I["[Person('Alice', 'Engineering'), Person('Bob', 'Sales')]"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style I fill:#c8e6c9
    style H fill:#ffcdd2
```

**Key Operations:**
- `minBy()` / `maxBy()` - Find min/max by custom selector
- `sortedBy()` - Sort by custom key  
- `distinctBy()` - Remove duplicates by custom key
- `filterIsInstance()` - Type-safe filtering

---

## Mathematical Operations

**Mathematical operations** provide comprehensive statistics and numerical analysis.

### How `statistics()` Works

```mermaid
flowchart TB
    A["[1, 2, 3, 4, 5]"] --> B["statistics()"]
    
    subgraph "Comprehensive Analysis"
        C["Count: 5"] 
        D["Sum: 15"]
        E["Average: 3.0"]
        F["Min: 1, Max: 5"]
        G["Standard Deviation: 1.58"]
        H["Variance: 2.5"]
    end
    
    B --> I["StreamStatistics Object"]
    I --> J["All statistics in one pass"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style I fill:#c8e6c9
    style J fill:#ffeb3b
```

### How `frequencies()` Works

```mermaid
flowchart TB
    A["['apple', 'banana', 'apple', 'cherry', 'banana', 'apple']"] --> B["frequencies()"]
    
    subgraph "Counting Process"
        C["apple → 1"] --> D["banana → 1"]
        D --> E["apple → 2 (increment)"]
        E --> F["cherry → 1"]
        F --> G["banana → 2 (increment)"]
        G --> H["apple → 3 (increment)"]
    end
    
    B --> I["Map{apple=3, banana=2, cherry=1}"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style I fill:#c8e6c9
```

**Available Operations:**
- `statistics()` - Complete statistical analysis in one pass
- `median()` - Middle value calculation
- `mode()` - Most frequent element
- `frequencies()` - Element frequency counting
- `cumSum()` / `cumProduct()` - Cumulative operations

---

## Safe Operations

**Safe operations** handle errors gracefully without breaking the stream flow.

### How `mapSafely()` Works

```mermaid
flowchart TB
    A["['123', 'abc', '456', 'xyz']"] --> B["mapSafely(Integer::parseInt, -1)"]
    
    subgraph "Safe Processing"
        C["'123' → parseInt() → 123 ✓"] --> D["Keep: 123"]
        E["'abc' → parseInt() → Exception ✗"] --> F["Use default: -1"]
        G["'456' → parseInt() → 456 ✓"] --> H["Keep: 456"]
        I["'xyz' → parseInt() → Exception ✗"] --> J["Use default: -1"]
    end
    
    B --> K["[123, -1, 456, -1]"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style K fill:#c8e6c9
    style D fill:#c8e6c9
    style H fill:#c8e6c9
    style F fill:#ffcdd2
    style J fill:#ffcdd2
```

**Benefits:**
- No stream interruption on exceptions
- Configurable default values
- Clean error handling without try-catch blocks

---

## Async Processing

**Async processing** enables parallel execution of stream operations.

### How `asyncMap()` + `awaitAll()` Work

```mermaid
flowchart TB
    A["[task1, task2, task3, task4]"] --> B["asyncMap(executor)"]
    
    subgraph "Parallel Execution"
        C["task1 → CompletableFuture"] --> D["Executor Thread 1"]
        E["task2 → CompletableFuture"] --> F["Executor Thread 2"]
        G["task3 → CompletableFuture"] --> H["Executor Thread 3"]
        I["task4 → CompletableFuture"] --> J["Executor Thread 4"]
    end
    
    B --> K["Stream<CompletableFuture<Result>>"]
    K --> L["awaitAll()"]
    L --> M["[result1, result2, result3, result4]"]
    
    style A fill:#e1f5fe
    style B fill:#fff3e0
    style K fill:#ffeb3b
    style L fill:#fff3e0
    style M fill:#c8e6c9
```

**Key Features:**
- Parallel processing for I/O bound operations
- Custom executor support
- Clean CompletableFuture management

---

## Stream Transformations

StreamX also provides advanced transformation operations inspired by functional programming:

- **`unzip()`** - Split paired streams back into two streams
- **`transpose()`** - Flip 2D stream structure (rows ↔ columns)  
- **`pairwise()`** - Create consecutive element pairs
- **`interpose()`** - Insert separators between elements
- **`let()`** - Apply function to stream and continue pipeline

---

## Summary

This visual documentation demonstrates how StreamX brings functional programming power to Java Streams through:

1. **Clear Visual Models** - Each operation is easy to understand
2. **Efficient Processing** - Single-pass operations where possible  
3. **Error Resilience** - Graceful handling of edge cases
4. **Performance Focus** - Optimized algorithms (e.g., topN uses heaps)
5. **Type Safety** - Full generic support with compile-time checking

StreamX transforms complex stream operations into intuitive, visually understandable processes that make your Java code more expressive and maintainable.

**For complete API documentation and examples, see the [README](README.md) and [CHANGELOG](CHANGELOG.md).**
