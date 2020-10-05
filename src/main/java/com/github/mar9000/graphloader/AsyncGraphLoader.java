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
import com.github.mar9000.graphloader.loader.DataLoader;
import com.github.mar9000.graphloader.loader.ExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Main class to resolve a single result of type D or a list of it.
 * @author ML
 * @since 1.0.3
 */
public class AsyncGraphLoader extends AbstractGraphLoader {
    private final GlAssemblerContext assemblerContext;
    private final MappedBatchLoaderContext loaderContext;
    protected AsyncGraphLoader(MappedBatchLoaderRegistry registry, GlContextHolder contextHolder,
                               ExecutionContext executionContext, GraphLoaderOptions options) {
        super(registry, options);
        this.loaderContext = new MappedBatchLoaderContext(contextHolder, executionContext);
        this.assemblerContext = new GlAssemblerContext(contextHolder, instrumentedRegistry, executionContext);
    }

    /**
     * Resolve a single result D starting from a key K.
     */
    public <K,V,D> CompletableFuture<GlResult<D>> resolve(K key, String loaderName, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        final GlResult<D> result = new GlResult<>();
        final CompletableFuture<GlResult<D>> futureResult = new CompletableFuture<>();
        try {
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName, loaderContext);
            loader.load(key, v -> result.result(assembler.assemble(v, assemblerContext)));
            waitPendingLoaders(result, futureResult);
        } catch (Throwable e) {
            result.exception(e);
            futureResult.complete(result);
        }
        resolvePostconditions();
        return futureResult;
    }
    /**
     * Resolve a list of D starting from a list of keys K.
     */
    public <K,V,D> CompletableFuture<GlResult<List<D>>> resolveMany(List<K> keys, String loaderName, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        CompletableFuture<GlResult<List<D>>> futureResult = new CompletableFuture<>();
        GlResult<List<D>> result = new GlResult<>();
        try {
            result.result(new ArrayList<>());
            DataLoader<K, V> loader = assemblerContext.registry().loader(loaderName, loaderContext);
            keys.forEach(key -> {
                loader.load(key, v -> result.result().add(assembler.assemble(v, assemblerContext)));
            });
            waitPendingLoaders(result, futureResult);
        } catch (Throwable e) {
            result.exception(e);
            futureResult.complete(result);
        }
        resolvePostconditions();
        return futureResult;
    }
    /**
     * Async resolve a single result D starting from a value V.
     */
    public <V,D> CompletableFuture<GlResult<D>> resolveValue(V value, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        final CompletableFuture<GlResult<D>> future = new CompletableFuture<>();
        final GlResult<D> result = new GlResult<>();
        try {
            result.result(assembler.assemble(value, assemblerContext));
            waitPendingLoaders(result, future);
        } catch (Throwable e) {
            result.exception(e);
            future.complete(result);
        }
        resolvePostconditions();
        return future;
    }
    /**
     * Resolve a list of D starting from a list of values V.
     */
    public <V,D> CompletableFuture<GlResult<List<D>>> resolveValues(List<V> values, GlAssembler<V, D> assembler) {
        resolvePreconditions();
        CompletableFuture<GlResult<List<D>>> futureResult = new CompletableFuture<>();
        GlResult<List<D>> result = new GlResult<>();
        try {
            result.result(new ArrayList<>());
            values.forEach(v -> {
                result.result().add(assembler.assemble(v, assemblerContext));
            });
            waitPendingLoaders(result, futureResult);
        } catch (Throwable e) {
            result.exception(e);
            futureResult.complete(result);
        }
        resolvePostconditions();
        return futureResult;
    }

    // Utility methods.

    /**
     * Execute all pending loaders.
     */
    private <D> void waitPendingLoaders(GlResult<D> glResult, CompletableFuture<GlResult<D>> future) {
        if (this.instrumentation.pendingLoads() > 0) {
            instrumentation.resetPendingLoads();
            Optional<CompletableFuture<?>> dispatching = instrumentedRegistry.dispatchAll();
            if (!dispatching.isPresent())
                waitPendingLoaders(glResult, future);
            else
                dispatching.get().thenRunAsync(() -> waitPendingLoaders(glResult, future));
        } else
            future.complete(glResult);
    }
}
