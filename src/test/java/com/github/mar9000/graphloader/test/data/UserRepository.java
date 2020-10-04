package com.github.mar9000.graphloader.test.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author ML
 * @since 1.0.0
 */
public class UserRepository {
    private static Map<Long, User> users = new HashMap<>();
    private static long nextId = 1;
    public static User add(String name) {
        User user = new User(nextId++, name);
        users.put(user.id, user);
        return user;
    }
    /* Don't want to ask for each id, resolve ids in batch by design (2020-09-23).
    public static User get(long id) {
        return users.get(id);
    }
     */
    public static List<User> get(Set<Long> ids) {
        return ids.stream()
                .map(id -> users.get(id))
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }
    public static CompletableFuture<List<User>> getAsync(Set<Long> ids) {
        return CompletableFuture.supplyAsync(() -> {
            return ids.stream()
                    .map(id -> users.get(id))
                    .filter(user -> user != null)
                    .collect(Collectors.toList());
        });
    }
    public static void clear() {
        users.clear();
    }
}
