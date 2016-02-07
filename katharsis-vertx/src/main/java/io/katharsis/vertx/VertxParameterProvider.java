package io.katharsis.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * The {@link io.katharsis.repository.RepositoryMethodParameterProvider RepositoryMethodParameterProvider}
 * allows you to inject object into your repository methods.
 */
@RequiredArgsConstructor
public class VertxParameterProvider implements RepositoryMethodParameterProvider {

    private final ObjectMapper objectMapper;
    private final RoutingContext ctx;

    @Override
    public <T> T provide(Method method, int parameterIndex) {
        Class<?> parameter = method.getParameterTypes()[parameterIndex];
        Object returnValue = null;
        if (RoutingContext.class.isAssignableFrom(parameter)) {
            returnValue = ctx;
        } else if (ObjectMapper.class.isAssignableFrom(parameter)) {
            returnValue = objectMapper;
        }
        return (T) returnValue;
    }
}
