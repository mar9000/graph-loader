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
package com.github.mar9000.graphloader.test.data;

import java.time.LocalDateTime;

/**
 * @author ML
 * @since 1.0.0
 */
public class Comment {
    public final long id;
    public final String text;
    public final long authorId;
    public final long postId;
    public final LocalDateTime date;
    public Comment(long id, String text, long authorId, long postId, LocalDateTime date) {
        this.id = id;
        this.text = text;
        this.authorId = authorId;
        this.postId = postId;
        this.date = date;
    }
}
