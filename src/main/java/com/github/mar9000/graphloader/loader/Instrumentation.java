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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Track pending loads to be use to trigger dispatch().
 * @author ML
 * @since 1.0.1
 */
public class Instrumentation {
    private AtomicInteger pendingLoads = new AtomicInteger(0);
    public int pendingLoads() {
        return pendingLoads.get();
    }
    /* package */ void incPendingLoads() {
        pendingLoads.incrementAndGet();
    }
    public void resetPendingLoads() {
        pendingLoads.set(0);
    }
}
