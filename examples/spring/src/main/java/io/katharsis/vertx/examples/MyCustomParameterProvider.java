package io.katharsis.vertx.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.katharsis.vertx.ParameterProviderFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCustomParameterProvider implements ParameterProviderFactory {

    private ObjectMapper mapper;
    private ApplicationContext context;

    @Override
    public RepositoryMethodParameterProvider provider(RoutingContext ctx) {
        return new SpringParameterProvider(mapper, ctx, context);
    }

    /**
     * The {@link RepositoryMethodParameterProvider RepositoryMethodParameterProvider}
     * allows you to inject object into your repository methods.
     */
    @Data
    @RequiredArgsConstructor
    public static class SpringParameterProvider implements RepositoryMethodParameterProvider {

        private final ObjectMapper mapper;
        private final RoutingContext ctx;
        private final ApplicationContext context;

        @Override
        public <T> T provide(Method method, int parameterIndex) {
            Class<?> parameter = method.getParameterTypes()[parameterIndex];
            Object returnValue = null;
            if (RoutingContext.class.isAssignableFrom(parameter)) {
                returnValue = ctx;
            } else if (ApplicationContext.class.isAssignableFrom(parameter)) {
                returnValue = context;
            } else if (ObjectMapper.class.isAssignableFrom(parameter)) {
                returnValue = mapper;
            }
            return (T) returnValue;
        }
    }

}
