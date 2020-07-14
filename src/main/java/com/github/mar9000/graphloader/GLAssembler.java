package com.github.mar9000.graphloader;

/**
 * @author ML
 * @since 1.0.0
 */
public interface GLAssembler<V, D> {
    public D assemble(V value, GLAssemblerContext context);
}
