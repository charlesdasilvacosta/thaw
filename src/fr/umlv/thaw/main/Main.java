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

        int id = -1;

        try {
            id = database.retrieveIdByToken("717fe34eb651c28a3bdfe44f863cdcccbf343f33");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(id);

        serv.getVertx().deployVerticle(serv);
    }
}
