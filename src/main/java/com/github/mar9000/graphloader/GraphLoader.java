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

import java.util.ArrayList;
import java.util.List;

/**
 * @author ML
 * @since 1.0.0
 */
public class GraphLoader {
    private transient final MappedBatchLoaderRegistry registry;
    /** Context that spans multiple resolve() invocations, e.g. a global context.   */
    private final GlContext context;
    public GraphLoader(MappedBatchLoaderRegistry registry, GlContext context) {
        this.registry = registry;
        this.context = context;
    }
    public <K,V,D> GlResult<D> resolve(K key, String loaderName, GlAssembler<V, D> assembler, ExecutionContext executionContext) {
        ExecutionState state = new ExecutionState();
        final GlResult<D> result = new GlResult<>(state);
        try {
            GlAssemblerContext assemblerContext = assemblerContext(executionContext, state);
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName);
            loader.load(key, v -> result.result(assembler.assemble(v, assemblerContext)));
            while(result.state().pendingLoads() > 0) {
                result.state().resetPendingLoads();
                assemblerContext.registry().dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
    public <K,V,D> GlResult<List<D>> resolveMany(List<K> keys, String loaderName, GlAssembler<V, D> assembler, ExecutionContext executionContext) {
        ExecutionState state = new ExecutionState();
        GlResult<List<D>> result = new GlResult<>(state);
        try {
            result.result(new ArrayList<>());
            GlAssemblerContext assemblerContext = assemblerContext(executionContext, state);
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName);
            keys.forEach(key -> {
                loader.load(key, v -> result.result().add(assembler.assemble(v, assemblerContext)));
            });
            while(result.state().pendingLoads() > 0) {
                result.state().resetPendingLoads();
                assemblerContext.registry().dispatchAll();
            }
        } catch (Exception e) {
            result.exception(e);
        }
        return result;
    }
    private GlAssemblerContext assemblerContext(ExecutionContext executionContext, ExecutionState state) {
        StatedDataLoaderRegistry statedRegistry = new StatedDataLoaderRegistry(registry, state);
        GlAssemblerContext assemblerContext = new GlAssemblerContext(context, statedRegistry, executionContext);
        return assemblerContext;
    }
}
