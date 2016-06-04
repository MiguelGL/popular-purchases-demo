package com.mgl.demo.popularpurchases.dawps.client;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.ProductWrapper;
import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.ProductsWrapper;
import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.PurchasesWrapper;
import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.UsersWrapper;
import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support.UserWrapper;

@Path("/api")
@Produces({MediaType.APPLICATION_JSON})
public interface ApiSpec {

    // I am putting everything into a single interface because there are just a few methods.
    // More canonically this would be split into different "users", "purchases" and "products"
    // dedicated resources (specs.)

    // TODO: Consider using GenericType<List<...>> instead of arrays. But this really would
    // break our convenient client proxy usage delegating to these methods.

    static final int DEFAULT_PAGINATION_LIMIT = 10;

    @GET
    @Path("/users")
    UsersWrapper getWrappedUsers(
            @QueryParam("limit") @DefaultValue("" + DEFAULT_PAGINATION_LIMIT)
            final int limit);

    @GET
    @Path("/users/{username}")
    UserWrapper getWrappedUser(
            @PathParam("username")
            final String username);

    @GET
    @Path("/products")
    ProductsWrapper getWrappedProducts(
            @QueryParam("limit") @DefaultValue("" + DEFAULT_PAGINATION_LIMIT)
            final int limit);

    @GET
    @Path("/products/{product-id}")
    ProductWrapper getWrappedProduct(
            @PathParam("product-id")
            final long productId);

    @GET
    @Path("/purchases/by_user/{username}")
    PurchasesWrapper getPurchasesByUser(
            @PathParam("username")
            final String username,
            @QueryParam("limit") @DefaultValue("" + DEFAULT_PAGINATION_LIMIT)
            final int limit);

    @GET
    @Path("/purchases/by_product/{product-id}")
    PurchasesWrapper getPurchasesByProduct(
            @PathParam("product-id")
            final long productId,
            @QueryParam("limit") @DefaultValue("" + DEFAULT_PAGINATION_LIMIT)
            final int limit);

}
