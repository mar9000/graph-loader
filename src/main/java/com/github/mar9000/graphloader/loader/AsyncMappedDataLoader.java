/*
 * Copyright 2020 Marco Lombardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mar9000.graphloader.loader;

import com.github.mar9000.graphloader.batch.AsyncMappedBatchLoader;
import com.github.mar9000.graphloader.batch.MappedBatchLoader;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;
import com.github.mar9000.graphloader.stats.SimpleStatisticsCollector;
import com.github.mar9000.graphloader.stats.Statistics;
import com.github.mar9000.graphloader.stats.StatisticsCollector;

import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 * {@link DataLoader} implementation using a {@link MappedBatchLoader}.
 * Data are loaded and passed to the Consumers only when dispatch() is called.
 * @author ML
 * @since 1.0.0
 */
public class AsyncMappedDataLoader<K, V> implements DataLoader<K, V> {
    private final AsyncMappedBatchLoader<K, V> batchLoader;
    private final MappedBatchLoaderContext context;
    private final StatisticsCollector statisticsCollector;
    protected Map<K, List<Consumer<V>>> pendingConsumers = new LinkedHashMap<>();
    public AsyncMappedDataLoader(AsyncMappedBatchLoader<K, V> batchLoader, MappedBatchLoaderContext context) {
        this.batchLoader = batchLoader;
        this.context = context;
        this.statisticsCollector = new SimpleStatisticsCollector();
    }
    @Override
    public void load(K key, Consumer<V> consumer) {
        List<Consumer<V>> list = pendingConsumers.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(consumer);
    }
    @Override
    public Optional<CompletionStage<?>> dispatch() {
        if (!dispatchNeeded())
            return Optional.empty();
        this.statisticsCollector.incrementBatchLoadCountBy(1);
        Map<K, List<Consumer<V>>> copied = new LinkedHashMap<>(pendingConsumers);
        pendingConsumers.clear();
        CompletionStage<Map<K,V>> future = batchLoader.load(copied.keySet(), context);
        CompletionStage<?> consumersDispatched = future.thenAcceptAsync(map -> {
            map.forEach((k,v) -> {
                List<Consumer<V>> consumers = copied.get(k);
                consumers.forEach(c -> c.accept(v));
            });
        });
        return Optional.of(consumersDispatched);
    }
    @Override
    public boolean abortPending() {
        boolean dispatchNeeded = dispatchNeeded();
        if (dispatchNeeded)
            pendingConsumers.clear();
        return dispatchNeeded;
    }
    private boolean dispatchNeeded() {
        return pendingConsumers.size() > 0;
    }
    @Override
    public Statistics statistics() {
        return statisticsCollector.statistics();
    }
}
