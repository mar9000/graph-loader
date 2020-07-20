package com.github.mar9000.graphloader.test.resources;

import com.github.mar9000.graphloader.assembler.GlAssembler;
import com.github.mar9000.graphloader.assembler.GlAssemblerContext;
import com.github.mar9000.graphloader.test.data.User;

public class UserResourceAssembler implements GlAssembler<User, UserResource> {
    @Override
    public UserResource assemble(User value, GlAssemblerContext context) {
        UserResource result = new UserResource();
        result.name = value.name;
        return result;
    }
}
