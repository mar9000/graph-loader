package com.github.mar9000.graphloader.test.data;

import java.time.LocalDateTime;
import java.util.*;

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

    /**
     * Here we use a List of Comment intentionally because there is no requirement on the shape of a repository result.
     * The only thing to implement is batch loading.
     */
    public static List<Comment> loadByPostIds(Set<Long> postKeys) {
        List<Comment> result = new ArrayList<>();
        comments.values().forEach(comment -> {
            if (postKeys.contains(comment.postId))
                result.add(comment);
        });
        return result;
    }
    public static void clear() {
        comments.clear();
    }
}
