/**
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
package com.github.mar9000.graphloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ML
 * @since 1.0.0
 */
public class StatedDataLoaderRegistry implements DataLoaderRegistry {
    private final MappedBatchLoaderRegistry batchLoaderRegistry;
    private final ExecutionState state;
    public StatedDataLoaderRegistry(MappedBatchLoaderRegistry batchLoaderRegistry, ExecutionState state) {
        this.batchLoaderRegistry = batchLoaderRegistry;
        this.state = state;
    }
    private final Map<String, MappedDataLoader<?, ?>> dataLoaders = new ConcurrentHashMap<>();
    @Override
    public <K,V> MappedDataLoader<K,V> loader(String key) {
        MappedDataLoader<K,V> dataLoader = (MappedDataLoader<K,V>)dataLoaders.get(key);
        if (dataLoader == null) {
            MappedBatchLoader<K, V> batchLoader = batchLoaderRegistry.batchLoader(key);
            if (batchLoader == null)
                throw new IllegalArgumentException("batchLoader not found: "+key);
            dataLoader = new GLDataLoader<>(batchLoader, state);
            dataLoaders.put(key, dataLoader);
        }
        return dataLoader;
    }
    @Override
    public void dispatchAll() {
        dataLoaders.values().forEach(dataLoader -> dataLoader.dispatch());
    }
}
