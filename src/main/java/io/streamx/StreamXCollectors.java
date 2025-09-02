package io.streamx;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Enhanced collectors for StreamX
 */
public class StreamXCollectors {
    
    /**
     * Collects to a LinkedHashMap preserving insertion order
     */
    public static <T, K, V> Collector<T, ?, LinkedHashMap<K, V>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper) {
        return Collector.of(
            LinkedHashMap::new,
            (map, item) -> map.put(keyMapper.apply(item), valueMapper.apply(item)),
            (map1, map2) -> { map1.putAll(map2); return map1; }
        );
    }
    
    /**
     * Collects to a LinkedHashMap with merge function for duplicate keys
     */
    public static <T, K, V> Collector<T, ?, LinkedHashMap<K, V>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper,
            BinaryOperator<V> mergeFunction) {
        return Collector.of(
            LinkedHashMap::new,
            (map, item) -> {
                K key = keyMapper.apply(item);
                V value = valueMapper.apply(item);
                map.merge(key, value, mergeFunction);
            },
            (map1, map2) -> {
                map2.forEach((k, v) -> map1.merge(k, v, mergeFunction));
                return map1;
            }
        );
    }
    
    /**
     * Collects to a MultiMap (Map<K, List<V>>)
     */
    public static <T, K, V> Collector<T, ?, Map<K, List<V>>> toMultiMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper) {
        return Collector.of(
            HashMap::new,
            (map, item) -> {
                K key = keyMapper.apply(item);
                V value = valueMapper.apply(item);
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            },
            (map1, map2) -> {
                map2.forEach((k, v) -> map1.merge(k, v, (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                }));
                return map1;
            }
        );
    }
    
    /**
     * Collects top N elements based on natural ordering
     */
    public static <T extends Comparable<T>> Collector<T, ?, List<T>> topN(int n) {
        return topN(n, Comparator.naturalOrder());
    }
    
    /**
     * Collects top N elements based on provided comparator
     */
    public static <T> Collector<T, ?, List<T>> topN(int n, Comparator<? super T> comparator) {
        return Collector.<T, PriorityQueue<T>, List<T>>of(
            () -> new PriorityQueue<>(comparator), // Min-heap: smallest at top
            (queue, item) -> {
                if (queue.size() < n) {
                    queue.offer(item);
                } else if (comparator.compare(item, queue.peek()) > 0) {
                    queue.poll();
                    queue.offer(item);
                }
            },
            (queue1, queue2) -> {
                queue2.forEach(item -> {
                    if (queue1.size() < n) {
                        queue1.offer(item);
                    } else if (comparator.compare(item, queue1.peek()) > 0) {
                        queue1.poll();
                        queue1.offer(item);
                    }
                });
                return queue1;
            },
            queue -> {
                List<T> result = new ArrayList<T>(queue);
                result.sort(comparator.reversed()); // Sort in descending order
                return result;
            }
        );
    }
    
    /**
     * Creates a histogram (frequency count) of elements
     */
    public static <T, K> Collector<T, ?, Map<K, Long>> histogram(Function<? super T, ? extends K> classifier) {
        return Collectors.groupingBy(classifier, Collectors.counting());
    }
    
    /**
     * Random sampling collector that selects up to n random elements
     */
    public static <T> Collector<T, ?, List<T>> sampling(int n) {
        return sampling(n, new Random());
    }
    
    /**
     * Random sampling collector with provided Random instance
     */
    public static <T> Collector<T, ?, List<T>> sampling(int n, Random random) {
        return Collector.of(
            () -> new ReservoirSampler<T>(n, random),
            ReservoirSampler::add,
            (sampler1, sampler2) -> {
                sampler1.merge(sampler2);
                return sampler1;
            },
            ReservoirSampler::getSample
        );
    }
    
    /**
     * Partitions elements into multiple groups based on a classifier function
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>> partitioningBy(Function<? super T, ? extends K> classifier) {
        return Collectors.groupingBy(classifier);
    }
    
    /**
     * Reservoir sampling implementation for random sampling
     */
    private static class ReservoirSampler<T> {
        private final List<T> reservoir;
        private final int maxSize;
        private final Random random;
        private int itemsSeen = 0;
        
        public ReservoirSampler(int maxSize, Random random) {
            this.maxSize = maxSize;
            this.random = random;
            this.reservoir = new ArrayList<>(maxSize);
        }
        
        public void add(T item) {
            itemsSeen++;
            if (reservoir.size() < maxSize) {
                reservoir.add(item);
            } else {
                int randomIndex = random.nextInt(itemsSeen);
                if (randomIndex < maxSize) {
                    reservoir.set(randomIndex, item);
                }
            }
        }
        
        public void merge(ReservoirSampler<T> other) {
            for (T item : other.reservoir) {
                add(item);
            }
        }
        
        public List<T> getSample() {
            return new ArrayList<>(reservoir);
        }
    }
}
