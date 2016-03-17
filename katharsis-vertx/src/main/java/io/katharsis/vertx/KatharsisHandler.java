package io.katharsis.vertx;

import io.katharsis.dispatcher.controller.BaseController;
import io.katharsis.errorhandling.ErrorData;
import io.katharsis.errorhandling.ErrorResponse;
import io.katharsis.errorhandling.exception.KatharsisMatchingException;
import io.katharsis.errorhandling.mapper.ExceptionMapperRegistry;
import io.katharsis.errorhandling.mapper.JsonApiExceptionMapper;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.queryParams.QueryParamsBuilder;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.katharsis.request.dto.RequestBody;
import io.katharsis.request.path.JsonPath;
import io.katharsis.request.path.PathBuilder;
import io.katharsis.response.BaseResponse;
import io.katharsis.utils.java.Optional;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Vertx handler to Katharsis resource controller. Vertx delegates request processing to Katharsis controller.
 */
@Slf4j
@Value
@RequiredArgsConstructor
public class KatharsisHandler implements Handler<RoutingContext> {

    private final QueryParamsBuilder builder;
    private final ParameterProviderFactory parameterProviderFactory;
    private final PathBuilder pathBuilder;
    private final ExceptionMapperRegistry exceptionMapperRegistry;
    private final BaseController controller;
    private final String webPath;

    public JsonPath buildPath(@NonNull String path) {
        //TODO; path need to be cleaned
        String cleaned = Paths.get(path).toString();
        String transformed = cleaned.substring(webPath.length());
        log.trace("Path is {}", transformed);
        return pathBuilder.buildPath(transformed);
    }

    public JsonPath buildPath(RoutingContext ctx) {
        return buildPath(ctx.request().path());
    }

    public QueryParams createQueryParams(RoutingContext ctx) {
        Map<String, Set<String>> transformed = new HashMap<>();

        QueryStringDecoder decoder = new QueryStringDecoder(ctx.request().uri());

        decoder.parameters().entrySet().stream()
                .forEach(param -> transformed.put(param.getKey(), new HashSet<>(param.getValue())));

        return builder.buildQueryParams(transformed);
    }

    @Override
    public void handle(RoutingContext ctx) {
        ctx.request().bodyHandler(requestBody -> {
            BaseResponse<?> response = null;
            boolean passToMethodMatcher = false;
            try {

                JsonPath jsonPath = buildPath(ctx);
                QueryParams queryParams = createQueryParams(ctx);
                RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);

                String bodyAsString = requestBody.toString(StandardCharsets.UTF_8);
                response = controller.handle(jsonPath, queryParams, parameterProvider, requestBody(bodyAsString));

            } catch (KatharsisMatchingException e) {
                log.error("Error {}", e);
                passToMethodMatcher = true;
            } catch (Exception e1) {
                response = toErrorResponse(e1);
            } finally {
                sendResponse(ctx, response, passToMethodMatcher);
            }
        });
    }

    public BaseResponse<?> toErrorResponse(@NonNull Throwable e) {
        return toErrorResponse(e, 422);
    }

    public BaseResponse<?> toErrorResponse(@NonNull Throwable e, int statusCode) {
        Optional<JsonApiExceptionMapper> exceptionMapper = exceptionMapperRegistry.findMapperFor(e.getClass());

        ErrorResponse errorResponse;
        if (exceptionMapper.isPresent()) {
            errorResponse = exceptionMapper.get().toErrorResponse(e);
        } else {
            errorResponse = ErrorResponse.builder()
                    .setStatus(statusCode)
                    .setSingleErrorData(ErrorData.builder()
                            .setDetail(e.getMessage() + e)
                            .build())
                    .build();
        }

        return errorResponse;
    }


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

    public RequestBody requestBody(String body) {
        if (body == null) {
            return null;
        }
        return Json.decodeValue(body, RequestBody.class);
    }

}
