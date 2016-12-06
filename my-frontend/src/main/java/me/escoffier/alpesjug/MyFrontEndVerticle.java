package me.escoffier.alpesjug;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.kubernetes.KubernetesServiceImporter;
import io.vertx.servicediscovery.types.HttpEndpoint;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class MyFrontEndVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        ServiceDiscovery discovery =
            ServiceDiscovery.create(vertx);
        discovery.registerServiceImporter(
            new KubernetesServiceImporter(), new JsonObject());

        Router router = Router.router(vertx);

        router.get("/stop").handler(rc -> {
            rc.response()
                .end("The last words from " + System.getenv("HOSTNAME"));
            System.exit(-1);
        });
        router.get("/").handler(rc -> {
           findMyService(discovery)
            .compose(this::callMyService)
           .setHandler(ar -> {
               if (ar.failed()) {
                   rc.response().setStatusCode(400)
                       .end(ar.cause().getMessage());
               } else {
                   rc.response()
                       .putHeader(CONTENT_TYPE, "text/html")
                       .end(
                           "<h3>" + ar.result().getString("message") + "</h3><p>Served by " + ar.result().getString("hostname") + "</p>");
               }
           });
        });

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(8080);

    }

    private Future<JsonObject> callMyService(
        HttpClient client) {
        Future<JsonObject> future = Future.future();
        client.get("/api/hello/AlpesJug", response -> {
           response
               .exceptionHandler(future::fail)
               .bodyHandler(buffer -> future.complete(buffer.toJsonObject()));
        })
        .setTimeout(3000)
        .exceptionHandler(future::fail)
        .end();

        return future;
    }

    private Future<HttpClient> findMyService(ServiceDiscovery discovery) {
        Future<HttpClient> future = Future.future();
        HttpEndpoint.getClient(discovery,
            record
                -> record.getName().equals("my-rest-service"), future.completer());
        return future;
    }
}
