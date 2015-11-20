package com.netdava.katharsis;

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
        katharsisGlue = KatharsisGlue.create("com.netdava.katharsis.example", "katharsis");
    }

    @Test
    public void testCollectionGet(TestContext context) throws Exception {

    }
}
