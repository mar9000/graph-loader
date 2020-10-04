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
package com.github.mar9000.graphloader.loader;

import com.github.mar9000.graphloader.stats.Statistics;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 * @author ML
 * @since 1.0.0
 */
public interface DataLoader<K, V> {
    /**
     * Request to load a key at some point in time in the future and pass a Consumer to be called when
     * the value V for the passed key will be loaded.
     */
    void load(K key, Consumer<V> consumer);
    /**
     * Dispatch the loading operation.
     * @return an empty {@link Optional} if the loader is not async.
     */
    Optional<CompletionStage<?>> dispatch();
    /**
     * Abort any pending operation.
     * @return true if there was pending consumers.
     */
    boolean abortPending();
    /**
     * {@link Statistics} for this loader.
     */
    Statistics statistics();
}
