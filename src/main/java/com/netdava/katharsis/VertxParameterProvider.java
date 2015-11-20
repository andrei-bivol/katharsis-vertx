package com.netdava.katharsis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@RequiredArgsConstructor
public class VertxParameterProvider implements RepositoryMethodParameterProvider {

    private final ObjectMapper objectMapper;
    private final RoutingContext ctx;

    @Override
    public <T> T provide(Method method, int parameterIndex) {
        Parameter parameter = getParameter(method, parameterIndex);
        Object returnValue = null;
//        if (ContainerRequestContext.class.isAssignableFrom(parameter.getType())) {
//            returnValue = requestContext;
//        } else if (SecurityContext.class.isAssignableFrom(parameter.getType())) {
//            returnValue = requestContext.getSecurityContext();
//        } else if (parameter.isAnnotationPresent(CookieParam.class)) {
//            returnValue = extractCookie(parameter);
//        } else if (parameter.isAnnotationPresent(HeaderParam.class)) {
//            returnValue = extractHeader(parameter);
//        }
        return (T) returnValue;
    }
}
