package com.github.mar9000.graphloader;

/**
 * @author ML
 * @since 1.0.0
 */
public interface DataLoaderRegistry {
    <K,V> MappedDataLoader<K,V> loader(String key);
    void dispatchAll();
}
