package com.mgl.demo.popularpurchases.server.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mgl.demo.popularpurchases.dawps.client.UserNotFoundException;
import com.mgl.demo.popularpurchases.server.model.PopularPurchase;
import com.mgl.demo.popularpurchases.server.service.PopularPurchasesService;

@Path("/recent_purchases")
@Produces({MediaType.APPLICATION_JSON})
public class PopularPurchasesResource {

    @Inject private PopularPurchasesService service;

    @Context private Request request;

    @GET
    @Path("/{username}")
    public Response getMostPopularPurchases(
            @PathParam("username")
            final String username,
            @QueryParam("limit") @DefaultValue("5")
            final int limit) 
    throws UserNotFoundException {
        // Get the list from our service and build a Java-Generics JAX-RS friendly entity.
        List<PopularPurchase> purchases = service.getMostPopularPurchases(username, limit);
        GenericEntity<List<PopularPurchase>> entity =
                new GenericEntity<List<PopularPurchase>>(purchases) {};

        // I can rely on the per-element dependent hashCode List contract:
        //   http://docs.oracle.com/javase/8/docs/api/java/util/List.html#hashCode
        EntityTag etag = new EntityTag(Integer.toString(purchases.hashCode()));

        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoTransform(true);
        // I am not using age-based caching, just ETag-based.
        // cacheControl.setMaxAge((int) SECONDS.convert(1, DAYS));

        ResponseBuilder maybeResponse = request.evaluatePreconditions(etag);
        if (maybeResponse == null) {
            return Response.ok(entity).tag(etag).cacheControl(cacheControl).build();
        } else {
            return maybeResponse.cacheControl(cacheControl).build();
        }
    }

}
