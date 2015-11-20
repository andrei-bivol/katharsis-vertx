package com.netdava.katharsis;

import com.google.common.base.Throwables;
import io.katharsis.errorhandling.exception.KatharsisMatchingException;
import io.katharsis.request.dto.RequestBody;
import io.katharsis.response.BaseResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RequiredArgsConstructor
public class KatharsisRestApi {

    final Vertx vertx;
    final KatharsisGlue katharsis;
    BodyHandler bodyHandler;

    public static Router createRouter(Vertx vertx, KatharsisGlue katharsisGlue) {
        KatharsisRestApi api = new KatharsisRestApi(vertx, katharsisGlue);
        return api.createRouter();
    }

    private Router createRouter() {
        Router router = Router.router(vertx);

        router.route()
            .consumes(JsonApiMediaType.APPLICATION_JSON_API)
            .produces(JsonApiMediaType.APPLICATION_JSON_API);

        router.get("/").handler(ctx -> {
            log.info("Get all resources");

            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.collectionGet(ctx);
            } catch (KatharsisMatchingException e) {
                passToMethodMatcher = true;
            } catch (Exception e1) {
                throw Throwables.propagate(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.get("/:id").handler(ctx -> {
            log.info("Get single resource");
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.resourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                throw Throwables.propagate(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.delete("/id").handler(ctx -> {
            log.info("Get single resource");
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.resourceDelete(ctx);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.get("/:id/relationships/:relationship").handler(ctx -> {
            log.info("Get relationship");
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.relationshipsResourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error relationship {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                throw Throwables.propagate(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }

        });

        router.get("/:id/:field").handler(ctx -> {
            log.info("Get field");
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.fieldResourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                throw Throwables.propagate(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.post("/").handler(ctx -> {
            log.info("post resource");

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.resourcePost(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    throw Throwables.propagate(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        router.post("/:id/:field").handler(ctx -> {
            log.info("Update field");

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.fieldResourcePost(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    throw Throwables.propagate(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });

        });

        router.post("/:id/relationship/:relationship").handler(ctx -> {
            log.info("Update relationship");
            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.relationshipsResourcePost(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    throw Throwables.propagate(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        router.patch("/:id").handler(ctx -> {
            log.info("Patch id");

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.resourcePatch(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    throw Throwables.propagate(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        router.patch("/:id/relationships/:relationship").handler(ctx -> {
            log.info("Patch relationship.");

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.relationshipsResourcePatch(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    throw Throwables.propagate(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        router.delete("/:id").handler(ctx -> {
            log.info("Delete single");
            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.resourceDelete(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    throw Throwables.propagate(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        router.delete("/:id/relationships/:relationship").handler(ctx -> {
            log.info("Delete relationship");
            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.relationshipsResourceDelete(ctx);

                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    throw Throwables.propagate(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        return router;
    }

    private void sendResponse(RoutingContext ctx, BaseResponse<?> response, boolean passToMethodMatcher) {
        if (!passToMethodMatcher) {
            ctx.response()
                .setStatusCode(406)
                .end(Json.encode(response));

        } else {
            ctx.response().end(Json.encode(response));
        }
    }

    private RequestBody requestBody(Buffer requestBody) {
        //TODO: build request body from buffer
        // RequestBody requestBody = inputStreamToBody(new ByteArrayInputStream(ctx.getBody().getBytes()));
        return new RequestBody();
    }

}
