package fr.umlv.thaw.server;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by Quentin Béacco and Charles Dasilva Costa
 * Thaw Project M1 Informatique
 */
public class Certificate {
    private final Path certPath;
    private final String password;

    /**
     * Constructor for certificate
     * @param certPath paths of jks (JavaKeyStore) file
     * @param password the password of jks file
     */
    public Certificate(Path certPath, String password) {
        this.certPath = Objects.requireNonNull(certPath);
        this.password = Objects.requireNonNull(password);
    }

    /**
     * Getter for certificate path
     * @return the path of certificate
     */
    public Path getCertPath() {
        return certPath;
    }

    /**
     * Getter for password
     * @return the password of jks file
     */
    public String getPassword() {
        return password;
    }
}
