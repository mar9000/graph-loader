package com.github.mar9000.graphloader.test.resources;

/**
 * @author ML
 * @since 1.0.0
 */
public class CommentResource {
    public String text;
    public UserResource author;
    public String date;
    public CommentResource(String text) {
        this.text = text;
    }
}
