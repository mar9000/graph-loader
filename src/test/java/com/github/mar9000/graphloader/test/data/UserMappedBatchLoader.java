package com.github.mar9000.graphloader.test.data;

import com.github.mar9000.graphloader.batch.MappedBatchLoader;
import com.github.mar9000.graphloader.batch.MappedBatchLoaderContext;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMappedBatchLoader implements MappedBatchLoader<Long, User> {
    @Override
    public Map<Long, User> load(Set<Long> keys, MappedBatchLoaderContext context) {
        return UserRepository.get(keys).stream()
                .collect(Collectors.toMap(user -> user.id, user -> user));
    }
}
