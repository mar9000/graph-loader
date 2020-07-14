package com.github.mar9000.graphloader;

/**
 * @author ML
 * @since 1.0.0
 */
public class GLContext {
    private final Object context;
    public GLContext(Object context) {
        this.context = context;
    }
    public <T> T context() {
        return (T)context;
    }
}
