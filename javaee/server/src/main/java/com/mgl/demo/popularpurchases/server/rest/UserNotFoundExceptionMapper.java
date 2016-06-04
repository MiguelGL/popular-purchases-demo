package com.mgl.demo.popularpurchases.server.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Charsets;
import com.mgl.demo.popularpurchases.dawps.client.UserNotFoundException;

@Provider
public class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {

    @Override
    public Response toResponse(UserNotFoundException exception) {
        return Response
                .status(Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .encoding(Charsets.UTF_8.name())
                .entity(String.format(
                            "User with username of '%s' was not found",
                            exception.getUsername()))
                .build();
    }

}
