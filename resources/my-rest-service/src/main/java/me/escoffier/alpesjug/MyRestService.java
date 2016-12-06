package me.escoffier.alpesjug;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class MyRestService extends AbstractVerticle {

    private long counter;

    private final static String HOSTNAME = System.getenv("HOSTNAME");
    private HttpServer server;

    @Override
    public void start() throws Exception {
        vertx.setTimer(10000, l -> {
            Router router = Router.router(vertx);

            router.get("/").handler(rc -> rc.response().end("OK"));
            router.get("/api/hello").handler(this::handleHello);
            router.get("/api/stop").handler(this::handleStop);

            server = vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
        });
    }

    private void handleStop(RoutingContext rc) {
        rc.response().end("That was the famous last words from " + HOSTNAME);
        server.close();
    }

    private void handleHello(RoutingContext rc) {
        String name = rc.request().getParam("name");
        if (name == null) {
            name = "Monde !";
        }
        rc.response()
            .putHeader(CONTENT_TYPE, "application/json;charset=UTF-8")
            .end(new JsonObject()
                .put("message", "Hello " + name)
                .put("counter", counter++)
                .put("host", HOSTNAME)
                .encode());
    }
}
