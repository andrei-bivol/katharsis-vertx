package io.katharsis.vertx;

import com.google.common.base.Throwables;
import io.katharsis.errorhandling.exception.KatharsisMatchingException;
import io.katharsis.response.BaseResponse;
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

        router.get("/").blockingHandler(ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.collectionGet(ctx);
            } catch (KatharsisMatchingException e) {
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = katharsis.toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.get("/:id").blockingHandler(ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.resourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = katharsis.toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.delete("/:id").blockingHandler(ctx -> {
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

        router.get("/:id/relationships/:relationship").blockingHandler(ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.relationshipsResourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error relationship {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = katharsis.toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }

        });

        router.get("/:id/:field").blockingHandler(ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.fieldResourceGet(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = katharsis.toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.post("/").blockingHandler(ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.resourcePost(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = katharsis.toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.post("/:id/:field").blockingHandler(ctx -> {

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.fieldResourcePost(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    response = katharsis.toErrorResponse(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });

        });

        router.post("/:id/relationships/:relationship").blockingHandler(ctx -> {

            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.relationshipsResourcePost(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = katharsis.toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.patch("/:id").blockingHandler(ctx -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {
                response = katharsis.resourcePatch(ctx);
            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = katharsis.toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });

        router.patch("/:id/relationships/:relationship").blockingHandler(ctx -> {

            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.relationshipsResourcePatch(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    response = katharsis.toErrorResponse(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        router.delete("/:id").blockingHandler(ctx -> {
            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.resourceDelete(ctx);
                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    response = katharsis.toErrorResponse(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        router.delete("/:id/relationships/:relationship").blockingHandler(ctx -> {
            ctx.request().bodyHandler(requestBody -> {
                BaseResponse<?> response = null;
                boolean passToMethodMatcher = false;
                try {
                    response = katharsis.relationshipsResourceDelete(ctx);

                } catch (KatharsisMatchingException e) {
                    log.error("Error {}", e);
                    passToMethodMatcher = true;
                } catch (Exception e1) {
                    response = katharsis.toErrorResponse(e1);
                } finally {
                    sendResponse(ctx, response, passToMethodMatcher);
                }
            });
        });

        return router;
    }
}
