/*
 * Copyright 2020 Marco Lombardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.assembler.GlAssembler;
import com.github.mar9000.graphloader.assembler.GlAssemblerContext;
import com.github.mar9000.graphloader.loader.DataLoader;
import com.github.mar9000.graphloader.test.data.Post;
import com.github.mar9000.graphloader.test.data.User;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * @author ML
 * @since 1.0.2
 */
public class AsyncPostResourceAssembler implements GlAssembler<Post, PostResource> {
    private GlAssembler<User, UserResource> authorAssembler = new UserResourceAssembler();
    @Override
    public PostResource assemble(Post post, GlAssemblerContext context) {
        DataLoader<Long, User> authorLoader = context.registry().loader("asyncUserLoader", context.loaderContext());
        PostResource resource = new PostResource();
        resource.text = post.content;
        Locale locale = ((LocaleExecutionContext)context.loaderContext().executionContext()).locale;
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale);
        resource.date = post.date.format(dtf);
        ServerContext serverContext = context.loaderContext().glContext().context();
        resource.path = serverContext.severPath+"/"+post.id;
        authorLoader.load(post.authorId, user -> resource.author = authorAssembler.assemble(user, context));
        return resource;
    }
}
