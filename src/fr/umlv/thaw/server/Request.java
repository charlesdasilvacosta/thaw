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
 * Created by Quentin Béacco and Charles Da Silva Costa
 * Thaw Project M1 Informatique
 */
public class Request {

    private final Router router;
    private final Database database;
    private final Vertx vertx;

    /**
     * Constructor for Request object
     *
     * @param router   used for all http request
     * @param database for adding, updating, and list request result
     * @param vertx    used for cookies protection and eventbus(proprietary websocket)
     */
    public Request(Router router, Database database, Vertx vertx) {
        this.router = Objects.requireNonNull(router);
        this.database = Objects.requireNonNull(database);
        this.vertx = Objects.requireNonNull(vertx);
    }

    /**
     * Getter, wich return router
     *
     * @return Router
     */
    public Router getRouter() {
        return router;
    }

    /**
     * Must call from outside to use request engine
     */
    public void useRequests() {
        protectCookies();
        allowRequest();
        requestPath();
    }

    /**
     * This method used internal interface for catching SQLException, without code replica
     *
     * @param consumer       method wich executed when http request is called
     * @param routingContext the routing context of actual request
     */
    private void tryCatchSQLException(CheckedSQLConsumer<RoutingContext> consumer, RoutingContext routingContext) {
        try {
            consumer.accept(routingContext);
        } catch (SQLException sqlException) {
            routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "Error")));
        }
    }

    /**
     * This method is used to allow all authorization for request over web browser and front
     */
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

    /**
     * This method activate secure flag into cookies
     */
    private void protectCookies() {
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler
                .create(LocalSessionStore.create(vertx))
                .setCookieHttpOnlyFlag(true)
                .setCookieSecureFlag(true));
    }

    /**
     * This method, activate eventbus and list all request allow by server
     */
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
        router.get("/message/:channelid").handler((routingContest) -> tryCatchSQLException(this::listAllMessageByChannel, routingContest));
        router.put("/channel/:token/:name").handler((routingContest) -> tryCatchSQLException(this::addChannel, routingContest));
        router.put("/message/:token/:channelid/:message").handler((routingContext) -> tryCatchSQLException(this::sendMessage, routingContext));
        router.put("/newuser/:name/:login/:password").handler((routingContest) -> tryCatchSQLException(this::addNewUser, routingContest));
        router.post("/connect/:login/:password").handler(routingContext -> tryCatchSQLException(this::connect, routingContext));
        router.delete("/channel/:name").handler((routingContest) -> tryCatchSQLException(this::deleteChannel, routingContest));
    }

    /**
     * This method delete a channel
     *
     * @param routingContext useful for response
     * @throws SQLException when have problem into sql query
     */
    private void deleteChannel(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();
        database.deleteChannel(routingContext.request().getParam("name"));
        response.putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "Channel deleted successfully")));

        vertx.eventBus().publish("channels", new JsonObject()
                .put("etat", "delete")
                .put("name", routingContext.request().getParam("name")));
    }

    /**
     * This method list all user in request response
     *
     * @param routingContext useful for response
     * @throws SQLException when have problem into sql query
     */
    private void listAllUsers(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(database.listAllUsers()));
    }

    /**
     * This method add channel, use user token for mor safety
     *
     * @param routingContext useful for response and retrieve info sent over http request
     * @throws SQLException when have problem into sql query
     */
    private void addChannel(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        String name = routingContext.request().getParam("name");
        String token = routingContext.request().getParam("token");

        database.addChannel(name, token);

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "Channel added successfully")));

        vertx.eventBus().publish("channels", new JsonObject()
                .put("etat", "add")
                .put("id_channel", (database.getSeqChannel()))
                .put("name", name)
                .put("ownerid", database.retrieveUserIdByToken(token)));
    }

    /**
     * This method list all channel
     *
     * @param routingContext useful for response
     * @throws SQLException when have problem into sql query
     */
    private void listAllChannels(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(database.listAllChannels()));
    }

    /**
     * This method send message, use user token for safety
     *
     * @param routingContext useful for response and retrieve info send over http request
     * @throws SQLException when have problem into sql query
     */
    private void sendMessage(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        database.addMessage(
                routingContext.request().getParam("token"),
                Integer.parseInt(routingContext.request().getParam("channelid")),
                routingContext.request().getParam("message")
        );

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "Message added successfully")));


        vertx.eventBus().publish("channel" + Integer.parseInt(routingContext.request().getParam("channelid")),
                new JsonObject().put("authorid", database.retrieveUserIdByToken(routingContext.request().getParam("token")))
                        .put("channelid", Integer.parseInt(routingContext.request().getParam("channelid")))
                        .put("message", routingContext.request().getParam("message"))
                        .put("post_date",database.getTimeDatabase()));
    }

    /**
     * This method list all message by channel
     *
     * @param routingContext useful for response and retrieve info over http request
     * @throws SQLException when have problem with sql query
     */
    private void listAllMessageByChannel(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(database.listMessagesByChannelId(Integer.parseInt(routingContext.request().getParam("channelid")))));
    }

    /**
     * This metho add new user
     *
     * @param routingContext useful for response and retrieve info over http method
     * @throws SQLException when have problem with sql query
     */
    private void addNewUser(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();
        String name = routingContext.request().getParam("name");
        String login = routingContext.request().getParam("login");
        String password = SHA1.hash(routingContext.request().getParam("password"));

        database.addNewUser(name, login, password);

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(new JsonObject().put("alert", "user create successfully")));


    }

    /**
     * This method connect user, create new token, each time the user log in
     *
     * @param routingContext useful for response and retrieve info over http request
     * @throws SQLException when have problem whith sql query
     */
    private void connect(RoutingContext routingContext) throws SQLException {
        HttpServerResponse response = routingContext.response();

        String login = routingContext.request().getParam("login");
        String password = SHA1.hash(routingContext.request().getParam("password"));

        response.putHeader("content-type", "application/json").end(Json.encodePrettily(database.connectUser(login, password)));

    }

    /**
     * This functional interface used to catch SQL Exception
     *
     * @param <T> actually, we use RoutingContext but you can use anything who throw SQLException
     */
    @FunctionalInterface
    private interface CheckedSQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

}
