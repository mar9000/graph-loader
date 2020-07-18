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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author ML
 * @since 1.0.0
 */
public class GraphLoaderTests {

    private static GraphLoader graphLoader;

    @BeforeAll
    static void init() {
        // Prepare objects that span multiple GL.resolve() invocations.
        PrepareData.defaultData();
        MappedBatchLoaderRegistry registry = new MappedBatchLoaderRegistry();
        registry.register("postLoader", new PostDataLoader());
        registry.register("exceptionPostLoader", new ExceptionPostDataLoader());
        registry.register("userLoader", new UserDataLoader());
        graphLoader = new GraphLoader(registry, new GLContext(new ServerContext("/rest")));
    }

    /**
     * Test resolve() one resource with different execution contexts.
     */
    @Test
    void test_item_load() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GLResult<PostResource> result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler(), context);
        assertEquals("me", result.result().author.name);
        assertEquals("/rest/1", result.result().path);
        assertEquals("06/07/20 12.12", result.result().date);

        // Second execution.
        context = new LocaleExecutionContext(Locale.US);
        result = graphLoader.resolve(1L, "postLoader", new PostResourceAssembler(), context);
        assertEquals("/rest/1", result.result().path);
        assertEquals("7/6/20 12:12 PM", result.result().date);
    }

    /**
     * Test resolve() a list of resource.
     */
    @Test
    void test_list_load() {
        // First execution.
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GLResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "postLoader", new PostResourceAssembler(), context);
        PostResource resource1 = result.result().get(0);
        PostResource resource2 = result.result().get(1);
        assertEquals("you", resource2.author.name);
        assertEquals("me", resource1.author.name);
        assertEquals("/rest/1", resource1.path);
        assertEquals("06/07/20 12.12", resource1.date);
        assertEquals(2, result.state().batchedLoadCount);
    }

    /**
     * Test exception in loader.
     */
    @Test
    void test_exception_in_assembler() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GLResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "exceptionPostLoader",
                new PostResourceAssembler(), context);
        assertTrue(result.exception() instanceof RuntimeException);
    }

    /**
     * Test exception in assembler.
     */
    @Test
    void test_exception_in_loader() {
        ExecutionContext context = new LocaleExecutionContext(Locale.ITALY);
        GLResult<List<PostResource>> result = graphLoader.resolveMany(Arrays.asList(1L, 2L), "postLoader",
                new ExceptionPostResourceAssembler(), context);
        assertTrue(result.exception() instanceof RuntimeException);
    }
}
