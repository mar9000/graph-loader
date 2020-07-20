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
package com.github.mar9000.graphloader.assembler;

import com.github.mar9000.graphloader.GlContextHolder;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;
import com.github.mar9000.graphloader.loader.DataLoaderRegistry;
import com.github.mar9000.graphloader.loader.ExecutionContext;

/**
 * The context passed to each assembler. It contains the global context, the current execution context
 * and the DataLoaderRegistry.
 * @author ML
 * @since 1.0.0
 */
public class GlAssemblerContext {
    private final DataLoaderRegistry registry;
    private final MappedBatchLoaderContext loaderContext;
    public GlAssemblerContext(GlContextHolder glContextHolder, DataLoaderRegistry registry, ExecutionContext executionContext) {
        this.registry = registry;
        this.loaderContext = new MappedBatchLoaderContext(glContextHolder, executionContext);
    }
    public DataLoaderRegistry registry() {
        return this.registry;
    }
    public MappedBatchLoaderContext loaderContext() {
        return this.loaderContext;
    }
}
