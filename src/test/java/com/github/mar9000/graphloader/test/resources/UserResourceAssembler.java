package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.DataLoaderRegistry;
import com.github.mar9000.graphloader.GLAssembler;
import com.github.mar9000.graphloader.GLAssemblerContext;
import com.github.mar9000.graphloader.test.data.User;

public class UserResourceAssembler implements GLAssembler<User, UserResource> {
    @Override
    public UserResource assemble(User value, GLAssemblerContext context) {
        UserResource result = new UserResource();
        result.name = value.name;
        return result;
    }
}
