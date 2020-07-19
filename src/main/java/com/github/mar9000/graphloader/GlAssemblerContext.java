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

/**
 * The context passed to each assembler. It contains the global context, the current execution context
 * and the DataLoaderRegistry.
 * @author ML
 * @since 1.0.0
 */
public class GlAssemblerContext {
    private final GlContextHolder glContextHolder;
    private final DataLoaderRegistry registry;
    private final ExecutionContext executionContext;
    public GlAssemblerContext(GlContextHolder glContextHolder, DataLoaderRegistry registry, ExecutionContext executionContext) {
        this.glContextHolder = glContextHolder;
        this.registry = registry;
        this.executionContext = executionContext;
    }
    public GlContextHolder glContext() {
        return this.glContextHolder;
    }
    public DataLoaderRegistry registry() {
        return this.registry;
    }
    public ExecutionContext executionContext() {
        return this.executionContext;
    }
}
