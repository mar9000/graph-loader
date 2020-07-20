package com.github.mar9000.graphloader.test.data;

import com.github.mar9000.graphloader.batch.MappedBatchLoader;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExceptionPostDataLoader implements MappedBatchLoader<Long, Post> {
    @Override
    public Map<Long, Post> load(Set keys, MappedBatchLoaderContext context) {
        if (keys.contains(Long.valueOf(2)))
            throw new RuntimeException();
        Map<Long, Post> result = new HashMap<>();
        PostRepository.posts.values().forEach(p -> {
            if (keys.contains(p.id))
                result.put(p.id, p);
        });
        return result;
    }
}
