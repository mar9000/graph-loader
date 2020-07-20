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

import com.github.mar9000.graphloader.batch.MappedBatchLoaderRegistry;
import com.github.mar9000.graphloader.loader.ExecutionContext;

/**
 * Factory for {@link GraphLoader} usually application scoped.
 * @author ML
 * @since 1.0.1
 */
public class GraphLoaderFactory {
    private final MappedBatchLoaderRegistry registry;
    private final GlContextHolder contextHolder;
    public GraphLoaderFactory(MappedBatchLoaderRegistry registry, Object glContext) {
        this.registry = registry;
        this.contextHolder = new GlContextHolder(glContext);
    }
    public GraphLoader graphLoader(ExecutionContext executionContext) {
        return graphLoader(executionContext, new GraphLoaderOptions());
    }
    public GraphLoader graphLoader(ExecutionContext executionContext, GraphLoaderOptions options) {
        return new GraphLoader(this.registry, this.contextHolder, executionContext, options);
    }
}
