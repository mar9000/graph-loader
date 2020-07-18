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
    private final GLContext context;
    public GraphLoader(MappedBatchLoaderRegistry registry, GLContext context) {
        this.registry = registry;
        this.context = context;
    }
    public <K,V,D> GLResult<D> resolve(K key, String loaderName, GLAssembler<V, D> assembler, ExecutionContext executionContext) {
        ExecutionState state = new ExecutionState();
        final GLResult<D> result = new GLResult<>(state);
        try {
            GLAssemblerContext assemblerContext = assemblerContext(executionContext, state);
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
    public <K,V,D> GLResult<List<D>> resolveMany(List<K> keys, String loaderName, GLAssembler<V, D> assembler, ExecutionContext executionContext) {
        ExecutionState state = new ExecutionState();
        GLResult<List<D>> result = new GLResult<>(state);
        try {
            result.result(new ArrayList<>());
            GLAssemblerContext assemblerContext = assemblerContext(executionContext, state);
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
    private GLAssemblerContext assemblerContext(ExecutionContext executionContext, ExecutionState state) {
        StatedDataLoaderRegistry statedRegistry = new StatedDataLoaderRegistry(registry, state);
        GLAssemblerContext assemblerContext = new GLAssemblerContext(context, statedRegistry, executionContext);
        return assemblerContext;
    }
}
