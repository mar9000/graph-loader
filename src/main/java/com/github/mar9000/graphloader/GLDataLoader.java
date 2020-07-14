package com.github.mar9000.graphloader;

import java.util.function.Consumer;

/**
 * @author ML
 * @since 1.0.0
 */
class GLDataLoader<K,V> extends MappedDataLoader<K,V> {
    private final ExecutionState state;
    public GLDataLoader(MappedBatchLoader<K, V> batchLoader, ExecutionState state) {
        super(batchLoader);
        this.state = state;
    }
    @Override
    public void load(K id, Consumer<V> consumer) {
        state.pendingLoads++;
        super.load(id, consumer);
    }

    @Override
    public void dispatch() {
        if (ids.size() != 0)
            state.batchedLoadCount++;
        super.dispatch();
    }
}
