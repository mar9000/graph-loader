package com.github.mar9000.graphloader.test.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ML
 * @since 1.0.0
 */
public class UserRepository {
    public static Map<Long, User> users = new HashMap<>();
    private static long nextId = 1;
    public static User add(String name) {
        User user = new User(nextId++, name);
        users.put(user.id, user);
        return user;
    }
    public static User get(long id) {
        return users.get(id);
    }
    public static void clear() {
        users.clear();
    }
}
