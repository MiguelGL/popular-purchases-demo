package com.mgl.demo.popularpurchases.server.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Charsets;
import com.mgl.demo.popularpurchases.dawps.client.ProductNotFoundException;

@Provider
public class ProductNotFoundExceptionMapper implements ExceptionMapper<ProductNotFoundException> {

    @Override
    public Response toResponse(ProductNotFoundException exception) {
        return Response
                .status(Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .encoding(Charsets.UTF_8.name())
                .entity(String.format(
                            "Product with id of '%d' was not found",
                            exception.getProductId()))
                .build();
    }

}
