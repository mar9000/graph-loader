package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.DataLoader;
import com.github.mar9000.graphloader.GlAssembler;
import com.github.mar9000.graphloader.GlAssemblerContext;
import com.github.mar9000.graphloader.test.data.Post;
import com.github.mar9000.graphloader.test.data.User;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class PostResourceAssembler implements GlAssembler<Post, PostResource> {
    private GlAssembler<User, UserResource> authorAssembler = new UserResourceAssembler();
    @Override
    public PostResource assemble(Post post, GlAssemblerContext context) {
        DataLoader<Long, User> authorLoader = context.registry().loader("userLoader", context.loaderContext());
        PostResource resource = new PostResource();
        resource.text = post.content;
        Locale locale = ((LocaleExecutionContext)context.executionContext()).locale;
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale);
        resource.date = post.date.format(dtf);
        ServerContext serverContext = context.glContext().context();
        resource.path = serverContext.severPath+"/"+post.id;
        authorLoader.load(post.authorId, user -> resource.author = authorAssembler.assemble(user, context));
        return resource;
    }
}
