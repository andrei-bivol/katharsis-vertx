package io.katharsis.vertx.examples;

import io.katharsis.vertx.examples.services.Generator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public Generator generator() {
        return new Generator();
    }

}
