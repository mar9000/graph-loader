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
    protected GraphLoader(MappedBatchLoaderRegistry registry, GlContextHolder contextHolder, ExecutionContext executionContext) {
        this.registry = registry;
        this.contextHolder = contextHolder;
        this.executionContext = executionContext;
    }
    public <K,V,D> GlResult<D> resolve(K key, String loaderName, GlAssembler<V, D> assembler) {
        ExecutionState state = new ExecutionState();
        final GlResult<D> result = new GlResult<>(state);
        try {
            StatedDataLoaderRegistry statedRegistry = new StatedDataLoaderRegistry(registry, state);
            GlAssemblerContext assemblerContext = new GlAssemblerContext(contextHolder, statedRegistry, executionContext);
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName);
            loader.load(key, v -> result.result(assembler.assemble(v, assemblerContext)));
            while(result.state().pendingLoads() > 0) {
                result.state().resetPendingLoads();
                statedRegistry.dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
    public <K,V,D> GlResult<List<D>> resolveMany(List<K> keys, String loaderName, GlAssembler<V, D> assembler) {
        ExecutionState state = new ExecutionState();
        GlResult<List<D>> result = new GlResult<>(state);
        try {
            result.result(new ArrayList<>());
            StatedDataLoaderRegistry statedRegistry = new StatedDataLoaderRegistry(registry, state);
            GlAssemblerContext assemblerContext = new GlAssemblerContext(contextHolder, statedRegistry, executionContext);
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName);
            keys.forEach(key -> {
                loader.load(key, v -> result.result().add(assembler.assemble(v, assemblerContext)));
            });
            while(result.state().pendingLoads() > 0) {
                result.state().resetPendingLoads();
                statedRegistry.dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
}
