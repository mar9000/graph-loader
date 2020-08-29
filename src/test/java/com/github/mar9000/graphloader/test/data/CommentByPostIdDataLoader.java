package com.github.mar9000.graphloader.test.data;

import com.github.mar9000.graphloader.batch.MappedBatchLoader;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Example of parent - children dataloader using Comment.postId parent key.
 */
public class CommentByPostIdDataLoader implements MappedBatchLoader<Long, List<Comment>> {
    @Override
    public Map<Long, List<Comment>> load(Set<Long> keys, MappedBatchLoaderContext context) {
        List<Comment> comments = CommentRepository.loadByPostIds(keys);
        return comments.stream().collect(Collectors.groupingBy(c -> c.postId));
    }
}
