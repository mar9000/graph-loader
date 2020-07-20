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
 * The result returned by a resolveXXX() invocation. A value of type D or a {@link Throwable} is returned.
 * All exceptions are caught.
 * @author ML
 * @since 1.0.0
 */
public class GlResult<D> {
    private D result;
    private Throwable exception;
    public D result() {
        return result;
    }
    public void result(D result) {
        this.result = result;
    }
    public Throwable exception() {
        return exception;
    }
    public void exception(Throwable exception) {
        this.exception = exception;
    }
}
