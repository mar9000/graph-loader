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
import com.github.mar9000.graphloader.batch.MappedBatchLoaderRegistry;
import com.github.mar9000.graphloader.exceptions.GlLoaderNotFoundException;
import com.github.mar9000.graphloader.stats.Statistics;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * @author ML
 * @since 1.0.0
 */
public class InstrumentedDataLoaderRegistry implements DataLoaderRegistry {
    private final MappedBatchLoaderRegistry batchLoaderRegistry;
    private final Instrumentation instrumentation;
    private final Map<String, DataLoader<?, ?>> dataLoaders = new HashMap<>();
    private boolean cachingEnabled = false;
    public InstrumentedDataLoaderRegistry(MappedBatchLoaderRegistry batchLoaderRegistry, Instrumentation instrumentation) {
        this.batchLoaderRegistry = batchLoaderRegistry;
        this.instrumentation = instrumentation;
    }
    @Override
    public <K,V> DataLoader<K,V> loader(String key, Object context) {
        synchronized (dataLoaders) {
            DataLoader<K,V> dataLoader = (DataLoader<K,V>)dataLoaders.get(key);
            if (dataLoader != null)
                return dataLoader;
            dataLoader = syncDataLoader(key, context);
            if (dataLoader != null)
                return dataLoader;
            dataLoader = asyncDataLoader(key, context);
            if (dataLoader != null)
                return dataLoader;
        }
        throw new GlLoaderNotFoundException(key);
    }
    private <K,V> DataLoader<K,V> syncDataLoader(String key, Object context) {
        MappedBatchLoader<K, V> batchLoader = batchLoaderRegistry.batchLoader(key);
        if (batchLoader == null)
            return null;
        InstrumentedDataLoader<K,V> dataLoader = new InstrumentedDataLoader<K,V>(batchLoader, instrumentation, cachingEnabled,
                (MappedBatchLoaderContext) context);
        dataLoaders.put(key, dataLoader);
        return dataLoader;
    }
    private <K,V> DataLoader<K,V> asyncDataLoader(String key, Object context) {
        AsyncMappedBatchLoader<K, V> batchLoader = batchLoaderRegistry.asyncBatchLoader(key);
        if (batchLoader == null)
            return null;
        DataLoader<K,V> dataLoader = new AsyncInstrumentedDataLoader<K,V>(batchLoader, instrumentation, cachingEnabled,
                (MappedBatchLoaderContext) context);
        dataLoaders.put(key, dataLoader);
        return dataLoader;
    }
    /**
     * Not thread-safe but it's executed only by {@link com.github.mar9000.graphloader.GraphLoader}
     * when there are no pending Future, either not yet started or already completed.
     */
    public Optional<CompletableFuture<?>> dispatchAll() {
        // Dispatch operation will cause more loaders to be created, avoid ConcurrentModificationException.
        Collection<DataLoader<?, ?>> loadersToDispatch = new ArrayList<>(dataLoaders.values());
        List<CompletionStage<?>> dispatching = loadersToDispatch.stream()
                .map(DataLoader::dispatch)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (dispatching.size() == 0)
            return Optional.empty();
        return Optional.of(CompletableFuture.allOf(dispatching.toArray(new CompletableFuture[0])));
    }
    /**
     * Require all loaders to clear the list of pending consumers to execute.
     * @return true if at least a loader clears its internal list.
     * Not thread-safe but it's executed only by {@link com.github.mar9000.graphloader.GraphLoader}
     * when there are no pending Future, either not yet started or already completed.
     */
    public boolean abortAll() {
        return dataLoaders.values().stream().anyMatch(DataLoader::abortPending);
    }
    public void cachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;
    }
    /**
     * Not thread-safe.
     */
    @Override
    public Statistics statistics(String key) {
        DataLoader<?,?> loader = dataLoaders.get(key);
        if (loader == null)
            return new Statistics();
        return loader.statistics();
    }
    /**
     * Not thread-safe.
     */
    @Override
    public Statistics statistics() {
        Statistics statistics = new Statistics();
        for (DataLoader<?,?> loader : dataLoaders.values()) {
            statistics = statistics.combine(loader.statistics());
        }
        return statistics;
    }
}
