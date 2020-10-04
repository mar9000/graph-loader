/*
 * From https://github.com/graphql-java/java-dataloader/tree/v2.2.3
 * Licensed under Apache License v2.0, see http://www.opensource.org/licenses/apache2.0.php
 * Copyright 2016 Arnold Schrijver, 2017 Brad Baker and others contributors.
 */
package com.github.mar9000.graphloader.stats;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This simple collector uses {@link AtomicLong}s to collect
 * statistics
 *
 * @see StatisticsCollector
 */
public class SimpleStatisticsCollector implements StatisticsCollector {
    private final AtomicLong loadCount = new AtomicLong();
    private final AtomicLong batchInvokeCount = new AtomicLong();
    private final AtomicLong batchLoadCount = new AtomicLong();
    private final AtomicLong cacheHitCount = new AtomicLong();
    private final AtomicLong batchLoadExceptionCount = new AtomicLong();
    private final AtomicLong loadErrorCount = new AtomicLong();

    @Override
    public long incrementLoadCount() {
        return loadCount.incrementAndGet();
    }

    @Override
    public long incrementBatchLoadCountBy(long delta) {
        batchInvokeCount.incrementAndGet();
        return batchLoadCount.addAndGet(delta);
    }

    @Override
    public long incrementCacheHitCount() {
        return cacheHitCount.incrementAndGet();
    }

    @Override
    public long incrementLoadErrorCount() {
        return loadErrorCount.incrementAndGet();
    }

    @Override
    public long incrementBatchLoadExceptionCount() {
        return batchLoadExceptionCount.incrementAndGet();
    }

    @Override
    public void resetBatchLoadCount() {
        this.batchLoadCount.set(0);
    }

    @Override
    public Statistics statistics() {
        return new Statistics(loadCount.get(), loadErrorCount.get(), batchInvokeCount.get(),
                batchLoadCount.get(), batchLoadExceptionCount.get(), cacheHitCount.get());
    }

    @Override
    public String toString() {
        return statistics().toString();
    }
}
