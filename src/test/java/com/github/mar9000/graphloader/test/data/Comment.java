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
