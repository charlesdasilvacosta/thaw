package fr.umlv.github.properties;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;

/**
 * Created by qbeacco on 21/12/16.
 */
public class Client extends AbstractVerticle {

    private final Vertx vertx;

    public Client(){
        this.vertx = Vertx.vertx();
    }

    @Override
    public Vertx getVertx(){
        return vertx;
    }

    @Override
    public void start(){
        HttpClientOptions clientOptions = new HttpClientOptions()
                .setDefaultHost("api.github.com");

        HttpClient client = vertx.createHttpClient(clientOptions);

        client.getNow("/repos/MrQwenty/just_uniq/commits", response -> System.out.println(response.statusMessage()));


    }
}
