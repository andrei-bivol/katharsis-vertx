package io.katharsis.vertx;

import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class KatharsisRestApiTest {

    KatharsisGlue katharsisGlue;

    @Before
    public void setUp() throws Exception {
        katharsisGlue = KatharsisGlue.create("io.katharsis", "api",
                new DefaultParameterProviderFactory(Json.mapper));
    }

    @Test
    public void testCollectionGet(TestContext context) throws Exception {

    }
}
