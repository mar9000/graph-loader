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

import com.github.mar9000.graphloader.batch.MappedBatchLoader;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderRegistry;

import java.util.*;

/**
 * @author ML
 * @since 1.0.0
 */
public class InstrumentedDataLoaderRegistry implements DataLoaderRegistry {
    private final MappedBatchLoaderRegistry batchLoaderRegistry;
    private final Instrumentation instrumentation;
    private final Map<String, MappedDataLoader<?, ?>> dataLoaders = new HashMap<>();
    private boolean cachingEnabled = false;
    public InstrumentedDataLoaderRegistry(MappedBatchLoaderRegistry batchLoaderRegistry, Instrumentation instrumentation) {
        this.batchLoaderRegistry = batchLoaderRegistry;
        this.instrumentation = instrumentation;
    }
    @Override
    public <K,V> MappedDataLoader<K,V> loader(String key, Object context) {
        MappedDataLoader<K,V> dataLoader = (MappedDataLoader<K,V>)dataLoaders.get(key);
        if (dataLoader == null) {
            MappedBatchLoader<K, V> batchLoader = batchLoaderRegistry.batchLoader(key);
            if (batchLoader == null)
                throw new IllegalArgumentException("batchLoader not found: "+key);
            dataLoader = new InstrumentedDataLoader<K,V>(batchLoader, instrumentation, cachingEnabled,
                    (MappedBatchLoaderContext) context);
            dataLoaders.put(key, dataLoader);
        }
        return dataLoader;
    }
    public void dispatchAll() {
        // Dispatch operation will cause more loaders to be created, avoid ConcurrentModificationException.
        Collection<MappedDataLoader<?, ?>> loadersToDispatch = new ArrayList<>(dataLoaders.values());
        loadersToDispatch.forEach(MappedDataLoader::dispatch);
    }
    public void cachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;
    }
}
