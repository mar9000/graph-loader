package com.github.mar9000.graphloader.benchmark;

import com.github.mar9000.graphloader.*;
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
    GraphLoader graphLoader;
    @Setup
    public void setup() {
        // Prepare objects that span multiple GL.resolve() invocations.
        PrepareData.defaultData();
        MappedBatchLoaderRegistry registry = new MappedBatchLoaderRegistry();
        registry.register("postLoader", new PostDataLoader());
        registry.register("userLoader", new UserDataLoader());
        graphLoader = new GraphLoader(registry, new GLContext(new ServerContext("/rest")));
    }
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void glAvgTime(Blackhole blackhole) {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GLResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler(), context);
        blackhole.consume(result);
    }
}
