package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.GLAssembler;
import com.github.mar9000.graphloader.GLAssemblerContext;
import com.github.mar9000.graphloader.test.data.Post;
import com.github.mar9000.graphloader.test.data.User;

public class ExceptionPostResourceAssembler implements GLAssembler<Post, PostResource> {
    private GLAssembler<User, UserResource> authorAssembler = new UserResourceAssembler();
    private PostResourceAssembler postResourceAssembler = new PostResourceAssembler();
    @Override
    public PostResource assemble(Post post, GLAssemblerContext context) {
        if (post.id == 2)
            throw new RuntimeException();
        else
            return postResourceAssembler.assemble(post, context);
    }
}
