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
package com.github.mar9000.graphloader.batch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple registry for MappedBatchLoader instances.
 * It uses {@link ConcurrentHashMap} because with async API more than one {@link com.github.mar9000.graphloader.loader.DataLoader}
 * can trigger {@link java.util.function.Consumer}s than {@link com.github.mar9000.graphloader.assembler.GlAssembler}
 * than this class through {@link com.github.mar9000.graphloader.loader.InstrumentedDataLoaderRegistry}.
 * @author ML
 * @since 1.0.0
 */
public class MappedBatchLoaderRegistry {
    private final Map<String, MappedBatchLoader<?, ?>> batchLoaders = new ConcurrentHashMap<>();
    private final Map<String, AsyncMappedBatchLoader<?, ?>> asyncBatchLoaders = new ConcurrentHashMap<>();
    public <K,V> void register(String key, MappedBatchLoader<K,V> batchLoader) {
        batchLoaders.put(key, batchLoader);
    }
    public <K,V> void register(String key, AsyncMappedBatchLoader<K,V> batchLoader) {
        asyncBatchLoaders.put(key, batchLoader);
    }
    public <K,V> MappedBatchLoader<K,V> batchLoader(String key) {
        return (MappedBatchLoader<K,V>)batchLoaders.get(key);
    }
    public <K,V> AsyncMappedBatchLoader<K,V> asyncBatchLoader(String key) {
        return (AsyncMappedBatchLoader<K,V>)asyncBatchLoaders.get(key);
    }
}
