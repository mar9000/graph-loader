package com.github.mar9000.graphloader.test.data;

import java.time.LocalDateTime;

/**
 * @author ML
 * @since 1.0.0
 */
public class PrepareData {
    public static Post post1;
    public static Post post2;
    public static void defaultData() {
        LocalDateTime july6th = LocalDateTime.of(2020, 7, 6, 12, 12);
        UserRepository.clear();
        PostRepository.clear();
        CommentRepository.clear();
        User me = UserRepository.add("me");
        User you = UserRepository.add("you");
        post1 = PostRepository.add(me.id, "post 1 of me", july6th);
        post2 = PostRepository.add(you.id, "post 2 of you", july6th);
        Post post2me = PostRepository.add(me.id, "post 2 of me", july6th);
        Post post3me = PostRepository.add(me.id, "post 3 of me", july6th);
        Post post1you = PostRepository.add(you.id, "post 1 of you", july6th);
        Post post2you = PostRepository.add(you.id, "post 2 of you", july6th);
        post1.addComment(you.id, "c1 by y p1", july6th);
        post1.addComment(you.id, "c2 by y p1", july6th);
        post1.addComment(me.id, "c1 by m p1", july6th);
    }
}
