package com.github.mar9000.graphloader.test.data;

import com.github.mar9000.graphloader.MappedBatchLoader;
import com.github.mar9000.graphloader.MappedBatchLoaderContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PostDataLoader implements MappedBatchLoader<Long, Post> {
    @Override
    public Map<Long, Post> load(Set keys, MappedBatchLoaderContext context) {
        Map<Long, Post> result = new HashMap<>();
        PostRepository.posts.values().forEach(p -> {
            if (keys.contains(p.id))
                result.put(p.id, p);
        });
        return result;
    }
}
