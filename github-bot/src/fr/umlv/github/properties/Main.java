package fr.umlv.github.properties;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by qbeacco on 20/12/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("github-bot.properties");
        Parser parser = Parser.setParser(path);

        System.out.println(parser.getAccessToken());
    }
}
