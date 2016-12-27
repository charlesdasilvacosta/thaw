package fr.umlv.thaw.server;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by qbeacco on 20/11/16.
 */
public class TestCertificate {

    @Test(expected = NullPointerException.class)
    public void testCertificateNotNull(){
        new Certificate(null,"pass");
        new Certificate(Paths.get("test"),null);
        new Certificate(null,null);
    }

    @Test
    public void testGetStringCertPath(){
        //assertEquals("montest", new Certificate(Paths.get("montest"),"monpassword").getStringCertPath());
    }

    @Test
    public void testGetpassword(){
        assertEquals("monpassword", new Certificate(Paths.get("montest"),"monpassword").getPassword());
    }
}
