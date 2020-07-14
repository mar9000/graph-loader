package com.github.mar9000.graphloader;

import java.util.function.Consumer;

/**
 * @author ML
 * @since 1.0.0
 */
public interface DataLoader<K, V> {
    void load(K id, Consumer<V> consumer);
    void dispatch();
}
