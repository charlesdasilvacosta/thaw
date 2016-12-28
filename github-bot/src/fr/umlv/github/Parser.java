package fr.umlv.github;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by qbeacco on 20/12/16.
 */
class Parser {
    private Properties properties;

    public static Parser setParser(Path path) throws IOException{
        Parser parser = new Parser();
        parser.loadFile(path);
        return parser;
    }

    private Parser() throws IOException {
        properties = new Properties();
    }

    private void loadFile(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        }
    }

    public String getAccessToken() {
        return properties.getProperty("access_token");
    }

    public String getRepoOwner() {
        return properties.getProperty("repo_owner");
    }

    public String getRepoName() {
        return properties.getProperty("repo_name");
    }
}
