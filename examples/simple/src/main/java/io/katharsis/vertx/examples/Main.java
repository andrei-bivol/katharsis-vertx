package io.katharsis.vertx.examples;


import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Hello ");

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new KatharsisVerticle(vertx));

    }
}
