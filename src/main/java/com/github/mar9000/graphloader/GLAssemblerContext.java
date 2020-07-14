package com.github.mar9000.graphloader;

/**
 * @author ML
 * @since 1.0.0
 */
public class GLAssemblerContext {
    private final GLContext glContext;
    private final DataLoaderRegistry registry;
    private final ExecutionContext executionContext;
    public GLAssemblerContext(GLContext glContext, DataLoaderRegistry registry, ExecutionContext executionContext) {
        this.glContext = glContext;
        this.registry = registry;
        this.executionContext = executionContext;
    }
    public GLContext glContext() {
        return this.glContext;
    }
    public DataLoaderRegistry registry() {
        return this.registry;
    }
    public ExecutionContext executionContext() {
        return this.executionContext;
    }
}
