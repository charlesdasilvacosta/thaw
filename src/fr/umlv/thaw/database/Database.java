package fr.umlv.thaw.database;

import fr.umlv.thaw.security.RandonString;
import fr.umlv.thaw.security.SHA1;
import io.vertx.core.json.JsonObject;

import java.nio.file.Path;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quentin Béacco and Charles Da Silva Costa
 * Thaw Project M1 Informatique
 */
public class Database {
    private final Path databasePath;
    private Connection connection = null;
    private Statement statement = null;

    /**
     * Constructor of database object
     *
     * @param databasePath the path of database file
     */
    public Database(Path databasePath) {
        this.databasePath = databasePath;
    }

    /**
     * Private method to connect to database
     */
    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Connection: failed");
        }
    }

    /**
     * Private method to disconnect to database
     */
    private void disconnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Private method to update, create or delete things on database (not available for query)
     *
     * @param request the request
     * @throws SQLException throw if have problem with sql request
     */
    private void sendRequest(String request) throws SQLException {
        this.connect();
        statement.executeUpdate(request);
        this.disconnect();
    }

    /**
     * List all users and collects
     *
     * @return list of json object of users
     * @throws SQLException throw if have problem with sql request
     */
    public List<JsonObject> listAllUsers() throws SQLException {
        List<JsonObject> allUsers = new ArrayList<>();
        ResultSet requestResult;

        connect();
        requestResult = statement.executeQuery(SQLRequest.listUsers());
        while (requestResult.next())
            allUsers.add(new JsonObject()
                    .put("id", requestResult.getInt("id_user"))
                    .put("name", requestResult.getString("name")));
        disconnect();


        return allUsers;

    }

    /**
     * Add channel to database
     *
     * @param channelName channel name
     * @param token       user token
     * @throws SQLException throw if have problem with sql request or channel name already exist (cause, in database, the name is unique) or invalid token
     */
    public void addChannel(String channelName, String token) throws SQLException {
        int ownerId = retrieveUserIdByToken(token);
        sendRequest(SQLRequest.addChannelToChannelsTable(channelName, ownerId));
        sendRequest(SQLRequest.addChannelTable(channelName));

    }

    /**
     * List all channels
     *
     * @return list of json object of all channels
     * @throws SQLException throw if have problem with sql request
     */
    public List<JsonObject> listAllChannels() throws SQLException {
        List<JsonObject> allChannels = new ArrayList<>();
        ResultSet requestResult;

        connect();
        requestResult = statement.executeQuery(SQLRequest.listChannels());
        while (requestResult.next()) {
            allChannels.add(new JsonObject()
                    .put("id_channel", requestResult.getInt("id_channel"))
                    .put("name", requestResult.getString("name").substring(3))
                    .put("owner_id", requestResult.getInt("owner_id")));
        }
        disconnect();
        return allChannels;
    }

    /**
     * Get the id of last channel created
     *
     * @return the id of last channel created
     * @throws SQLException throw if have problem with sql request
     */
    public int getSeqChannel() throws SQLException {
        int seq;
        ResultSet requestResult;
        connect();
        requestResult = statement.executeQuery(SQLRequest.getSeqChannel());
        seq = requestResult.getInt("seq");
        disconnect();
        return seq;
    }

    /**
     * Add message to database
     *
     * @param token     user token
     * @param channelId channel id
     * @param message   message body
     * @throws SQLException throw if have problem with sql request of invalid token
     */
    public void addMessage(String token, Integer channelId, String message) throws SQLException {
        sendRequest(SQLRequest.addMessage(retrieveUserIdByToken(token), retrieveChannelName(channelId), message));
    }

    public List<JsonObject> listMessagesByChannelId(Integer channelId) throws SQLException {
        List<JsonObject> allMessages = new ArrayList<>();
        ResultSet requestResult;
        String channelName = retrieveChannelName(channelId);

        connect();

        requestResult = statement.executeQuery(SQLRequest.listMessageByChannel(channelName));
        while (requestResult.next()) {
            allMessages.add(new JsonObject()
                    .put("author_id", requestResult.getInt("author_id"))
                    .put("post_date", requestResult.getString("post_date"))
                    .put("message", requestResult.getString("message")));
        }

        disconnect();

        return allMessages;
    }

    public void addNewUser(String name, String login, String password) throws SQLException {
        StringBuilder requestCreateUser = new StringBuilder();

        sendRequest(SQLRequest.addUser(name, login, password, SHA1.hash(RandonString.randomString())));

    }

    /**
     * Return the new token of connected user
     *
     * @param login    login or user
     * @param password password of user
     * @return the new token of connected user or if user doesn't exist, "null" has returned and front will handle this data
     * @throws SQLException throw if have problem with sql request
     */
    public JsonObject connectUser(String login, String password) throws SQLException {
        String newToken = SHA1.hash(RandonString.randomString());
        if (userExist(login, password)) {
            sendRequest(SQLRequest.connectUser(login, password, newToken));
            return new JsonObject().put("token", newToken);
        }
        return new JsonObject().put("token", "null");

    }

    /**
     * Return just boolean to say if user exit of no
     *
     * @param login    login of user
     * @param password password of user
     * @return true, if user exist or false
     * @throws SQLException throw if have problem with sql request
     */
    private boolean userExist(String login, String password) throws SQLException {
        ResultSet resultSet;
        boolean flag = false;
        connect();
        resultSet = statement.executeQuery(SQLRequest.userExist(login, password));
        while (resultSet.next()) {
            if (login.equals(resultSet.getString("login")) && password.equals(resultSet.getString("password"))) {
                flag = true;
            }
        }
        disconnect();
        return flag;
    }

    /**
     * Return the name of channel by his id
     *
     * @param id the id of channel
     * @return the name of channel
     * @throws SQLException throw if have problem with sql request or if id is wrong
     */
    private String retrieveChannelName(int id) throws SQLException {
        ResultSet resultSet;
        String buffer;
        connect();
        resultSet = statement.executeQuery(SQLRequest.retrieveChannelName(id));
        buffer = resultSet.getString("name");
        disconnect();
        return buffer;
    }

    /**
     * Return the id by the user token
     *
     * @param token user token
     * @return the id of user token
     * @throws SQLException throw if have problem with sql request or token is wrong
     */
    public int retrieveUserIdByToken(String token) throws SQLException {
        ResultSet resultSet;
        int buffer = 0;
        connect();
        resultSet = statement.executeQuery(SQLRequest.retrieveUserIdByToken(token));
        buffer = resultSet.getInt("id_user");
        disconnect();

        if (buffer == 0) {
            throw new SQLException("Unknown token");
        }

        return buffer;
    }

    /**
     * Delete channel
     *
     * @param name channel name
     * @throws SQLException throw if have problem with sql request or channel name doesn't exist
     */
    public void deleteChannel(String name) throws SQLException {
        sendRequest(SQLRequest.deleteChannelTable(name));
        sendRequest(SQLRequest.deleteChannelFromChannels(name));
    }

    /**
     * Get the date and time of database
     *
     * @return string contains date and time of database
     */
    public String getTimeDatabase() {
        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));

    }

}
