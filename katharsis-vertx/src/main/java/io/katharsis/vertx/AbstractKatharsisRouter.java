package io.katharsis.vertx;

import com.google.common.base.Throwables;
import io.katharsis.errorhandling.exception.KatharsisMatchingException;
import io.katharsis.response.BaseResponse;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Default handler for Katharsis Vertx integration. You can extend and override functionality or
 * implement your own version.
 */
@Slf4j
public abstract class AbstractKatharsisRouter {

    public abstract KatharsisGlue getKatharsis();

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



    protected Handler<RoutingContext> relationshipDeleteHandler() {
        return ctx -> {
            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = getKatharsis().relationshipsResourceDelete(ctx);

                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    response = getKatharsis().toErrorResponse(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        };
    }

    protected Handler<RoutingContext> relationshipPatchHandler() {
        return ctx -> {

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = getKatharsis().relationshipsResourcePatch(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    response = getKatharsis().toErrorResponse(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        };
    }

    protected Handler<RoutingContext> resourcePatchHandler() {
        return ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().resourcePatch(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = getKatharsis().toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        };
    }

    protected Handler<RoutingContext> relationshipPostHandler() {
        return ctx -> {

            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().relationshipsResourcePost(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = getKatharsis().toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        };
    }

    protected Handler<RoutingContext> fieldPostHandler() {
        return ctx -> {

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = getKatharsis().fieldResourcePost(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    response = getKatharsis().toErrorResponse(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });

        };
    }

    protected Handler<RoutingContext> resourcePostHandler() {
        return ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().resourcePost(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = getKatharsis().toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        };
    }

    protected Handler<RoutingContext> fieldGetHandler() {
        return ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().fieldResourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = getKatharsis().toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        };
    }

    protected Handler<RoutingContext> relationshipsGetHandler() {
        return ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().relationshipsResourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error relationship {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = getKatharsis().toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }

        };
    }

    protected Handler<RoutingContext> resourceDeleteHandler() {
        return ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().resourceDelete(ctx);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        };
    }

    protected Handler<RoutingContext> resourceGetHandler() {
        return ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().resourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = getKatharsis().toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        };
    }

    protected Handler<RoutingContext> collectionGetHandler() {
        return ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = getKatharsis().collectionGet(ctx);
            } catch (KatharsisMatchingException e) {
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = getKatharsis().toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        };
    }
}
