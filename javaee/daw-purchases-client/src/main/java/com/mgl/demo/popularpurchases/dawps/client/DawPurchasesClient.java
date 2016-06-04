package com.mgl.demo.popularpurchases.dawps.client;

import java.util.List;

import com.mgl.demo.popularpurchases.dawps.client.model.Product;
import com.mgl.demo.popularpurchases.dawps.client.model.Purchase;
import com.mgl.demo.popularpurchases.dawps.client.model.User;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support.NothingWrappedException;

public class DawPurchasesClient {

    public static final boolean USE_HTTP = false;
    public static final boolean USE_HTTPS = true;

    // TODO: these defaults should be make configurable and passed in via a
    // configuration object or similar in the constructor.
    private static final int GLOBAL_TIMEOUT = 15_000;
    private static final int GLOBAL_MAX_CONNS = 1;
    private static final int CONNECTION_TTL = 0; // Effectively closing TCP socket after use.
    private static final int GET_CONNECTION_TIMEOUT = GLOBAL_TIMEOUT * 2;

    public static final int NO_LIMIT = Integer.MAX_VALUE;

    private final ResteasyClient jaxRsClient;
    private final ApiSpec apiClientProxy;

    protected DawPurchasesClient() {
        // So that I can be a CDI proxyable bean: requries empty constructor
        this.jaxRsClient = null;
        this.apiClientProxy = null;
    }

    public DawPurchasesClient(boolean useHttps, String hostname, int port) {
        checkNotNull(hostname, "hostname");
        checkArgument(port > 0, "Invalid port %s", port);
        String apiHostUrl = String.format("%s://%s:%d", useHttps ? "https" : "http", hostname, port);

        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        clientBuilder.establishConnectionTimeout(GLOBAL_TIMEOUT, TimeUnit.MILLISECONDS);
        clientBuilder.socketTimeout(GLOBAL_TIMEOUT, TimeUnit.MILLISECONDS);
        clientBuilder.maxPooledPerRoute(GLOBAL_MAX_CONNS);
        clientBuilder.connectionPoolSize(GLOBAL_MAX_CONNS);
        clientBuilder.connectionTTL(CONNECTION_TTL, TimeUnit.MILLISECONDS);
        clientBuilder.connectionCheckoutTimeout(GET_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        jaxRsClient = clientBuilder.build();
        ResteasyWebTarget target = jaxRsClient.target(apiHostUrl);

        // TODO: potential improvements here:
        //   - Avoid "useHttps" boolean parameter - enum instead

        apiClientProxy = target.proxy(ApiSpec.class);
    }

    public void dispose() {
        jaxRsClient.close();
    }

    // Just delegate to our API spec. methods.

    public List<User> getUsers(int limit) {
        return apiClientProxy.getWrappedUsers(limit).unwrap();
    }

    public User getUser(String username) throws UserNotFoundException {
        try {
            return apiClientProxy.getWrappedUser(username).unwrap();
        } catch (NothingWrappedException e) {
            throw new UserNotFoundException(username);
        }
    }

    public List<Product> getProducts(int limit) {
        return apiClientProxy.getWrappedProducts(limit).unwrap();
    }

    public Product getProduct(long productId) throws ProductNotFoundException {
        try {
            return apiClientProxy.getWrappedProduct(productId).unwrap();
        } catch (NothingWrappedException e) {
            throw new ProductNotFoundException(productId);
        }
    }

    public List<Purchase> getPurchasesByUser(String username, int limit) {
        return apiClientProxy.getPurchasesByUser(username, limit).unwrap();
    }

    public List<Purchase> getPurchasesByProduct(long productId, int limit) {
        return apiClientProxy.getPurchasesByProduct(productId, limit).unwrap();
    }

}
