package com.github.mar9000.graphloader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ML
 * @since 1.0.0
 */
public class GL {
    private final MappedBatchLoaderRegistry registry;
    /** Context that spans multiple resolve() invocations, e.g. a global context.   */
    private final GLContext context;
    public GL(MappedBatchLoaderRegistry registry, GLContext context) {
        this.registry = registry;
        this.context = context;
    }
    public <K,V,D> GLResult<D> resolve(K key, String loaderName, GLAssembler<V, D> assembler, ExecutionContext executionContext) {
        GLResult<D> result = new GLResult<>();
        GLAssemblerContext assemblerContext = assemblerContext(executionContext, result);
        MappedDataLoader<K, V> loader = assemblerContext.registry().loader(loaderName);
        loader.load(key, v -> result.result = assembler.assemble(v, assemblerContext));
        while(result.state.pendingLoads > 0) {
            result.state.pendingLoads = 0;
            assemblerContext.registry().dispatchAll();
        }
        return result;
    }
    public <K,V,D> GLResult<List<D>> resolveMany(List<K> keys, String loaderName, GLAssembler<V, D> assembler, ExecutionContext executionContext) {
        GLResult<List<D>> result = new GLResult<>();
        result.result = new ArrayList<>();
        GLAssemblerContext assemblerContext = assemblerContext(executionContext, result);
        MappedDataLoader<K, V> loader = assemblerContext.registry().loader(loaderName);
        keys.forEach(key -> {
            loader.load(key, v -> result.result.add(assembler.assemble(v, assemblerContext)));
        });
        while(result.state.pendingLoads > 0) {
            result.state.pendingLoads = 0;
            assemblerContext.registry().dispatchAll();
        }
        return result;
    }
    private GLAssemblerContext assemblerContext(ExecutionContext executionContext, GLResult<?> result) {
        ExecutionState state = new ExecutionState();
        StatedDataLoaderRegistry statedRegistry = new StatedDataLoaderRegistry(registry, state);
        result.state = state;
        GLAssemblerContext assemblerContext = new GLAssemblerContext(context, statedRegistry, executionContext);
        return assemblerContext;
    }
}
