package fr.umlv.thaw.main;

import fr.umlv.thaw.database.Database;
import fr.umlv.thaw.server.Certificate;
import fr.umlv.thaw.server.Server;

import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * Created by qbeacco on 20/11/16.
 */
public class Main {
    public static void main(String[] args) {
        Database database = new Database(Paths.get("ressources/thaw.db"));
        Certificate cert = new Certificate(Paths.get("ressources/thawkeystore.jks"), "qwenty");
        Server serv = new Server(database, cert, 4443);

        serv.getVertx().deployVerticle(serv);
    }
}
