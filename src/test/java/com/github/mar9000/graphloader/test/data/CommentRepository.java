package com.github.mar9000.graphloader.test.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ML
 * @since 1.0.0
 */
public class CommentRepository {
    public static Map<Long, Comment> comments = new HashMap<>();
    private static long nextId = 1;
    public static Comment add(long postId, long authorId, String text, LocalDateTime date) {
        Comment comment = new Comment(nextId++, text, authorId, postId, date);
        comments.put(comment.id, comment);
        return comment;
    }
    public static void clear() {
        comments.clear();
    }
}
