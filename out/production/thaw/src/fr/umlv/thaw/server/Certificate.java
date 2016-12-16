package fr.umlv.thaw.server;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by qbeacco on 20/11/16.
 */
public class Certificate {
    private final Path certPath;
    private final String password;

    public Certificate(Path certPath, String password) {
        this.certPath = Objects.requireNonNull(certPath);
        this.password = Objects.requireNonNull(password);
    }

    private Path getCertPath() {
        return certPath;
    }

    public String getStringCertPath() {
        return this.getCertPath().toString();
    }

    public String getPassword() {
        return password;
    }
}
