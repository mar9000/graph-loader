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

import java.util.ArrayList;
import java.util.List;

/**
 * Main class to resolve a single result (K -> V -> D) or a list of them.
 * @author ML
 * @since 1.0.0
 */
public class GraphLoader {
    private transient final MappedBatchLoaderRegistry registry;
    /** Context that spans multiple resolve() invocations, e.g. a global context.   */
    private final GlContextHolder contextHolder;
    private final ExecutionContext executionContext;
    private final Instrumentation instrumentation;
    private final InstrumentedDataLoaderRegistry statedRegistry;
    private final GlAssemblerContext assemblerContext;
    private final MappedBatchLoaderContext loaderContext;
    protected GraphLoader(MappedBatchLoaderRegistry registry, GlContextHolder contextHolder,
                          ExecutionContext executionContext, GraphLoaderOptions options) {
        this.registry = registry;
        this.contextHolder = contextHolder;
        this.executionContext = executionContext;
        this.instrumentation = new Instrumentation();
        this.loaderContext = new MappedBatchLoaderContext(this.contextHolder, this.executionContext);
        statedRegistry = new InstrumentedDataLoaderRegistry(registry, instrumentation);
        statedRegistry.cachingEnabled(options.cachingEnabled());
        assemblerContext = new GlAssemblerContext(contextHolder, statedRegistry, executionContext);
    }
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
    public Instrumentation instrumentation() {
        return this.instrumentation;
    }
    private void resolvePreconditions() {
        if (this.instrumentation.pendingLoads() != 0)
            throw new GlPendingLoadsException("pendingLoads: " + this.instrumentation.pendingLoads());
        this.instrumentation.resetBatchedLoads();
    }
}
