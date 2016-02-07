package io.katharsis.vertx;

import io.katharsis.response.BaseResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class AbstractKatharsisRouter {

    void sendResponse(RoutingContext ctx, BaseResponse<?> response, boolean passToMethodMatcher) {
        if (passToMethodMatcher) {
            ctx.response()
                    .setStatusCode(406)
                    .putHeader(HttpHeaders.CONTENT_TYPE, JsonApiMediaType.APPLICATION_JSON_API)
                    .end(Json.encode(response));
        } else {
            ctx.response()
                    .setStatusCode(response.getHttpStatus())
                    .putHeader(HttpHeaders.CONTENT_TYPE, JsonApiMediaType.APPLICATION_JSON_API)
                    .end(Json.encode(response));
        }
    }

}
