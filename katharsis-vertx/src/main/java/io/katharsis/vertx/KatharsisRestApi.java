package io.katharsis.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class KatharsisRestApi extends AbstractKatharsisRouter {

    final Vertx vertx;
    final KatharsisGlue katharsis;
    final AuthHandler authHandler;

    public static Router createRouter(Vertx vertx, KatharsisGlue katharsisGlue) {
        KatharsisRestApi api = new KatharsisRestApi(vertx, katharsisGlue, null);
        return api.createRouter();
    }

    public static Router createRouter(Vertx vertx, KatharsisGlue katharsisGlue, AuthHandler authHandler) {
        KatharsisRestApi api = new KatharsisRestApi(vertx, katharsisGlue, authHandler);
        return api.createRouter();
    }

    private Router createRouter() {
        Router router = Router.router(vertx);

        if (authHandler != null) {
            router.route().handler(authHandler);
        }

        // http://katharsis.io/#supported-requests
        router.get("/").blockingHandler(collectionGetHandler());
        router.get("/:id").blockingHandler(resourceGetHandler());
        router.get("/:id/relationships/:relationship").blockingHandler(relationshipsGetHandler());
        router.get("/:id/:field").blockingHandler(fieldGetHandler());

        router.post("/").blockingHandler(resourcePostHandler());
        router.post("/:id/:field").blockingHandler(fieldPostHandler());
        router.post("/:id/relationships/:relationship").blockingHandler(relationshipPostHandler());

        router.patch("/:id").blockingHandler(resourcePatchHandler());
        router.patch("/:id/relationships/:relationship").blockingHandler(relationshipPatchHandler());

        router.delete("/:id").blockingHandler(resourceDeleteHandler());
        router.delete("/:id/relationships/:relationship").blockingHandler(relationshipDeleteHandler());

        return router;
    }

}
