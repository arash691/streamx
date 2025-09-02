# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-09-02

### Added
- **First public release** of StreamX - Enhanced Java Stream Utilities
- **Core StreamX Features**:
  - Indexed operations (`withIndex`, `zipWithIndex`)
  - Scan operations (`scan`, `runningReduce`)
  - Advanced partitioning (`partition`, `span`, `partitionBy`)
  - Sequence generation (`cycle`, `repeat`, `replicate`, `range`)
  - Enhanced selectors (`minBy`, `maxBy`, `sortedBy`, `distinctBy`)
  - Advanced zipping (`zip`, `zipAll`, `zipLongest`)
  - Windowing operations (`chunked`, `windowed`, `sliding`)
  - Mathematical operations (`statistics`, `median`, `mode`, `frequencies`)
  - Safe operations (`mapSafely`, `filterSafely`)
  - Debugging tools (`debug`, `tap`)
  - String operations (`joinToString`)
  - Stream manipulation (`unzip`, `pairwise`, `interpose`, `transpose`)
  - Async processing (`asyncMap`, `awaitAll`)

- **Enhanced Collectors** (`StreamXCollectors`):
  - Top N collection without full sorting (`topN`)
  - LinkedHashMap collection (`toLinkedMap`)
  - MultiMap collection (`toMultiMap`) 
  - Random sampling (`sampling`)

- **Utility Classes**:
  - `Pair<T, U>` record for paired values
  - `IndexedValue<T>` record for indexed elements
  - `StreamStatistics` class for comprehensive statistics
  - `TriFunction` functional interface for three-parameter functions

- **Comprehensive Test Suite**:
  - 76 unit tests with 100% pass rate
  - 23 tests for core StreamX features
  - 41 tests for functional programming features  
  - 12 tests for enhanced collectors
  - Edge case handling and error scenarios

- **Documentation**:
  - Comprehensive README with examples from functional programming languages
  - Comparison with standard Java Stream operations
  - Real-world usage examples
  - Performance notes and design philosophy

- **Maven Configuration**:
  - Java 21 compatibility
  - Source and Javadoc JAR generation
  - Maven Central publishing configuration
  - MIT License

### Features Inspired By
- **Kotlin**: `maxBy`, `minBy`, `sortedBy`, `filterNotNull`, `filterIsInstance`, `onEach`, `let`, `joinToString`
- **Scala**: `scan`, `partition`, `span`, `unzip`, `zipAll`
- **Haskell**: `cycle`, `repeat`, `transpose`, `span`, `replicate` 
- **F#**: Mathematical operations, windowing, pipelining
- **Clojure**: `partitionBy`, `interpose`, `frequencies`

### Technical Details
- **Requires**: Java 21+
- **Dependencies**: None for runtime (only test dependencies)
- **JAR Size**: ~26KB main JAR
- **License**: MIT
- **Group ID**: `io.streamx`
- **Artifact ID**: `streamx`

[1.0.0]: https://github.com/arash691/streamx/releases/tag/v1.0.0
