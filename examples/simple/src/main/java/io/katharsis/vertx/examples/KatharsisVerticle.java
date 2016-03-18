package io.katharsis.vertx.examples;

import io.katharsis.vertx.KatharsisHandlerFactory;
import io.katharsis.vertx.KatharsisRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KatharsisVerticle extends AbstractVerticle {

    final Vertx vertx;

    @Override
    public void start(Future<Void> fut) throws Exception {
        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 3 application</h1>" +
                            "<a href='/api/projects'>/api/projects</a>");
        });

        KatharsisHandlerFactory katharsisGlue = KatharsisHandlerFactory.create(Json.mapper, Main.class.getPackage().getName(), "/api");

        Router katharsisRouter = KatharsisRestApi.createRouter(vertx, katharsisGlue);
        router.mountSubRouter("/api/projects", katharsisRouter);
        router.mountSubRouter("/api/tasks", katharsisRouter);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }
}
