package io.katharsis.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultParameterProviderFactory implements ParameterProviderFactory {

    private ObjectMapper mapper;

    @Override
    public RepositoryMethodParameterProvider provider(RoutingContext ctx) {
        return new DefaultParameterProvider(mapper, ctx);
    }

    /**
     * The {@link RepositoryMethodParameterProvider RepositoryMethodParameterProvider}
     * allows you to inject object into your repository methods.
     */
    @Data
    @RequiredArgsConstructor
    public static class DefaultParameterProvider implements RepositoryMethodParameterProvider {

        private final ObjectMapper mapper;
        private final RoutingContext ctx;

        @Override
        public <T> T provide(Method method, int parameterIndex) {
            Class<?> parameter = method.getParameterTypes()[parameterIndex];
            Object returnValue = null;
            if (RoutingContext.class.isAssignableFrom(parameter)) {
                returnValue = ctx;
            } else if (ObjectMapper.class.isAssignableFrom(parameter)) {
                returnValue = mapper;
            }
            return (T) returnValue;
        }
    }
}
