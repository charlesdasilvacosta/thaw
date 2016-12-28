package fr.umlv.github;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

/**
 * Package name: fr.umlv.github.properties
 * Created by Quentin Béacco and Charles Dasilva Costa
 * Thaw Project M1 Informatique
 */
public class Json {
    private final JsonNode jsonNode;

    public Json(String jsonValue) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(Objects.requireNonNull(jsonValue));
        jsonNode = mapper.readTree(parser);

        if(jsonNode == null)
            throw new IllegalStateException("JsonNode is null");

    }

    public String getCommits(){
        return jsonNode.get("commit").toString();
    }
}
