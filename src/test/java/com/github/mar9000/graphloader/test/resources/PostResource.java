package com.github.mar9000.graphloader.test.resources;

import java.util.List;

/**
 * @author ML
 * @since 1.0.0
 */
public class PostResource {
    public String text = null;
    public UserResource author = null;
    public List<CommentResource> comments = null;
    public String date;
    public String path;
}
