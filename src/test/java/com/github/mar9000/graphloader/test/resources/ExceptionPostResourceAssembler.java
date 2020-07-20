package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.assembler.GlAssembler;
import com.github.mar9000.graphloader.assembler.GlAssemblerContext;
import com.github.mar9000.graphloader.test.data.Post;
import com.github.mar9000.graphloader.test.data.User;

public class ExceptionPostResourceAssembler implements GlAssembler<Post, PostResource> {
    private GlAssembler<User, UserResource> authorAssembler = new UserResourceAssembler();
    private PostResourceAssembler postResourceAssembler = new PostResourceAssembler();
    @Override
    public PostResource assemble(Post post, GlAssemblerContext context) {
        if (post.id == 2)
            throw new RuntimeException();
        else
            return postResourceAssembler.assemble(post, context);
    }
}
