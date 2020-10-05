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
import com.github.mar9000.graphloader.exceptions.GlPendingLoadsException;
import com.github.mar9000.graphloader.loader.Instrumentation;
import com.github.mar9000.graphloader.loader.InstrumentedDataLoaderRegistry;
import com.github.mar9000.graphloader.stats.Statistics;

/**
 * Base class to resolve a single result of type D or a list of it.
 * @author ML
 * @since 1.0.3
 */
public class AbstractGraphLoader {
    protected final Instrumentation instrumentation;
    protected final InstrumentedDataLoaderRegistry instrumentedRegistry;
    protected boolean lastAbortAll = false;
    protected int lastResetPendingLoads = 0;
    protected AbstractGraphLoader(MappedBatchLoaderRegistry registry, GraphLoaderOptions options) {
        this.instrumentation = new Instrumentation();
        this.instrumentedRegistry = new InstrumentedDataLoaderRegistry(registry, this.instrumentation);
        this.instrumentedRegistry.cachingEnabled(options.cachingEnabled());
    }
    protected void resolvePreconditions() {
        if (this.instrumentation.pendingLoads() != 0)
            throw new GlPendingLoadsException("pendingLoads: " + this.instrumentation.pendingLoads());
    }
    protected void resolvePostconditions() {
        this.lastAbortAll = this.instrumentedRegistry.abortAll();
        this.lastResetPendingLoads = this.instrumentation.pendingLoads();
        this.instrumentation.resetPendingLoads();
    }
    public Statistics statistics() {
        return instrumentedRegistry.statistics();
    }
    public boolean lastAbortAll() {
        return this.lastAbortAll;
    }
    public int lastResetPendingLoads() {
        return this.lastResetPendingLoads;
    }
}
