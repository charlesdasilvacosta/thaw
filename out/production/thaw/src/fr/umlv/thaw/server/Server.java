package fr.umlv.thaw.server;


import fr.umlv.thaw.database.Database;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by qbeacco on 18/10/16.
 */
public class Server extends AbstractVerticle {

    private final Database database;
    private final Certificate cert;
    private final Vertx vertx;
    private final int port;

    public Server(Database database, Certificate cert, int port) {
        this.database = Objects.requireNonNull(database);
        this.cert = Objects.requireNonNull(cert);
        this.vertx = Vertx.vertx();
        this.port = Objects.requireNonNull(port);

    }

    @Override
    public void start() {

        Request requests = new Request(Router.router(this.vertx), this.database, this.vertx);

        requests.useRequests();
        /*
         * Https supports with ssl certificate
         */

        vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyStoreOptions(
                new JksOptions().setPath(cert.getStringCertPath()).setPassword(cert.getPassword())
        )).requestHandler(requests.getRouter()::accept).listen(port);

        System.out.println("Vert.x server is running");
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

}
