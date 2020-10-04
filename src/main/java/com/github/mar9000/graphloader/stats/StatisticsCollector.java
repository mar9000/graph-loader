/*
 * From https://github.com/graphql-java/java-dataloader/tree/v2.2.3
 * Licensed under Apache License v2.0, see http://www.opensource.org/licenses/apache2.0.php
 * Copyright 2016 Arnold Schrijver, 2017 Brad Baker and others contributors.
 */
package com.github.mar9000.graphloader.stats;

/**
 * This allows statistics to be collected for {@link com.github.mar9000.graphloader.loader.DataLoader} operations
 */
public interface StatisticsCollector {

    /**
     * Called to increment the number of loads
     *
     * @return the current value after increment
     */
    long incrementLoadCount();

    /**
     * Called to increment the number of loads that resulted in an object deemed in error
     *
     * @return the current value after increment
     */
    long incrementLoadErrorCount();

    /**
     * Called to increment the number of batch loads
     *
     * @param delta how much to add to the count
     *
     * @return the current value after increment
     */
    long incrementBatchLoadCountBy(long delta);

    /**
     * Called to reset the number of batch loads
     */
    void resetBatchLoadCount();

    /**
     * Called to increment the number of batch loads exceptions
     *
     * @return the current value after increment
     */
    long incrementBatchLoadExceptionCount();

    /**
     * Called to increment the number of cache hits
     *
     * @return the current value after increment
     */
    long incrementCacheHitCount();

    /**
     * @return the statistics that have been gathered up to this point in time
     */
    Statistics statistics();
}
