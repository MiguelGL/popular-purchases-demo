package com.mgl.demo.popularpurchases.vertx;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.net.HttpHeaders.CONTENT_ENCODING;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static com.google.common.net.UrlEscapers.urlPathSegmentEscaper;
import static com.mgl.demo.popularpurchases.vertx.StreamUtils.asStream;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rx.java.ObservableHandler;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.core.shareddata.LocalMap;
import io.vertx.rxjava.core.shareddata.SharedData;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;

public class RecentPurchasesHandler implements Handler<RoutingContext> {

    private static final int PURCHASES_LIMIT = 5;

    private final HttpClient client;

    private final LocalMap<String, String> usersCache; // username, user
    private final LocalMap<Long, String> productsCache; // productId, product
    private final LocalMap<String, String> userPurchasesCache; // username, purchases
    private final LocalMap<Long, String> productPurchasesCache; // productId, purchases

    public RecentPurchasesHandler(HttpClient client, SharedData sharedData) {
        this.client = client;

        usersCache = sharedData.getLocalMap("users");
        productsCache = sharedData.getLocalMap("products");
        userPurchasesCache = sharedData.getLocalMap("user-purchases");
        productPurchasesCache = sharedData.getLocalMap("product-purchases");
    }

    private static String cacheSerialize(JsonNode jsonNode) {
        return Json.encode(jsonNode);
    }

    private static ObjectNode cacheDeserialiseObject(String cachedValue) {
        return Json.decodeValue(cachedValue, ObjectNode.class);
    }

    private static ArrayNode cacheDeserialiseArray(String cachedValue) {
        return Json.decodeValue(cachedValue, ArrayNode.class);
    }

    private static String userEndpoint(String username) {
        String encodedUsername = urlPathSegmentEscaper().escape(username);
        return String.format("/api/users/%s", encodedUsername);
    }

    private static String productEndpoint(long productId) {
        return String.format("/api/products/%d", productId);
    }

    private static String purchasesByUserEndpoint(String username, int limit) {
        String encodedUsername = urlPathSegmentEscaper().escape(username);
        return String.format("/api/purchases/by_user/%s?limit=%d", encodedUsername, limit);
    }

    private static String purchasesByProductEndpoint(long productId) {
        return String.format("/api/purchases/by_product/%d", productId);
    }

    private Observable<Boolean> observeUserExists(String username) {
        String maybeJsonUser = usersCache.get(username);
        if (maybeJsonUser != null) {
            return Observable.just(true);
        }

        ObservableHandler<HttpClientResponse> responseObservable = RxHelper.observableHandler();
        client.getNow(userEndpoint(username), responseObservable.toHandler());
        return responseObservable
            .flatMap(response -> {
                if (response.statusCode() == HTTP_OK) {
                    ObservableHandler<Buffer> observable = RxHelper.observableHandler();
                    response.bodyHandler(observable.toHandler());
                    return observable;
                } else {
                    throw new RuntimeException(String.format("User call returned %d status",
                            response.statusCode()));
                }                
            })
            .map(buffer -> Json.decodeValue(buffer.toString(UTF_8.name()), ObjectNode.class))
            .map(user -> {
                boolean exists = user.has("user");
                if (exists) {
                    // FIXME: not caching "non existing" users.
                    usersCache.put(username, cacheSerialize(user));
                }
                return exists;
            });
    }

    private void respondNotFound(HttpServerResponse response, String username) {
        response.setStatusCode(HTTP_NOT_FOUND);
        response.headers()
            .set(CONTENT_ENCODING, UTF_8.name())
            .set(CONTENT_TYPE, PLAIN_TEXT_UTF_8.toString());
        response.end(String.format("User with username of '%s' was not found", username));
    }

    private void respondInternalError(HttpServerResponse response, Throwable th) {
        response.setStatusCode(HTTP_NOT_FOUND);
        response.headers()
            .set(CONTENT_ENCODING, UTF_8.name())
            .set(CONTENT_TYPE, PLAIN_TEXT_UTF_8.toString());
        response.end(String.valueOf(th));
    }

    private Observable<ObjectNode> observeGetObject(String endpoint) {
        ObservableHandler<HttpClientResponse> responseObservable = RxHelper.observableHandler();
        client.getNow(endpoint, responseObservable.toHandler());
        return responseObservable
            .flatMap(response -> {
                if (response.statusCode() == HTTP_OK) {
                    ObservableHandler<Buffer> observable = RxHelper.observableHandler();
                    response.bodyHandler(observable.toHandler());
                    return observable;
                } else {
                    throw new RuntimeException(String.format(
                            "Endpoint '%s' get returned %d status",
                            endpoint, response.statusCode()));
                }
            })
            .map(buffer -> Json.decodeValue(buffer.toString(UTF_8.name()), ObjectNode.class));
    }

    private Observable<ArrayNode> observeGetWrappedArray(String endpoint, String wrappingField) {
        return observeGetObject(endpoint)
            .map((ObjectNode jsonObject) -> {
                JsonNode wrappedValue = jsonObject.get(wrappingField);
                if (wrappedValue == null) {
                    throw new RuntimeException(String.format("Wrapping field '%s' is null",
                            wrappingField));
                } else if (wrappedValue.getNodeType() == JsonNodeType.ARRAY) {
                    return (ArrayNode) wrappedValue;
                } else {
                    throw new RuntimeException(String.format(
                            "Wrappeed value '%s' is not an array, but %s",
                            wrappingField, wrappedValue.getNodeType()));
                }
            });
    }

    private Observable<ObjectNode> observeGetWrappedObject(String endpoint, String wrappingField) {
        return observeGetObject(endpoint)
            .map((ObjectNode jsonObject) -> {
                JsonNode wrappedValue = jsonObject.get(wrappingField);
                if (wrappedValue == null) {
                    throw new RuntimeException(String.format("Wrapping field '%s' is null",
                            wrappingField));
                } else if (wrappedValue.getNodeType() == JsonNodeType.OBJECT) {
                    return (ObjectNode) wrappedValue;
                } else {
                    throw new RuntimeException(String.format(
                            "Wrappeed value '%s' is not an object, but %s",
                            wrappingField, wrappedValue.getNodeType()));
                }
            });
    }

    private Observable<ArrayNode> observeGetUserPurchases(String username) {
        String maybeJsonPurchases = userPurchasesCache.get(username);
        if (maybeJsonPurchases != null) {
            return Observable.just(cacheDeserialiseArray(maybeJsonPurchases));
        }

        return observeGetWrappedArray(purchasesByUserEndpoint(username, PURCHASES_LIMIT), "purchases")
            .map(purchases -> {
                userPurchasesCache.put(username, cacheSerialize(purchases));
                return purchases;
            });
    }

    private Observable<ArrayNode> observeGetProductPurchases(long productId) {
        String maybeJsonPurchases = productPurchasesCache.get(productId);
        if (maybeJsonPurchases != null) {
            return Observable.just(cacheDeserialiseArray(maybeJsonPurchases));
        }

        return observeGetWrappedArray(purchasesByProductEndpoint(productId), "purchases")
            .map(purchases -> {
                productPurchasesCache.put(productId, cacheSerialize(purchases));
                return purchases;
            });
    }

    private Observable<ObjectNode> observeGetProductDetails(long productId) {
        String maybeJsonProduct = productsCache.get(productId);
        if (maybeJsonProduct != null) {
            return Observable.just(cacheDeserialiseObject(maybeJsonProduct));
        }

        return observeGetWrappedObject(productEndpoint(productId), "product")
            .map(product -> {
                productsCache.put(productId, cacheSerialize(product));
                return product;
            });
    }

    private void respondRecentPurchases(HttpServerResponse response, String username) {
        Observable<List<ObjectNode>> observedRecentPurchasesList =
            observeGetUserPurchases(username)
            .flatMap(userPurchases -> {

                List<Long> productIds = asStream(userPurchases.elements())
                    .map(ObjectNode.class::cast)
                    .map(jsonPurchase -> jsonPurchase.get("productId").asLong())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                Observable<ObjectNode> observedProductDetails = Observable.from(productIds)
                    .flatMap(this::observeGetProductDetails);

                Observable<ArrayNode> observedProductPurchases = Observable.from(productIds)
                    .flatMap(this::observeGetProductPurchases);

                Observable<List<ObjectNode>> observedRecentPurchases = observedProductDetails
                    .zipWith(
                        observedProductPurchases,
                        (productDetails, productPurchases) -> {
                            ObjectNode recentPurchase = Json.mapper.createObjectNode()
                                .put("id", productDetails.get("id").asText())
                                .put("face", productDetails.get("face").asText())
                                .put("price", productDetails.get("price").asLong())
                                .put("size", productDetails.get("size").asLong())
                                .put("recent", productDetails.get("size").asLong());
                            ArrayNode recents = recentPurchase.putArray("recent");
                            asStream(productPurchases.elements())
                                .map(purchase -> purchase.get("username").textValue())
                                .distinct()
                                .forEach(user -> recents.add(user));
                            return recentPurchase;
                        })
                    .toSortedList((recentPurchase1, recentPurchase2) ->  {
                        ArrayNode recent1 = (ArrayNode) recentPurchase1.get("recent");
                        ArrayNode recent2 = (ArrayNode) recentPurchase2.get("recent");
                        return Integer.compare(recent1.size(), recent2.size()) * -1; // desc
                    });

                return observedRecentPurchases;
            });

        observedRecentPurchasesList.subscribe(recentPurchasesList -> {

            ArrayNode jsonResponse = Json.mapper.createArrayNode();

            recentPurchasesList.forEach(jsonResponse::add);

            response.setStatusCode(HTTP_OK);
            response.headers()
                .set(CONTENT_ENCODING, UTF_8.name())
                .set(CONTENT_TYPE, JSON_UTF_8.toString());

            response.end(Json.encode(jsonResponse));
        }, th -> {
            respondInternalError(response, th);
        });
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String username = routingContext.request().getParam("username");
        observeUserExists(username)
            .subscribe(exists -> {
                if (exists) {
                    respondRecentPurchases(routingContext.response(), username);
                } else {
                    respondNotFound(routingContext.response(), username);
                }
            }, th -> {
                respondInternalError(routingContext.response(), th);
            });
    }

}
