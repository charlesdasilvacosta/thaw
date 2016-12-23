package fr.umlv.thaw.server;

import fr.umlv.thaw.database.Database;
import fr.umlv.thaw.security.SHA1;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by qbeacco on 20/11/16.
 */
public class Request {

    private final Router router;
    private final Database database;
    private final Vertx vertx;

    public Request(Router router, Database database, Vertx vertx) {
        this.router = Objects.requireNonNull(router);
        this.database = Objects.requireNonNull(database);
        this.vertx = Objects.requireNonNull(vertx);
    }

    public Router getRouter() {
        return router;
    }

    public void useRequests() {
        protectCookies(vertx);
        allowRequest();
        requestPath();
    }

    /*
         * Catch SQLException without code replica
         */
    private void tryCatchSQLException(Request.CheckedSQLConsumer<RoutingContext> consumer, RoutingContext routingContext) {
        try {
            consumer.accept(routingContext);
        } catch (SQLException sqlException) {
            routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "Error")));
        }
    }

    private void allowRequest() {
        CorsHandler corsHandler = CorsHandler.create("*");
        corsHandler.allowedMethod(HttpMethod.OPTIONS);
        corsHandler.allowedMethod(HttpMethod.GET);
        corsHandler.allowedMethod(HttpMethod.POST);
        corsHandler.allowedMethod(HttpMethod.DELETE);
        corsHandler.allowedMethod(HttpMethod.PUT);
        corsHandler.allowedHeader("Authorization");
        corsHandler.allowedHeader("www-authenticate");
        corsHandler.allowedHeader("Content-Type");
        router.route().handler(corsHandler);
    }

    private void protectCookies(Vertx vertx) {
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler
                .create(LocalSessionStore.create(vertx))
                .setCookieHttpOnlyFlag(true)
                .setCookieSecureFlag(true));
    }

    private void requestPath() {


        BridgeOptions options = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("channel[0-9]+"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("channels"));
        SockJSHandler bridge = SockJSHandler.create(vertx).bridge(options);


        router.route().handler(BodyHandler.create());
        //router.route().handler(StaticHandler.create("front/"));
        router.route("/*").handler(bridge);

        router.get("/users").handler((routingContext) -> tryCatchSQLException(this::listAllUsers, routingContext));
        router.get("/channels").handler((routingContext) -> tryCatchSQLException(this::listAllChannels, routingContext));
        router.get("/message/:channelid").handler((routingContest) -> tryCatchSQLException(this::getAllMessage, routingContest));
        router.put("/channel/:token/:name").handler((routingContest) -> tryCatchSQLException(this::addChannel, routingContest));
        router.put("/message/:token/:channelid/:message").handler((routingContext) -> tryCatchSQLException(this::sendMessage, routingContext));
        router.put("/newuser/:name/:login/:password").handler((routingContest) -> tryCatchSQLException(this::addNewUser, routingContest));
        router.post("/connect/:login/:password").handler(routingContext -> tryCatchSQLException(this::connect,routingContext));
    }

    private void listAllUsers(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(database.listAllUsers()));
    }

    private void addChannel(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        String name = routingContext.request().getParam("name");
        String token = routingContext.request().getParam("token");

        database.addChannel(name, token);

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "Channel added successfully")));


        vertx.eventBus().publish("channels", new JsonObject()
                .put("id_channel",(database.getSeqChannel()))
                .put("name", name)
                .put("ownerid",database.retrieveIdByToken(token)));
    }

    private void listAllChannels(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(database.listAllChannels()));
    }

    //Modify to use token
    private void sendMessage(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        database.addMessage(
                routingContext.request().getParam("token"),
                Integer.parseInt(routingContext.request().getParam("channelid")),
                routingContext.request().getParam("message")
        );

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "Message added successfully")));
        System.out.println("envoi sur channel "+routingContext.request().getParam("channelid"));

        vertx.eventBus().publish("channel"+Integer.parseInt(routingContext.request().getParam("channelid")),
                new JsonObject().put("authorid",database.retrieveIdByToken(routingContext.request().getParam("token")))
                .put("channelid",Integer.parseInt(routingContext.request().getParam("channelid")))
                .put("message",routingContext.request().getParam("message")));
    }

    private void getAllMessage(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(database.listMessagesByChannelId(Integer.parseInt(routingContext.request().getParam("channelid")))));
    }

    private void addNewUser(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();
        String name = routingContext.request().getParam("name");
        String login = routingContext.request().getParam("login");
        String password = SHA1.hash(routingContext.request().getParam("password"));

        database.addNewUser(name,login,password);

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert","user create successfully")));


    }

    private void connect(RoutingContext routingContext) throws SQLException{
        HttpServerResponse response = routingContext.response();

        String login = routingContext.request().getParam("login");
        String password = SHA1.hash(routingContext.request().getParam("password"));

        response.putHeader("content-type","application/json").end(Json.encodePrettily(database.connectUser(login,password)));

    }

    /*
     * Private interface to catch exception for request method
     */
    @FunctionalInterface
    private interface CheckedSQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

}
