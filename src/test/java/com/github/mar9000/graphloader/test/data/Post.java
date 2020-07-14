package com.github.mar9000.graphloader.test.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ML
 * @since 1.0.0
 */
public class Post {
    public final long id;
    public final String content;
    public final long authorId;
    private List<Comment> comments = new ArrayList<>();
    public final LocalDateTime date;
    public Post(long id, String content, long authorId, LocalDateTime date) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.date = date;
    }
    public Comment addComment(long authorId, String text, LocalDateTime date) {
        Comment comment = CommentRepository.add(id, authorId, text, date);
        comments.add(comment);
        return comment;
    }
}
