package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.MappedDataLoader;
import com.github.mar9000.graphloader.GLAssembler;
import com.github.mar9000.graphloader.GLAssemblerContext;
import com.github.mar9000.graphloader.test.data.Post;
import com.github.mar9000.graphloader.test.data.User;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class PostResourceAssembler implements GLAssembler<Post, PostResource> {
    private GLAssembler<User, UserResource> authorAssembler = new UserResourceAssembler();
    @Override
    public PostResource assemble(Post post, GLAssemblerContext context) {
        MappedDataLoader<Long, User> authorLoader = context.registry().loader("userLoader");
        PostResource resource = new PostResource();
        resource.text = post.content;
        Locale locale = ((LocaleExecutionContext)context.executionContext()).locale;
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale);
        resource.date = post.date.format(dtf);
        resource.path = ((ServerContext)context.glContext().context()).severPath+"/"+post.id;
        authorLoader.load(post.authorId, user -> resource.author = authorAssembler.assemble(user, context));
        return resource;
    }
}
