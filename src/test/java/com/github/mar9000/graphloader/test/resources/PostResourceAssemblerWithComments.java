package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.assembler.GlAssemblerContext;
import com.github.mar9000.graphloader.loader.DataLoader;
import com.github.mar9000.graphloader.test.data.Comment;
import com.github.mar9000.graphloader.test.data.Post;

import java.util.ArrayList;
import java.util.List;

public class PostResourceAssemblerWithComments extends PostResourceAssembler {
    @Override
    public PostResource assemble(Post post, GlAssemblerContext context) {
        PostResource resource = super.assemble(post, context);
        resource.comments = new ArrayList<>();
        DataLoader<Long, List<Comment>> commentsLoader = context.registry().loader("commentByPostIdLoader", context.loaderContext());
        commentsLoader.load(post.id, comments -> {
            comments.forEach(comment -> resource.comments.add(new CommentResource(comment.text)));
        });
        return resource;
    }
}
