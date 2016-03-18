package io.katharsis.vertx;

import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class KatharsisRestApiTest {

    KatharsisHandlerFactory katharsisGlue;

    @Before
    public void setUp() throws Exception {
        katharsisGlue = KatharsisHandlerFactory.create("io.katharsis", "api");
    }

    @Test
    public void testCollectionGet(TestContext context) throws Exception {

    }
}
