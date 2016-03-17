package io.katharsis.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.katharsis.dispatcher.RequestDispatcher;
import io.katharsis.dispatcher.controller.collection.CollectionGet;
import io.katharsis.dispatcher.controller.resource.FieldResourceGet;
import io.katharsis.dispatcher.controller.resource.FieldResourcePost;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourceDelete;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourceGet;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourcePatch;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourcePost;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourceUpsert;
import io.katharsis.dispatcher.controller.resource.ResourceDelete;
import io.katharsis.dispatcher.controller.resource.ResourceGet;
import io.katharsis.dispatcher.controller.resource.ResourcePatch;
import io.katharsis.dispatcher.controller.resource.ResourcePost;
import io.katharsis.dispatcher.controller.resource.ResourceUpsert;
import io.katharsis.dispatcher.registry.ControllerRegistry;
import io.katharsis.dispatcher.registry.ControllerRegistryBuilder;
import io.katharsis.errorhandling.ErrorData;
import io.katharsis.errorhandling.ErrorResponse;
import io.katharsis.errorhandling.exception.KatharsisMappableException;
import io.katharsis.errorhandling.mapper.ExceptionMapperRegistry;
import io.katharsis.errorhandling.mapper.ExceptionMapperRegistryBuilder;
import io.katharsis.errorhandling.mapper.JsonApiExceptionMapper;
import io.katharsis.errorhandling.mapper.KatharsisExceptionMapper;
import io.katharsis.jackson.JsonApiModuleBuilder;
import io.katharsis.locator.JsonServiceLocator;
import io.katharsis.locator.SampleJsonServiceLocator;
import io.katharsis.queryParams.DefaultQueryParamsParser;
import io.katharsis.queryParams.QueryParams;
import io.katharsis.queryParams.QueryParamsBuilder;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.katharsis.request.dto.RequestBody;
import io.katharsis.request.path.JsonPath;
import io.katharsis.request.path.PathBuilder;
import io.katharsis.resource.field.ResourceFieldNameTransformer;
import io.katharsis.resource.include.IncludeLookupSetter;
import io.katharsis.resource.information.ResourceInformationBuilder;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ResourceRegistryBuilder;
import io.katharsis.response.BaseResponse;
import io.katharsis.utils.java.Optional;
import io.katharsis.utils.parser.TypeParser;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KatharsisGlue {

    private final QueryParamsBuilder builder = new QueryParamsBuilder(new DefaultQueryParamsParser());
    private ObjectMapper objectMapper;
    private ParameterProviderFactory parameterProviderFactory;
    private ResourceRegistry resourceRegistry;
    private JsonServiceLocator jsonServiceLocator;
    private TypeParser typeParser;
    private String webPath;
    private ExceptionMapperRegistry exceptionMapperRegistry;
    private PathBuilder pathBuilder;
    private KatharsisExceptionMapper katharsisExceptionMapper;
    private IncludeLookupSetter includeLookupSetter;
    private CollectionGet collectionGet;
    private ResourceDelete resourceDelete;
    private ResourceGet resourceGet;
    private ResourcePatch resourcePatch;
    private ResourcePost resourcePost;
    private ResourceUpsert resourceUpsert;
    private FieldResourceGet fieldResourceGet;
    private FieldResourcePost fieldResourcePost;
    private RelationshipsResourceDelete relationshipsResourceDelete;
    private RelationshipsResourceGet relationshipsResourceGet;
    private RelationshipsResourcePatch relationshipsResourcePatch;
    private RelationshipsResourcePost relationshipsResourcePost;
    private RelationshipsResourceUpsert relationshipsResourceUpsert;

    private KatharsisGlue(ObjectMapper objectMapper, ParameterProviderFactory factory) {
        this.objectMapper = objectMapper;
        this.parameterProviderFactory = factory;
    }

    public static KatharsisGlue create(@NonNull String packagesToScan,
                                       @NonNull String webPath,
                                       @NonNull ParameterProviderFactory factory) {

        KatharsisGlue katharsisGlue = new KatharsisGlue(Json.mapper, factory);
        katharsisGlue.setWebPath(webPath);
        return katharsisGlue.configure(packagesToScan, webPath);
    }

    public JsonPath buildPath(@NonNull String path) {
        //TODO; path need to be cleaned
        String cleaned = Paths.get(path).toString();
        String transformed = cleaned.substring(webPath.length());
        log.info("Path is {}", transformed);
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

    public KatharsisGlue configure(@NonNull String packagesToScan, @NonNull String webPath) {
        resourceRegistry = buildRegistry(packagesToScan, webPath);
        pathBuilder = new PathBuilder(resourceRegistry);
        includeLookupSetter = new IncludeLookupSetter(resourceRegistry);
        typeParser = new TypeParser();

        JsonApiModuleBuilder jsonApiModuleBuilder = new JsonApiModuleBuilder();
        objectMapper.registerModule(jsonApiModuleBuilder.build(resourceRegistry));

        exceptionMapperRegistry = buildExceptionMapperRegistry(packagesToScan);
        katharsisExceptionMapper = new KatharsisExceptionMapper();

        collectionGet = new CollectionGet(resourceRegistry, typeParser, includeLookupSetter);
        resourceGet = new ResourceGet(resourceRegistry, typeParser, includeLookupSetter);
        resourceDelete = new ResourceDelete(resourceRegistry, typeParser);

        resourcePatch = new ResourcePatch(resourceRegistry, typeParser, objectMapper);
        resourcePost = new ResourcePost(resourceRegistry, typeParser, objectMapper);
        resourceUpsert = null;


        fieldResourceGet = new FieldResourceGet(resourceRegistry, typeParser, includeLookupSetter);
        fieldResourcePost = new FieldResourcePost(resourceRegistry, typeParser, objectMapper);
        relationshipsResourceDelete = new RelationshipsResourceDelete(resourceRegistry, typeParser);
        relationshipsResourceGet = new RelationshipsResourceGet(resourceRegistry, typeParser, includeLookupSetter);
        relationshipsResourcePatch = new RelationshipsResourcePatch(resourceRegistry, typeParser);
        relationshipsResourcePost = new RelationshipsResourcePost(resourceRegistry, typeParser);

        return this;
    }

    public ResourceRegistry buildRegistry(String packageToScan, String webPath) {
        ResourceRegistryBuilder registryBuilder = new ResourceRegistryBuilder(new SampleJsonServiceLocator(),
                new ResourceInformationBuilder(new ResourceFieldNameTransformer()));

        return registryBuilder.build(packageToScan, webPath);
    }

    private ExceptionMapperRegistry buildExceptionMapperRegistry(String resourceSearchPackage) {
        ExceptionMapperRegistryBuilder mapperRegistryBuilder = new ExceptionMapperRegistryBuilder();
        try {
            return mapperRegistryBuilder.build(resourceSearchPackage);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private RequestDispatcher createRequestDispatcher(ResourceRegistry resourceRegistry,
                                                      ExceptionMapperRegistry exceptionMapperRegistry) {
        TypeParser typeParser = new TypeParser();
        ControllerRegistryBuilder controllerRegistryBuilder = new ControllerRegistryBuilder(resourceRegistry,
                typeParser, Json.mapper);
        ControllerRegistry controllerRegistry;
        try {
            controllerRegistry = controllerRegistryBuilder.build();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return new RequestDispatcher(controllerRegistry, exceptionMapperRegistry);
    }

    public BaseResponse<?> collectionGet(RoutingContext ctx) throws InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return collectionGet.handle(jsonPath, queryParams, parameterProvider, null);
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> resourceGet(RoutingContext ctx) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return resourceGet.handle(jsonPath, queryParams, parameterProvider, null);
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> relationshipsResourceGet(RoutingContext ctx) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return relationshipsResourceGet.handle(jsonPath, queryParams, parameterProvider, null);
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> fieldResourceGet(RoutingContext ctx) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return fieldResourceGet.handle(jsonPath, queryParams, parameterProvider, null);
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> resourcePost(RoutingContext ctx) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, IOException, InvocationTargetException {

        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return resourcePost.handle(jsonPath, queryParams, parameterProvider, requestBody(ctx.getBodyAsString()));
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        } catch (RuntimeException re) {
            return toErrorResponse(re);
        }
    }

    public BaseResponse<?> fieldResourcePost(RoutingContext ctx) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, IOException, InvocationTargetException {

        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return fieldResourcePost.handle(jsonPath, queryParams, parameterProvider,
                    requestBody(ctx.getBodyAsString()));
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> relationshipsResourcePost(RoutingContext ctx) throws Exception {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return relationshipsResourcePost.handle(jsonPath, queryParams,
                    parameterProvider, requestBody(ctx.getBodyAsString()));
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        } catch (RuntimeException re) {
            return toErrorResponse(re);
        }
    }

    public BaseResponse<?> resourcePatch(RoutingContext ctx) throws Exception {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return resourcePatch.handle(jsonPath, queryParams,
                    parameterProvider, requestBody(ctx.getBodyAsString()));
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> relationshipsResourcePatch(RoutingContext ctx) throws Exception {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            return relationshipsResourcePatch.handle(jsonPath, queryParams,
                    parameterProvider, requestBody(ctx.getBodyAsString()));
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> resourceDelete(RoutingContext ctx)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);
            // TODO: I think we can pass null for json path and request params
            return resourceDelete.handle(jsonPath, queryParams, parameterProvider, null);
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> relationshipsResourceDelete(RoutingContext ctx) throws Exception {
        try {
            JsonPath jsonPath = buildPath(ctx);
            QueryParams queryParams = createQueryParams(ctx);
            RepositoryMethodParameterProvider parameterProvider = parameterProviderFactory.provider(ctx);

            return relationshipsResourceDelete.handle(jsonPath, queryParams, parameterProvider,
                    requestBody(ctx.getBodyAsString()));
        } catch (KatharsisMappableException e) {
            return toErrorResponse(e);
        }
    }

    public BaseResponse<?> toErrorResponse(@NonNull Throwable e) {
        return toErrorResponse(e, 422);
    }

    public BaseResponse<?> toErrorResponse(@NonNull Throwable e, int statusCode) {
        Optional<JsonApiExceptionMapper> exceptionMapper =
                exceptionMapperRegistry.findMapperFor(e.getClass());


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

    public RequestBody requestBody(String body) {
        return Json.decodeValue(body, RequestBody.class);
    }
}
