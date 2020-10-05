package com.github.mar9000.graphloader.test;

import com.github.mar9000.graphloader.*;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderRegistry;
import com.github.mar9000.graphloader.exceptions.GlDispatchException;
import com.github.mar9000.graphloader.exceptions.GlLoaderNotFoundException;
import com.github.mar9000.graphloader.loader.ExecutionContext;
import com.github.mar9000.graphloader.test.data.*;
import com.github.mar9000.graphloader.test.resources.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ML
 * @since 1.0.0
 */
public class GraphLoaderTests {

    private static GraphLoaderFactory graphLoaderFactory;
    DateTimeFormatter italyDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.ITALY);
    DateTimeFormatter usDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.US);

    @BeforeAll
    static void init() {
        // Prepare objects that span multiple GL.resolve() invocations.
        PrepareData.defaultData();
        MappedBatchLoaderRegistry registry = new MappedBatchLoaderRegistry();
        registry.register("postLoader", new PostDataLoader());
        registry.register("asyncPostLoader", new AsyncPostDataLoader());
        registry.register("exceptionPostLoader", new ExceptionPostDataLoader());
        registry.register("userLoader", new UserMappedBatchLoader());
        registry.register("asyncUserLoader", new UserAsyncMappedBatchLoader());
        registry.register("commentByPostIdLoader", new CommentByPostIdDataLoader());
        registry.register("asyncCommentByPostIdLoader", new AsyncCommentByPostIdDataLoader());
        graphLoaderFactory = new GraphLoaderFactory(registry, new ServerContext("/rest"));
    }

    /**
     * Test resolve() one resource with different execution contexts.
     */
    @Test
    void test_resolve() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertNull(result.exception());
        assertEquals("me", result.result().author.name);
        assertEquals("/rest/1", result.result().path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), result.result().date);

        // Second execution.
        context = new LocaleExecutionContext(Locale.US);
        graphLoader = graphLoaderFactory.graphLoader(context);
        result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertNull(result.exception());
        assertEquals("/rest/1", result.result().path);
        assertEquals(usDateTimeFormatter.format(PrepareData.post1.date), result.result().date);
    }
    /**
     * Test resolveAsync().
     */
    @Test
    void test_resolveAsync() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        AsyncGraphLoader graphLoader = graphLoaderFactory.asyncGraphLoader(context);
        CompletableFuture<GlResult<PostResource>> future = graphLoader.resolve(1L, "asyncPostLoader", new AsyncPostResourceAssembler());
        GlResult<PostResource> result = future.join();
        assertNull(result.exception());
        assertEquals("me", result.result().author.name);
        assertEquals("/rest/1", result.result().path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), result.result().date);

        // Second execution.
        context = new LocaleExecutionContext(Locale.US);
        graphLoader = graphLoaderFactory.asyncGraphLoader(context);
        future = graphLoader.resolve(1L, "postLoader", new AsyncPostResourceAssembler());
        result = future.join();
        assertNull(result.exception());
        assertEquals("/rest/1", result.result().path);
        assertEquals(usDateTimeFormatter.format(PrepareData.post1.date), result.result().date);
    }
    /**
     * Test resolveAsync() exception.
     */
    @Test
    void test_resolveAsync_exception() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<PostResource> result = graphLoader.resolve(1L, "postLoader",
                new AsyncPostResourceAssembler());
        assertTrue(result.exception() instanceof GlDispatchException);
    }

    @Test
    void test_resolve_value() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        Post post1 = PostRepository.load(new LinkedHashSet<Long>(Arrays.asList(new Long(1))))
                .get(1l);
        GlResult<PostResource> result = graphLoader.resolveValue(post1, new PostResourceAssembler());
        assertNull(result.exception());
        assertEquals("me", result.result().author.name);
        assertEquals("/rest/1", result.result().path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), result.result().date);
    }
    @Test
    void test_resolve_value_async() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        AsyncGraphLoader graphLoader = graphLoaderFactory.asyncGraphLoader(context);
        Post post1 = PostRepository.load(new LinkedHashSet<Long>(Arrays.asList(new Long(1))))
                .get(1l);
        CompletableFuture<GlResult<PostResource>> future = graphLoader.resolveValue(post1,
                new AsyncPostResourceAssembler());
        GlResult<PostResource> result = future.join();
        assertNull(result.exception());
        assertEquals("me", result.result().author.name);
        assertEquals("/rest/1", result.result().path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), result.result().date);
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
        assertEquals(2, graphLoader.statistics().batchLoadCount());

        // No caching, reload again.
        result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertEquals(2+2, graphLoader.statistics().batchLoadCount());
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
        assertEquals(2, graphLoader.statistics().batchLoadCount());

        // Caching, no more load.
        result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler());
        assertEquals(2+0, graphLoader.statistics().batchLoadCount());
    }

    /**
     * Test resolve() a list of resource.
     */
    @Test
    void test_resolve_many() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "postLoader",
                new PostResourceAssembler());
        PostResource resource1 = result.result().get(0);
        PostResource resource2 = result.result().get(1);
        assertEquals("you", resource2.author.name);
        assertEquals("me", resource1.author.name);
        assertEquals("/rest/1", resource1.path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), resource1.date);
        assertEquals(2, graphLoader.statistics().batchInvokeCount());
    }
    /**
     * Test resolveManyAsync() a list of resource.
     */
    @Test
    void test_resolve_many_async() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        AsyncGraphLoader graphLoader = graphLoaderFactory.asyncGraphLoader(context);
        CompletableFuture<GlResult<List<PostResource>>> future = graphLoader.resolveMany(Arrays.asList(1L, 2L),
                "asyncPostLoader", new AsyncPostResourceAssembler());
        GlResult<List<PostResource>> result = future.join();
        PostResource resource1 = result.result().get(0);
        PostResource resource2 = result.result().get(1);
        assertEquals("you", resource2.author.name);
        assertEquals("me", resource1.author.name);
        assertEquals("/rest/1", resource1.path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), resource1.date);
        assertEquals(2, graphLoader.statistics().batchInvokeCount());
    }

    /**
     * Test resolveValues().
     */
    @Test
    void test_resolve_values() {
        Post post1 = PostRepository.load(new LinkedHashSet<Long>(Arrays.asList(new Long(1))))
                .get(1l);
        Post post2 = PostRepository.load(new LinkedHashSet<Long>(Arrays.asList(new Long(2))))
                .get(2l);
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<List<PostResource>> result = graphLoader.resolveValues(Arrays.asList(post1, post2), new PostResourceAssembler());
        PostResource resource1 = result.result().get(0);
        PostResource resource2 = result.result().get(1);
        assertEquals("you", resource2.author.name);
        assertEquals("me", resource1.author.name);
        assertEquals("/rest/1", resource1.path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), resource1.date);
        assertEquals(1, graphLoader.statistics().batchInvokeCount());   // Only authors.
    }
    /**
     * Test resolveValuesAsync().
     */
    @Test
    void test_resolve_values_async() {
        Post post1 = PostRepository.load(new LinkedHashSet<Long>(Arrays.asList(new Long(1))))
                .get(1l);
        Post post2 = PostRepository.load(new LinkedHashSet<Long>(Arrays.asList(new Long(2))))
                .get(2l);
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        AsyncGraphLoader graphLoader = graphLoaderFactory.asyncGraphLoader(context);
        CompletableFuture<GlResult<List<PostResource>>> futureResult = graphLoader.resolveValues(Arrays.asList(post1, post2),
                new AsyncPostResourceAssembler());
        GlResult<List<PostResource>> result = futureResult.join();
        PostResource resource1 = result.result().get(0);
        PostResource resource2 = result.result().get(1);
        assertEquals("you", resource2.author.name);
        assertEquals("me", resource1.author.name);
        assertEquals("/rest/1", resource1.path);
        assertEquals(italyDateTimeFormatter.format(PrepareData.post1.date), resource1.date);
        assertEquals(1, graphLoader.statistics().batchInvokeCount());   // Only authors.
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

    /**
     * Test resume after exception.
     */
    @Test
    void test_resume_after_exception() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "postLoader",
                new ExceptionPostResourceAssembler());
        assertTrue(result.exception() instanceof RuntimeException);
        assertEquals(true, graphLoader.lastAbortAll());
        assertEquals(1, graphLoader.lastResetPendingLoads());
        result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "postLoader", new PostResourceAssembler());
        assertNull(result.exception());
    }

    /**
     * Test {@link com.github.mar9000.graphloader.exceptions.GlLoaderNotFoundException}.
     */
    @Test
    void test_loader_not_found_exception() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "x", null);
        assertTrue(result.exception() instanceof GlLoaderNotFoundException);
    }
    /**
     * Test resolve() with dataloader using parent id.
     */
    @Test
    void test_resolve_with_data_loader_by_parent_id() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GraphLoader graphLoader = graphLoaderFactory.graphLoader(context);
        GlResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssemblerWithComments());
        assertEquals(3, result.result().comments.size());
    }
}
