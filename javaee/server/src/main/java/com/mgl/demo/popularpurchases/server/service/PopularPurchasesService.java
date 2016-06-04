package com.mgl.demo.popularpurchases.server.service;

import static com.mgl.demo.popularpurchases.dawps.client.DawPurchasesClient.NO_LIMIT;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.function.Function.identity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import com.mgl.demo.popularpurchases.dawps.client.ProductNotFoundException;
import com.mgl.demo.popularpurchases.dawps.client.UserNotFoundException;
import com.mgl.demo.popularpurchases.dawps.client.model.Product;
import com.mgl.demo.popularpurchases.dawps.client.model.Purchase;
import com.mgl.demo.popularpurchases.dawps.client.model.User;
import com.mgl.demo.popularpurchases.server.model.PopularPurchase;
import lombok.SneakyThrows;

// I am making this service an @Stateless EJB although I do not need transactional features,
// just interested in having instances pooled by the app server. Which is a premature
// optimisation however.
@Stateless
public class PopularPurchasesService {

    private static final int MAX_PURCHASES = 5;

    @Inject private CacheAwareDawPurchasesClient dawClient;

    @Resource private ManagedExecutorService executorService;

    private Set<String> getPurcharsersUsernames(long productId) {
        return dawClient.getPurchasesByProduct(productId, NO_LIMIT).stream()
                .map(Purchase::getUsername)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PopularPurchase> getMostPopularPurchases(String username, int limit) 
    throws UserNotFoundException, ProductNotFoundException {

        // First of all, ensure the user exists.
        User user = dawClient.getUser(username);

        // Now find the purchases this user made.
        List<Purchase> userPurchases = dawClient.getPurchasesByUser(
                user.getUsername(), MAX_PURCHASES);

        // Asynchronicity could also be achieved by using executorService.submit(Callable<T> task),
        // returning plain Future<T>'s. Instead of based on CompletableFuture's (these are
        // more poweful though, just maybe overusing them here).

        // Now asynchronously fetch ...

        // ... user purchased products details
        CompletableFuture<List<Product>> productsPromise = supplyAsync(
                () -> userPurchases.stream()
                        .map(Purchase::getProductId)
                        .distinct()
                        .map(dawClient::getProduct)
                        .collect(Collectors.toList()),
                executorService);

        // ... and the purchases by other users for these same products
        Map<Long, CompletableFuture<Set<String>>> perProductPurchasersPromises = 
                userPurchases.stream()
                .map(Purchase::getProductId)
                .distinct()
                .collect(Collectors.toMap(
                        identity(),
                        (productId) -> supplyAsync(
                                () -> getPurcharsersUsernames(productId), executorService)));

        // And now wait for results to be available ...

        // ... user purchased products details
        List<Product> products = productsPromise.get();

        // ... for purchases by other users for these same products
        Map<Long, Set<String>> perProductPurchasers = perProductPurchasersPromises
                .entrySet().stream()
                .collect(Collectors.toMap(
                        (entry) -> entry.getKey(), 
                        (entry) -> {
                            // Hate Java8 lambdas and checked exceptions :( I would indeed use
                            // something like jOOL for this, just did not want to get another
                            // dependency for this evaluation.
                            try {
                                return entry.getValue().get();
                            } catch (InterruptedException | ExecutionException ex) {
                                throw new RuntimeException(ex);
                            }
                        }));

        // This will rank products in terms of the most purchased.
        Comparator<Product> productPurchasesCountComparator =
                Comparator.comparing(product -> perProductPurchasers.get(product.getId()).size());

        // Finally return the sorted list of popular purchases.
        return products.stream()
                .sorted(productPurchasesCountComparator.reversed())
                .map(product -> new PopularPurchase(
                        product, perProductPurchasers.get(product.getId())))
                .collect(Collectors.toList());
    }

}
