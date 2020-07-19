package com.github.mar9000.graphloader.test;

import com.github.mar9000.graphloader.*;
import com.github.mar9000.graphloader.test.data.ExceptionPostDataLoader;
import com.github.mar9000.graphloader.test.data.PostDataLoader;
import com.github.mar9000.graphloader.test.data.PrepareData;
import com.github.mar9000.graphloader.test.data.UserDataLoader;
import com.github.mar9000.graphloader.test.resources.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ML
 * @since 1.0.0
 */
public class GraphLoaderTests {

    private static GraphLoaderFactory graphLoaderFactory;

    @BeforeAll
    static void init() {
        // Prepare objects that span multiple GL.resolve() invocations.
        PrepareData.defaultData();
        MappedBatchLoaderRegistry registry = new MappedBatchLoaderRegistry();
        registry.register("postLoader", new PostDataLoader());
        registry.register("exceptionPostLoader", new ExceptionPostDataLoader());
        registry.register("userLoader", new UserDataLoader());
        graphLoaderFactory = new GraphLoaderFactory(registry, new ServerContext("/rest"));
    }

    /**
     * Test resolve() one resource with different execution contexts.
     */
    @Test
    void test_item_load() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertNull(result.exception());
        assertEquals("me", result.result().author.name);
        assertEquals("/rest/1", result.result().path);
        assertEquals("06/07/20 12.12", result.result().date);

        // Second execution.
        context = new LocaleExecutionContext(Locale.US);
        graphLoader = graphLoaderFactory.graphLoader(context);
        result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertNull(result.exception());
        assertEquals("/rest/1", result.result().path);
        assertEquals("7/6/20 12:12 PM", result.result().date);
    }

    /**
     * Test data returned from cache when caching is enabled.
     */
    @Test
    void test_loading_stats_without_cache() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertEquals(2, graphLoader.instrumentation().batchedLoads());
        assertEquals(2, graphLoader.instrumentation().overallBatchedLoads());

        // No caching, reload again.
        result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertEquals(2, graphLoader.instrumentation().batchedLoads());
        assertEquals(4, graphLoader.instrumentation().overallBatchedLoads());
    }

    /**
     * Test data returned from cache when caching is enabled.
     */
    @Test
    void test_loading_stats_with_cache() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context, new GraphLoaderOptions().cachingEnabled(true));
        GlResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertEquals(2, graphLoader.instrumentation().batchedLoads());
        assertEquals(2, graphLoader.instrumentation().overallBatchedLoads());

        // Caching, no more load.
        result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertEquals(0, graphLoader.instrumentation().batchedLoads());
        assertEquals(2, graphLoader.instrumentation().overallBatchedLoads());
    }

    /**
     * Test resolve() a list of resource.
     */
    @Test
    void test_list_load() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "postLoader", new PostResourceAssembler());
        PostResource resource1 = result.result().get(0);
        PostResource resource2 = result.result().get(1);
        assertEquals("you", resource2.author.name);
        assertEquals("me", resource1.author.name);
        assertEquals("/rest/1", resource1.path);
        assertEquals("06/07/20 12.12", resource1.date);
        assertEquals(2, graphLoader.instrumentation().batchedLoads());
    }

    /**
     * Test exception in loader.
     */
    @Test
    void test_exception_in_assembler() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "exceptionPostLoader",
                new PostResourceAssembler());
        assertTrue(result.exception() instanceof RuntimeException);
    }

    /**
     * Test exception in assembler.
     */
    @Test
    void test_exception_in_loader() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "postLoader",
                new ExceptionPostResourceAssembler());
        assertTrue(result.exception() instanceof RuntimeException);
    }
}
