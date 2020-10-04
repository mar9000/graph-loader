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
import com.github.mar9000.graphloader.loader.MappedDataLoader;
import com.github.mar9000.graphloader.test.resources.LocaleExecutionContext;
import graphql.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @author ML
 * @since 1.0.2
 */
public class MappedDataLoaderTests {
    private MappedBatchLoader<Long, String> batchLoader;
    private MappedBatchLoaderContext loaderContext;
    private ExecutionContext executionContext;
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
    }
    @Test
    public void test_load_dispatch() {
        MappedDataLoader<Long, String> mappedLoader = new MappedDataLoader<>(batchLoader, loaderContext);
        Assert.assertFalse(mappedLoader.dispatch().isPresent());

        final List<String> result = new ArrayList<>();
        mappedLoader.load(1l, result::add);
        mappedLoader.load(2l, result::add);
        Assertions.assertTrue(mappedLoader.abortPending());

        mappedLoader.load(12l, result::add);
        mappedLoader.load(13l, result::add);
        mappedLoader.dispatch();
        Assertions.assertEquals("12", result.get(0));
        Assertions.assertEquals("13", result.get(1));
    }
}
