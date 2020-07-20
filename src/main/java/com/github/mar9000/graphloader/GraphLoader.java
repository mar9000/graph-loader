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
package com.github.mar9000.graphloader;

import com.github.mar9000.graphloader.assembler.GlAssembler;
import com.github.mar9000.graphloader.assembler.GlAssemblerContext;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderRegistry;
import com.github.mar9000.graphloader.exceptions.GlPendingLoadsException;
import com.github.mar9000.graphloader.loader.DataLoader;
import com.github.mar9000.graphloader.loader.ExecutionContext;
import com.github.mar9000.graphloader.loader.Instrumentation;
import com.github.mar9000.graphloader.loader.InstrumentedDataLoaderRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class to resolve a single result of type D or a list of it.
 * @author ML
 * @since 1.0.0
 */
public class GraphLoader {
    private final Instrumentation instrumentation;
    private final InstrumentedDataLoaderRegistry statedRegistry;
    private final GlAssemblerContext assemblerContext;
    private final MappedBatchLoaderContext loaderContext;
    protected GraphLoader(MappedBatchLoaderRegistry registry, GlContextHolder contextHolder,
                          ExecutionContext executionContext, GraphLoaderOptions options) {
        this.instrumentation = new Instrumentation();
        this.loaderContext = new MappedBatchLoaderContext(contextHolder, executionContext);
        this.statedRegistry = new InstrumentedDataLoaderRegistry(registry, this.instrumentation);
        this.statedRegistry.cachingEnabled(options.cachingEnabled());
        this.assemblerContext = new GlAssemblerContext(contextHolder, this.statedRegistry, executionContext);
    }

    /**
     * Resolve a single result D starting from a key K.
     */
    public <K,V,D> GlResult<D> resolve(K key, String loaderName, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        final GlResult<D> result = new GlResult<>();
        try {
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName, loaderContext);
            loader.load(key, v -> result.result(assembler.assemble(v, assemblerContext)));
            while(this.instrumentation.pendingLoads() > 0) {
                instrumentation.resetPendingLoads();
                statedRegistry.dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
    /**
     * Resolve a list of D starting from a list of keys K.
     */
    public <K,V,D> GlResult<List<D>> resolveMany(List<K> keys, String loaderName, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        GlResult<List<D>> result = new GlResult<>();
        try {
            result.result(new ArrayList<>());
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName, loaderContext);
            keys.forEach(key -> {
                loader.load(key, v -> result.result().add(assembler.assemble(v, assemblerContext)));
            });
            while(instrumentation.pendingLoads() > 0) {
                instrumentation.resetPendingLoads();
                statedRegistry.dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
    /**
     * Resolve a single result D starting from a value V.
     */
    public <V,D> GlResult<D> resolveValue(V value, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        final GlResult<D> result = new GlResult<>();
        try {
            result.result(assembler.assemble(value, assemblerContext));
            while(this.instrumentation.pendingLoads() > 0) {
                instrumentation.resetPendingLoads();
                statedRegistry.dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
    /**
     * Resolve a list of D starting from a list of values V.
     */
    public <V,D> GlResult<List<D>> resolveValues(List<V> values, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        GlResult<List<D>> result = new GlResult<>();
        try {
            result.result(new ArrayList<>());
            values.forEach(v -> {
                result.result().add(assembler.assemble(v, assemblerContext));
            });
            while(instrumentation.pendingLoads() > 0) {
                instrumentation.resetPendingLoads();
                statedRegistry.dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
    private void resolvePreconditions() {
        if (this.instrumentation.pendingLoads() != 0)
            throw new GlPendingLoadsException("pendingLoads: " + this.instrumentation.pendingLoads());
        this.instrumentation.resetBatchedLoads();
    }
    public int batchedLoads() {
        return instrumentation.batchedLoads();
    }
    public int overallBatchedLoads() {
        return instrumentation.overallBatchedLoads();
    }
}
