package com.mgl.demo.popularpurchases.vertx;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        HttpClient client = vertx.createHttpClient(new HttpClientOptions()
                .setDefaultHost("74.50.59.155")
                .setDefaultPort(6000)
                .setConnectTimeout(15_000)
                .setIdleTimeout(15_000));

        router.route()
                .path("/api/recent_purchases/:username")
                .produces("application/json")
                .handler(new RecentPurchasesHandler(client, vertx.sharedData()));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listenObservable(8002)
                .subscribe(
                        (server) -> {}, 
                        (error) -> System.exit(1));
    }

}
