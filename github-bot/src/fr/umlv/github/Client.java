package fr.umlv.github;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by qbeacco on 21/12/16.
 */
class Client {
    private final URLConnection connection;

    public Client(URL url) throws IOException {
        connection = url.openConnection();
    }

    public String getContent() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

        String line;
        StringBuilder result = new StringBuilder();

        while((line = input.readLine()) != null){
            result.append(line);
        }

        input.close();

        return result.toString();
    }
}
