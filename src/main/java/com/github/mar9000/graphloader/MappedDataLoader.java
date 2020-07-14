package com.github.mar9000.graphloader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author ML
 * @since 1.0.0
 */
public class MappedDataLoader<K, V> implements DataLoader<K, V> {
    private final MappedBatchLoader<K, V> batchLoader;
    public MappedDataLoader(MappedBatchLoader<K, V> batchLoader) {
        this.batchLoader = batchLoader;
    }

    protected Map<K, List<Consumer<V>>> ids = new LinkedHashMap<>();
    @Override
    public void load(K id, Consumer<V> consumer) {
        List<Consumer<V>> list = ids.get(id);
        if (list == null) {
            list = new ArrayList<>();
            ids.put(id, list);
        }
        list.add(consumer);
    }
    @Override
    public void dispatch() {
        if (ids.size() == 0)
            return;
        Map<K, List<Consumer<V>>> copied = new LinkedHashMap<>();
        copied.putAll(ids);
        ids.clear();
        Map<K,V> map = batchLoader.load(copied.keySet(), null);
        map.forEach((k,v) -> {
            List<Consumer<V>> consumers = copied.get(k);
            consumers.forEach(c -> c.accept(v));
        });
    }
}
