/*
 * From https://github.com/graphql-java/java-dataloader/tree/v2.2.3
 * Licensed under Apache License v2.0, see http://www.opensource.org/licenses/apache2.0.php
 * Copyright 2016 Arnold Schrijver, 2017 Brad Baker and others contributors.
 */
package com.github.mar9000.graphloader.stats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This holds statistics on how a {@link com.github.mar9000.graphloader.loader.DataLoader} has performed
 */
public class Statistics {

    private final long loadCount;
    private final long loadErrorCount;
    private final long batchInvokeCount;
    private final long batchLoadCount;
    private final long batchLoadExceptionCount;
    private final long cacheHitCount;

    /**
     * Zero statistics
     */
    public Statistics() {
        this(0, 0, 0, 0, 0, 0);
    }

    public Statistics(long loadCount, long loadErrorCount, long batchInvokeCount,
                      long batchLoadCount, long batchLoadExceptionCount, long cacheHitCount) {
        this.loadCount = loadCount;
        this.batchInvokeCount = batchInvokeCount;
        this.batchLoadCount = batchLoadCount;
        this.cacheHitCount = cacheHitCount;
        this.batchLoadExceptionCount = batchLoadExceptionCount;
        this.loadErrorCount = loadErrorCount;
    }

    /**
     * A helper to divide two numbers and handle zero
     *
     * @param numerator   the top bit
     * @param denominator the bottom bit
     *
     * @return numerator / denominator returning zero when denominator is zero
     */
    public double ratio(long numerator, long denominator) {
        return denominator == 0 ? 0f : ((double) numerator) / ((double) denominator);
    }

    /**
     * @return the number of objects {@link com.github.mar9000.graphloader.loader.DataLoader#load(Object, Consumer)}
     * has been asked to load
     */
    public long loadCount() {
        return loadCount;
    }

    /**
     * @return the number of times the {@link com.github.mar9000.graphloader.loader.DataLoader}
     * batch loader function return an specific object that was in error
     */
    public long loadErrorCount() {
        return loadErrorCount;
    }

    /**
     * @return loadErrorCount / loadCount
     */
    public double loadErrorRatio() {
        return ratio(loadErrorCount, loadCount);
    }

    /**
     * @return the number of times the {@link com.github.mar9000.graphloader.loader.DataLoader} batch loader function has been called
     */
    public long batchInvokeCount() {
        return batchInvokeCount;
    }

    /**
     * @return the number of objects that the {@link com.github.mar9000.graphloader.loader.DataLoader} batch loader function has been asked to load
     */
    public long batchLoadCount() {
        return batchLoadCount;
    }

    /**
     * @return batchLoadCount / loadCount
     */
    public double batchLoadRatio() {
        return ratio(batchLoadCount, loadCount);
    }

    /**
     * @return the number of times the DataLoader batch loader function throw an exception when trying to get any values
     */
    public long batchLoadExceptionCount() {
        return batchLoadExceptionCount;
    }

    /**
     * @return batchLoadExceptionCount / loadCount
     */
    public double batchLoadExceptionRatio() {
        return ratio(batchLoadExceptionCount, loadCount);
    }

    /**
     * @return the number of times  DataLoader#load(Object) resulted in a cache hit
     */
    public long cacheHitCount() {
        return cacheHitCount;
    }

    /**
     * @return then number of times we missed the cache during {@link com.github.mar9000.graphloader.loader.DataLoader}
     */
    public long cacheMissCount() {
        return loadCount - cacheHitCount;
    }

    /**
     * @return cacheHits / loadCount
     */
    public double cacheHitRatio() {
        return ratio(cacheHitCount, loadCount);
    }

    /**
     * This will combine this set of statistics with another set of statistics so that they become the combined count of each
     *
     * @param other the other statistics to combine
     *
     * @return a new statistics object of the combined counts
     */
    public Statistics combine(Statistics other) {
        return new Statistics(
                this.loadCount + other.loadCount(),
                this.loadErrorCount + other.loadErrorCount(),
                this.batchInvokeCount + other.batchInvokeCount(),
                this.batchLoadCount + other.batchLoadCount(),
                this.batchLoadExceptionCount + other.batchLoadExceptionCount(),
                this.cacheHitCount + other.cacheHitCount()
        );
    }

    /**
     * @return a map representation of the statistics, perhaps to send over JSON or some such
     */
    public Map<String, Number> toMap() {
        Map<String, Number> stats = new LinkedHashMap<>();
        stats.put("loadCount", loadCount());
        stats.put("loadErrorCount", loadErrorCount());
        stats.put("loadErrorRatio", loadErrorRatio());

        stats.put("batchInvokeCount", batchInvokeCount());
        stats.put("batchLoadCount", batchLoadCount());
        stats.put("batchLoadRatio", batchLoadRatio());
        stats.put("batchLoadExceptionCount", batchLoadExceptionCount());
        stats.put("batchLoadExceptionRatio", batchLoadExceptionRatio());

        stats.put("cacheHitCount", cacheHitCount());
        stats.put("cacheHitRatio", cacheHitRatio());
        return stats;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "loadCount=" + loadCount +
                ", loadErrorCount=" + loadErrorCount +
                ", batchLoadCount=" + batchLoadCount +
                ", batchLoadExceptionCount=" + batchLoadExceptionCount +
                ", cacheHitCount=" + cacheHitCount +
                '}';
    }
}
