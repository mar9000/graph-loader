package com.github.mar9000.graphloader;

import java.util.Map;
import java.util.Set;

/**
 * @author ML
 * @since 1.0.0
 */
public interface MappedBatchLoader<K, V> {
    Map<K, V> load(Set<K> keys, ExecutionContext context);
}
