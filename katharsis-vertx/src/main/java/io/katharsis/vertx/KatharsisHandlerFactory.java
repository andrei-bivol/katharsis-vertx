package io.katharsis.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.katharsis.dispatcher.controller.BaseController;
import io.katharsis.dispatcher.controller.collection.CollectionGet;
import io.katharsis.dispatcher.controller.resource.FieldResourceGet;
import io.katharsis.dispatcher.controller.resource.FieldResourcePost;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourceDelete;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourceGet;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourcePatch;
import io.katharsis.dispatcher.controller.resource.RelationshipsResourcePost;
import io.katharsis.dispatcher.controller.resource.ResourceDelete;
import io.katharsis.dispatcher.controller.resource.ResourceGet;
import io.katharsis.dispatcher.controller.resource.ResourcePatch;
import io.katharsis.dispatcher.controller.resource.ResourcePost;
import io.katharsis.errorhandling.exception.KatharsisException;
import io.katharsis.errorhandling.mapper.ExceptionMapperRegistry;
import io.katharsis.errorhandling.mapper.ExceptionMapperRegistryBuilder;
import io.katharsis.jackson.JsonApiModuleBuilder;
import io.katharsis.locator.SampleJsonServiceLocator;
import io.katharsis.queryParams.DefaultQueryParamsParser;
import io.katharsis.queryParams.QueryParamsBuilder;
import io.katharsis.request.path.PathBuilder;
import io.katharsis.resource.field.ResourceFieldNameTransformer;
import io.katharsis.resource.include.IncludeLookupSetter;
import io.katharsis.resource.information.ResourceInformationBuilder;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ResourceRegistryBuilder;
import io.katharsis.utils.parser.TypeParser;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.NonNull;

public class KatharsisHandlerFactory {

    private final ObjectMapper objectMapper;
    private final String webPath;

    private QueryParamsBuilder builder;
    private ParameterProviderFactory parameterProviderFactory;
    private ResourceRegistry resourceRegistry;
    private TypeParser typeParser;
    private PathBuilder pathBuilder;
    private ExceptionMapperRegistry exceptionMapperRegistry;
    private IncludeLookupSetter includeLookupSetter;

    /**
     * Build a handler factory for katharsis. Use this constructor if the factory methods do not have what you need.
     *
     * @param objectMapper
     * @param webPath
     * @param builder
     * @param parameterProviderFactory
     * @param resourceRegistry
     * @param typeParser
     * @param exceptionMapperRegistry
     */
    public KatharsisHandlerFactory(ObjectMapper objectMapper,
                                   String webPath,
                                   QueryParamsBuilder builder,
                                   ParameterProviderFactory parameterProviderFactory,
                                   ResourceRegistry resourceRegistry,
                                   TypeParser typeParser,
                                   ExceptionMapperRegistry exceptionMapperRegistry) {

        this.objectMapper = objectMapper;
        this.webPath = webPath;
        this.builder = builder;
        this.parameterProviderFactory = parameterProviderFactory;
        this.resourceRegistry = resourceRegistry;
        this.typeParser = typeParser;
        this.exceptionMapperRegistry = exceptionMapperRegistry;

        this.includeLookupSetter = new IncludeLookupSetter(resourceRegistry);
        this.pathBuilder = new PathBuilder(resourceRegistry);

    }

    public static KatharsisHandlerFactory create(@NonNull String packagesToScan,
                                                 @NonNull String webPath) {

        return create(packagesToScan, webPath, Json.mapper);
    }

    public static KatharsisHandlerFactory create(@NonNull String packagesToScan,
                                                 @NonNull String webPath,
                                                 @NonNull ObjectMapper objectMapper) {

        return create(packagesToScan, webPath, objectMapper, new DefaultParameterProviderFactory());
    }

    public static KatharsisHandlerFactory create(@NonNull String packagesToScan,
                                                 @NonNull String webPath,
                                                 @NonNull ObjectMapper objectMapper,
                                                 @NonNull ParameterProviderFactory parameterProviderFactory) {

        TypeParser typeParser = new TypeParser();
        QueryParamsBuilder queryParamsBuilder = new QueryParamsBuilder(new DefaultQueryParamsParser());
        ExceptionMapperRegistry exceptionMapperRegistry = buildExceptionMapperRegistry(packagesToScan);
        ResourceRegistry resourceRegistry = buildRegistry(packagesToScan, webPath);

        JsonApiModuleBuilder jsonApiModuleBuilder = new JsonApiModuleBuilder();

        objectMapper.registerModule(jsonApiModuleBuilder.build(resourceRegistry));

        return new KatharsisHandlerFactory(objectMapper, webPath, queryParamsBuilder,
                parameterProviderFactory,
                resourceRegistry,
                typeParser,
                exceptionMapperRegistry);
    }

    public static ResourceRegistry buildRegistry(@NonNull String packageToScan, @NonNull String webPath) {
        ResourceRegistryBuilder registryBuilder = new ResourceRegistryBuilder(new SampleJsonServiceLocator(),
                new ResourceInformationBuilder(new ResourceFieldNameTransformer()));

        return registryBuilder.build(packageToScan, webPath);
    }

    private static ExceptionMapperRegistry buildExceptionMapperRegistry(String resourceSearchPackage) {
        ExceptionMapperRegistryBuilder mapperRegistryBuilder = new ExceptionMapperRegistryBuilder();
        try {
            return mapperRegistryBuilder.build(resourceSearchPackage);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public Handler<RoutingContext> handle(JsonApiCall apiCall) throws KatharsisException {
        switch (apiCall) {
            case COLLECTION_GET:
                return handleWith(new CollectionGet(resourceRegistry, typeParser, includeLookupSetter));
            case RESOURCE_GET:
                return handleWith(new ResourceGet(resourceRegistry, typeParser, includeLookupSetter));
            case FIELD_GET:
                return handleWith(new FieldResourceGet(resourceRegistry, typeParser, includeLookupSetter));
            case RELATIONSHIP_GET:
                return handleWith(new RelationshipsResourceGet(resourceRegistry, typeParser, includeLookupSetter));

            case RESOURCE_POST:
                return handleWith(new ResourcePost(resourceRegistry, typeParser, objectMapper));
            case RELATIONSHIP_POST:
                return handleWith(new RelationshipsResourcePost(resourceRegistry, typeParser));
            case FIELD_POST:
                return handleWith(new FieldResourcePost(resourceRegistry, typeParser, objectMapper));

            case RESOURCE_PATCH:
                return handleWith(new ResourcePatch(resourceRegistry, typeParser, objectMapper));
            case RELATIONSHIP_PATCH:
                return handleWith(new RelationshipsResourcePatch(resourceRegistry, typeParser));

            case RESOURCE_DELETE:
                return handleWith(new ResourceDelete(resourceRegistry, typeParser));
            case RELATIONSHIP_DELETE:
                return handleWith(new RelationshipsResourceDelete(resourceRegistry, typeParser));

            default:
                throw new KatharsisVertxException("Unsupported method.");
        }
    }

    public KatharsisHandler handleWith(BaseController controller) {
        return new KatharsisHandler(builder, parameterProviderFactory, pathBuilder, exceptionMapperRegistry, controller, webPath);
    }

}
