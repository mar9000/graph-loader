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
package com.github.mar9000.graphloader.test.loader;

import com.github.mar9000.graphloader.GlContextHolder;
import com.github.mar9000.graphloader.batch.MappedBatchLoader;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;
import com.github.mar9000.graphloader.loader.ExecutionContext;
import com.github.mar9000.graphloader.loader.Instrumentation;
import com.github.mar9000.graphloader.loader.InstrumentedDataLoader;
import com.github.mar9000.graphloader.test.resources.LocaleExecutionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @author ML
 * @since 1.0.2
 */
public class InstrumentedDataLoaderTests {
    private MappedBatchLoader<Long, String> batchLoader;
    private MappedBatchLoaderContext loaderContext;
    private ExecutionContext executionContext;
    private Instrumentation instrumentation;
    @BeforeEach
    public void beforeEach() {
        batchLoader = new MappedBatchLoader<Long, String>() {
            @Override
            public Map<Long, String> load(Set<Long> keys, MappedBatchLoaderContext context) {
                Map<Long, String> result = new LinkedHashMap<>();
                keys.forEach(k -> result.put(k, String.valueOf(k)));
                return result;
            }
        };
        executionContext = new LocaleExecutionContext(Locale.CANADA);
        loaderContext = new MappedBatchLoaderContext(new GlContextHolder("ctx"), executionContext);
        instrumentation = new Instrumentation();
    }
    @Test
    public void test_load_dispatch() {
        InstrumentedDataLoader<Long, String> mappedLoader = new InstrumentedDataLoader<>(batchLoader, instrumentation,
                false, loaderContext);
        Assertions.assertEquals(0, instrumentation.pendingLoads());

        final List<String> result = new ArrayList<>();
        mappedLoader.load(1l, result::add);
        mappedLoader.load(2l, result::add);
        Assertions.assertEquals(2, instrumentation.pendingLoads());
        Assertions.assertEquals(0, mappedLoader.statistics().batchLoadCount());

        instrumentation.resetPendingLoads();   // This is the event that triggers dispatch().
        mappedLoader.dispatch();
        Assertions.assertEquals(0, instrumentation.pendingLoads());
        Assertions.assertEquals(1, mappedLoader.statistics().batchInvokeCount());
        Assertions.assertEquals(2, mappedLoader.statistics().batchLoadCount());

        mappedLoader.dispatch();   // Nothing to do, same stats.
        Assertions.assertEquals(0, instrumentation.pendingLoads());
        Assertions.assertEquals(2, mappedLoader.statistics().batchLoadCount());
        Assertions.assertEquals(2, result.size());

        // Same load without cache.
        mappedLoader.load(1l, result::add);
        mappedLoader.dispatch();
        Assertions.assertEquals(2+1, mappedLoader.statistics().batchLoadCount());
        Assertions.assertEquals(3, result.size());
    }
    @Test
    public void test_load_dispatch_with_cache() {
        InstrumentedDataLoader<Long, String> mappedLoader = new InstrumentedDataLoader<>(batchLoader, instrumentation,
                true, loaderContext);
        Assertions.assertEquals(0, instrumentation.pendingLoads());
        int batchLoadCount = 0;
        int batchInvokeCount = 0;

        final List<String> result = new ArrayList<>();
        mappedLoader.load(1l, result::add);
        mappedLoader.load(2l, result::add);
        batchLoadCount += 2;
        batchInvokeCount++;
        mappedLoader.dispatch();
        Assertions.assertEquals(batchInvokeCount, mappedLoader.statistics().batchInvokeCount());
        Assertions.assertEquals(batchLoadCount, mappedLoader.statistics().batchLoadCount());
        Assertions.assertEquals(2, result.size());

        instrumentation.resetPendingLoads();
        mappedLoader.load(1l, result::add);   // Same keys, no new load.
        mappedLoader.load(2l, result::add);
        mappedLoader.dispatch();
        Assertions.assertEquals(0, instrumentation.pendingLoads());
        Assertions.assertEquals(batchInvokeCount, mappedLoader.statistics().batchInvokeCount());
        Assertions.assertEquals(batchLoadCount, mappedLoader.statistics().batchLoadCount());
        Assertions.assertEquals(4, result.size());

        mappedLoader.load(10l, result::add);   // New keys, new load.
        mappedLoader.load(20l, result::add);
        batchLoadCount += 2;
        batchInvokeCount++;
        mappedLoader.dispatch();
        Assertions.assertEquals(2, instrumentation.pendingLoads());
        Assertions.assertEquals(batchInvokeCount, mappedLoader.statistics().batchInvokeCount());
        Assertions.assertEquals(batchLoadCount, mappedLoader.statistics().batchLoadCount());
        Assertions.assertEquals(6, result.size());
    }
}
