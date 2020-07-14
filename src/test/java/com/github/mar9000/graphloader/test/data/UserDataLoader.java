package com.github.mar9000.graphloader.test.data;

import com.github.mar9000.graphloader.ExecutionContext;
import com.github.mar9000.graphloader.MappedBatchLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserDataLoader implements MappedBatchLoader<Long, User> {
    @Override
    public Map<Long, User> load(Set keys, ExecutionContext context) {
        Map<Long, User> result = new HashMap<>();
        UserRepository.users.forEach((k,v) -> {
            if (keys.contains(k))
                result.put(k, v);
        });
        return result;
    }
}
