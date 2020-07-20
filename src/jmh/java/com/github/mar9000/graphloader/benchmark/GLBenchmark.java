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
package com.github.mar9000.graphloader.benchmark;

import com.github.mar9000.graphloader.*;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderRegistry;
import com.github.mar9000.graphloader.loader.ExecutionContext;
import com.github.mar9000.graphloader.test.data.PostDataLoader;
import com.github.mar9000.graphloader.test.data.PrepareData;
import com.github.mar9000.graphloader.test.data.UserDataLoader;
import com.github.mar9000.graphloader.test.resources.LocaleExecutionContext;
import com.github.mar9000.graphloader.test.resources.PostResource;
import com.github.mar9000.graphloader.test.resources.PostResourceAssembler;
import com.github.mar9000.graphloader.test.resources.ServerContext;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Locale;

/**
 * @author ML
 * @since 1.0.0
 */
@State(Scope.Benchmark)
public class GLBenchmark {
    GraphLoaderFactory factory;
    @Setup
    public void setup() {
        // Prepare objects that span multiple GL.resolve() invocations.
        PrepareData.defaultData();
        MappedBatchLoaderRegistry registry = new MappedBatchLoaderRegistry();
        registry.register("postLoader", new PostDataLoader());
        registry.register("userLoader", new UserDataLoader());
        factory = new GraphLoaderFactory(registry, new ServerContext("/rest"));
    }
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void glAvgTime(Blackhole blackhole) {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = factory.graphLoader(context);
        GlResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        blackhole.consume(result);
    }
}
