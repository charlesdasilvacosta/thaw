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
 * Created by Quentin Béacco and Charles Dasilva Costa
 * Thaw Project M1 Informatique
 */
public class Server extends AbstractVerticle {

    private final Database database;
    private final Certificate cert;
    private final Vertx vertx;
    private final int port;

    /**
     * Constructor of server
     * @param database database for adding, updating, and list request result
     * @param cert certificate for https support
     * @param port port for request server
     */
    public Server(Database database, Certificate cert, int port) {
        this.database = Objects.requireNonNull(database);
        this.cert = Objects.requireNonNull(cert);
        this.vertx = Vertx.vertx();
        this.port = Objects.requireNonNull(port);

    }

    /**
     * This method is the main component of server
     */
    @Override
    public void start() {

        Request requests = new Request(Router.router(this.vertx), this.database, this.vertx);

        requests.useRequests();
        /*
         * Https supports with ssl certificate
         */

        vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyStoreOptions(
                new JksOptions().setPath(cert.getCertPath().toString()).setPassword(cert.getPassword())
        )).requestHandler(requests.getRouter()::accept).listen(port);

        System.out.println("Vert.x server is running");
    }

    /**
     * Getter for vertx object
     * @return return vertx of actual object
     */
    @Override
    public Vertx getVertx() {
        return vertx;
    }

}
