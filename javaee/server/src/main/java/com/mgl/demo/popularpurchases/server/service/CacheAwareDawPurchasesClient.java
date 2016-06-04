package com.mgl.demo.popularpurchases.server.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import com.mgl.demo.popularpurchases.dawps.client.DawPurchasesClient;
import com.mgl.demo.popularpurchases.dawps.client.ProductNotFoundException;
import com.mgl.demo.popularpurchases.dawps.client.UserNotFoundException;
import com.mgl.demo.popularpurchases.dawps.client.model.Product;
import com.mgl.demo.popularpurchases.dawps.client.model.Purchase;
import com.mgl.demo.popularpurchases.dawps.client.model.User;
import org.infinispan.jcache.annotation.CacheResultInterceptor;
import org.wildfly.swarm.cdi.ConfigValue;

@ApplicationScoped
@CacheDefaults
@Interceptors({CacheResultInterceptor.class})
public class CacheAwareDawPurchasesClient {

    // A smarter, finer grained caching strategy could be implemented for methods
    // taking a limit parameter, but because the consumed API does not have "offset" taking
    // operations, I am sticking to this.

    // Also please note this is a too straightforward cache because no invalidation
    // is taking place. This is because this project use case is very simple and there are
    // no modifying operations happening, nor we have a means to detect that the data
    // from the consumed API has changed.

    @Inject @ConfigValue("daw.https") private boolean useHttps;
    @Inject @ConfigValue("daw.host") private String host;
    @Inject @ConfigValue("daw.port") private int port;

    private DawPurchasesClient delegate;

    @PostConstruct
    public void init() {
        delegate = new DawPurchasesClient(useHttps, host, port);
    }

    @PreDestroy
    public void destroy() {
        delegate.dispose();
    }

    @CacheResult(cacheName = "users", cachedExceptions = {UserNotFoundException.class})
    public User getUser(@CacheKey String username) throws UserNotFoundException {
        return delegate.getUser(username);
    }

    @CacheResult(cacheName = "products", cachedExceptions = {ProductNotFoundException.class})
    public Product getProduct(@CacheKey long productId) throws ProductNotFoundException {
        return delegate.getProduct(productId);
    }

    @CacheResult(cacheName = "user-purchases")
    public List<Purchase> getPurchasesByUser(@CacheKey String username, @CacheKey int limit) {
        return delegate.getPurchasesByUser(username, limit);
    }

    @CacheResult(cacheName = "product-purchases")
    public List<Purchase> getPurchasesByProduct(@CacheKey long productId, @CacheKey int limit) {
        return delegate.getPurchasesByProduct(productId, limit);
    }

}
