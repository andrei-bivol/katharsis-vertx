package io.katharsis.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@AllArgsConstructor
public class KatharsisRestApi {

    final Vertx vertx;
    final KatharsisHandlerFactory handlerFactory;

    public static Router createRouter(@NonNull Vertx vertx, @NonNull String packagesToScan, @NonNull String webPath) {
        return createRouter(vertx, KatharsisHandlerFactory.create(packagesToScan, webPath));
    }

    public static Router createRouter(Vertx vertx, KatharsisHandlerFactory handlerFactory) {
        KatharsisRestApi api = new KatharsisRestApi(vertx, handlerFactory);
        return api.createRouter();
    }

    private Router createRouter() {
        Router router = Router.router(vertx);

        // http://katharsis.io/#supported-requests
        router.get("/")
                .blockingHandler(handlerFactory.handle(JsonApiCall.COLLECTION_GET));
        router.get("/:id")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RESOURCE_GET));

        router.get("/:id/relationships/:relationship")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RELATIONSHIP_GET));

        router.get("/:id/:field")
                .blockingHandler(handlerFactory.handle(JsonApiCall.FIELD_GET));

        router.post("/")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RELATIONSHIP_POST));
        router.post("/:id/:field")
                .blockingHandler(handlerFactory.handle(JsonApiCall.FIELD_POST));
        router.post("/:id/relationships/:relationship")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RELATIONSHIP_POST));

        router.patch("/:id")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RESOURCE_PATCH));
        router.patch("/:id/relationships/:relationship")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RELATIONSHIP_PATCH));

        router.delete("/:id")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RESOURCE_DELETE));
        router.delete("/:id/relationships/:relationship")
                .blockingHandler(handlerFactory.handle(JsonApiCall.RELATIONSHIP_DELETE));

        return router;
    }

}
