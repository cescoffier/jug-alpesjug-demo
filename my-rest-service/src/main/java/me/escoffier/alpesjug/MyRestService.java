package me.escoffier.alpesjug;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class MyRestService extends AbstractVerticle {

    public static final String HOSTNAME =
        System.getenv("HOSTNAME");

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.get("/")
            .handler(rc -> rc.response().end("OK"));
        router.get("/api/hello/:name").handler(
            rc -> rc.response()
                .end(
                    new JsonObject()
                        .put("hostname", HOSTNAME)
                        .put("message", "Hello "
                            + rc.pathParam("name"))
                        .encode()));

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(8080);

    }
}
