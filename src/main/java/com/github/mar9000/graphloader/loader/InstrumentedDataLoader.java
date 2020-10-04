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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author ML
 * @since 1.0.0
 */
public class InstrumentedDataLoader<K,V> extends MappedDataLoader<K,V> {
    private final Instrumentation instrumentation;
    private final boolean cachingEnabled;
    private final Map<K,V> cacheMap;
    public InstrumentedDataLoader(MappedBatchLoader<K, V> batchLoader, Instrumentation instrumentation,
                                  boolean cachingEnabled, MappedBatchLoaderContext context) {
        super(batchLoader, context);
        this.instrumentation = instrumentation;
        this.cachingEnabled = cachingEnabled;
        this.cacheMap = cachingEnabled ? new LinkedHashMap<>() : null;
    }
    @Override
    public synchronized void load(K key, Consumer<V> consumer) {
        if (cachingEnabled) {
            V v = cacheMap.get(key);
            if (v != null) {
                consumer.accept(v);
                return;
            }
        }
        instrumentation.incPendingLoads();
        if (!cachingEnabled)
            super.load(key, consumer);
        else
            super.load(key, v -> {
                cacheMap.put(key, v);
                consumer.accept(v);
            });
    }
}
