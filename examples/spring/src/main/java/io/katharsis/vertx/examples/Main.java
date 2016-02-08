package io.katharsis.vertx.examples;


import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

        vertx.deployVerticle(new KatharsisVerticle(vertx, context));

    }
}
