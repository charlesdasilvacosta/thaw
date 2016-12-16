package fr.umlv.thaw.server;

import fr.umlv.thaw.database.Database;

import static org.junit.Assert.*;

import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by qbeacco on 23/11/16.
 */
public class TestServer {

    @Test()
    public void testNotNullServer() {
        Database d = new Database(Paths.get("ressources/thaw.db"));
        Certificate c = new Certificate(Paths.get("ressources/thawkeystore.jks"), "qwenty");
        new Server(d, c, 4443);
    }

    @Test(expected = NullPointerException.class)
    public void testNullServer() {
        Database d = new Database(Paths.get("ressources/thaw.db"));
        Certificate c = new Certificate(Paths.get("ressources/thawkeystore.jks"), "qwenty");

        new Server(null, c, 8080);
        new Server(d, null, 8000);
    }

    @Test()
    public void testGetVertx() {
        Database d = new Database(Paths.get("ressources/thaw.db"));
        Certificate c = new Certificate(Paths.get("ressources/thawkeystore.jks"), "qwenty");
        Server s = new Server(d, c, 4443);
        assertNotEquals(null, s.getVertx());
    }
}
