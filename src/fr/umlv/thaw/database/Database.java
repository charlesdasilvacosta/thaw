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
 * Created by qbeacco on 18/10/16.
 */
public class Database {
    private final Path databasePath;
    private Connection connection = null;
    private Statement statement = null;

    public Database(Path databasePath) {
        this.databasePath = databasePath;
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            statement = connection.createStatement();
            System.out.println("Connection: ok");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Connection: failed");
        }
    }

    private void disconnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void sendRequest(String request) throws SQLException {
        this.connect();
        statement.executeUpdate(request);
        this.disconnect();
    }

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

    /*
     * Must change id to user token
     */
    public void addChannel(String channelName, String token) throws SQLException {
        int ownerId = retrieveIdByToken(token);
        sendRequest(SQLRequest.addChannelToChannelsTable(channelName, ownerId));
        sendRequest(SQLRequest.addChannelTable(channelName));

    }

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

    /*
     * Useful to get id of created channel
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

    public void addMessage(String token, Integer channelId, String message) throws SQLException {
        StringBuilder requestInsertMessage = new StringBuilder();

        /*requestInsertMessage.append("insert into ").append(retrieveChannelName(channelId))
                .append(" values")
                .append("(").append(retrieveIdByToken(token)).append(",").append("datetime(CURRENT_TIMESTAMP,'LOCALTIME')").append(",\"").append(message).append("\")");
           */

        sendRequest(SQLRequest.addMessage(retrieveIdByToken(token),retrieveChannelName(channelId),message));
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

    /*
     * This method return the new token of connected user
     */
    public JsonObject connectUser(String login, String password) throws SQLException {
        String newToken = SHA1.hash(RandonString.randomString());
        if (userExist(login, password)) {
            sendRequest(SQLRequest.connectUser(login,password,newToken));
            return new JsonObject().put("token", newToken);
        }
        return new JsonObject().put("token", "null");

    }


    /*
     * Unused method, must delete it after
     */
    private String retrieveUserName(int id) throws SQLException {
        ResultSet resultSet;
        String buffer;
        connect();
        resultSet = statement.executeQuery("select name from users where id_user = " + id);
        buffer = resultSet.getString("name");
        disconnect();
        return buffer;
    }


    private boolean userExist(String login, String password) throws SQLException {
        ResultSet resultSet;
        boolean flag = false;
        connect();
        resultSet = statement.executeQuery(SQLRequest.userExist(login,password));
        while (resultSet.next()) {
            if (login.equals(resultSet.getString("login")) && password.equals(resultSet.getString("password"))) {
                flag = true;
            }
        }
        disconnect();
        return flag;
    }

    private String retrieveChannelName(int id) throws SQLException {
        ResultSet resultSet;
        String buffer;
        connect();
        resultSet = statement.executeQuery(SQLRequest.retrieveChannelName(id));
        buffer = resultSet.getString("name");
        disconnect();
        return buffer;
    }

    public int retrieveIdByToken(String token) throws SQLException {
        ResultSet resultSet;
        int buffer = 0;
        connect();
        resultSet = statement.executeQuery(SQLRequest.retrieveIdByToken(token));
        buffer = resultSet.getInt("id_user");
        disconnect();

        if (buffer == 0) {
            throw new SQLException("Unknown token");
        }

        return buffer;
    }

    public void deleteChannel(String name) throws SQLException {
        StringBuilder request1= new StringBuilder();
        StringBuilder request2= new StringBuilder();
        request1.append("DROP TABLE ch_").append(name);
        request2.append("DELETE FROM CHANNELS WHERE name like \"ch_").append(name).append("\";");
        sendRequest(request1.toString());
        sendRequest(request2.toString());
    }

    public String getTimeDatabase(){
        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new java.sql.Timestamp(System.currentTimeMillis()));

    }

}
