package me.escoffier.alpesjug;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.kubernetes.KubernetesServiceImporter;
import io.vertx.servicediscovery.types.HttpEndpoint;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class MyFrontEndVerticle extends AbstractVerticle {


    @Override
    public void start() throws Exception {
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        discovery.registerServiceImporter(new KubernetesServiceImporter(), new JsonObject());

        Router router = Router.router(vertx);

        router.get("/").handler(rc -> {
            retrieveHttpClient(discovery)
                .compose(this::callService)
                .setHandler(ar -> {
                    if (ar.failed()) {
                        rc.response().setStatusCode(403).end(ar.cause().getMessage());
                    } else {
                        rc.response().setStatusCode(200).end(ar.result());
                    }
                });
        });

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(8080);
    }

    private Future<HttpClient> retrieveHttpClient(ServiceDiscovery discovery) {
        Future<HttpClient> future = Future.future();
        HttpEndpoint.getClient(discovery,
            record -> record.getName().equals("my-rest-service"),
            future.completer());
        return future;
    }

    public Future<String> callService(HttpClient client) {
        Future<String> future = Future.future();
        client.get("/api/hello?name=alpesjug", response ->
            response
                .exceptionHandler(future::fail)
                .bodyHandler(buffer -> future.complete(buffer.toString())))
            .exceptionHandler(future::fail)
            .setTimeout(3000)
            .end();
        return future;
    }
}
