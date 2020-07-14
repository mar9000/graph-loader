package com.github.mar9000.graphloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ML
 * @since 1.0.0
 */
public class MappedBatchLoaderRegistry {
    private final Map<String, MappedBatchLoader<?, ?>> batchLoaders = new ConcurrentHashMap<>();
    public <K,V> void register(String key, MappedBatchLoader<K,V> batchLoader) {
        batchLoaders.put(key, batchLoader);
    }
    public <K,V> MappedBatchLoader<K,V> batchLoader(String key) {
        return (MappedBatchLoader<K,V>)batchLoaders.get(key);
    }
}
