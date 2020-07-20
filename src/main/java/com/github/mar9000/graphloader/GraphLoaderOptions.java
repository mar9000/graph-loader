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

/**
 * Options for a {@link GraphLoader} instance.
 * @author ML
 * @since 1.0.1
 */
public class GraphLoaderOptions {
    private boolean cachingEnabled = false;
    public boolean cachingEnabled() {
        return this.cachingEnabled;
    }
    public GraphLoaderOptions cachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;
        return this;
    }
}
