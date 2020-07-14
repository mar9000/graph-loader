package com.github.mar9000.graphloader.test.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ML
 * @since 1.0.0
 */
public class PostRepository {
    public static Map<Long, Post> posts = new HashMap<>();
    private static long nextId = 1;
    public static Post add(long authorId, String text, LocalDateTime date) {
        Post post = new Post(nextId++, text, authorId, date);
        posts.put(post.id, post);
        return post;
    }
    public static void clear() {
        posts.clear();
    }
    public static Map<Long, Post> load(Set<Long> keys) {
        Map<Long, Post> result = new HashMap<>();
        posts.values().forEach(p -> {
            if (keys.contains(p.id))
                result.put(p.id, p);
        });
        return result;
    }
}
