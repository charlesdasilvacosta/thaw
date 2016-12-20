package fr.umlv.thaw.database;

import fr.umlv.thaw.security.RandonString;
import fr.umlv.thaw.security.SHA1;
import javafx.beans.binding.When;

/**
 * Created by qbeacco on 19/12/16.
 */
class SQLRequest {

    public static String listUsers() {
        return "select id_user, name from users";
    }

    public static String addChannelToChannelsTable(String channelName, Integer ownerId) {
        StringBuilder request = new StringBuilder();
        request.append("insert into channels ")
                .append("(name, owner_id) values ")
                .append("( \"ch_")
                .append(channelName)
                .append("\",")
                .append(ownerId)
                .append(")");

        return request.toString();
    }

    public static String addChannelTable(String channelName) {
        StringBuilder request = new StringBuilder();

        request.append("CREATE TABLE \"ch_").append(channelName).append("\" ( ")
                .append("author_id integer not null,")
                .append("post_date datetime not null,")
                .append("message varchar(140) not null,")
                .append("foreign key (author_id) references users(id_users))");

        return request.toString();
    }

    public static String listChannels() {
        return "select * from channels";
    }

    public static String getSeqChanbnel() {
        return "select seq from sqlite_sequence where name=\"channels\"";
    }

    public static String addMessage(Integer userId, String channelName, String message) {
        StringBuilder request = new StringBuilder();

        request.append("insert into ")
                .append(channelName)
                .append(" values")
                .append("(").append(userId).append(",").append("datetime(CURRENT_TIMESTAMP,'LOCALTIME')").append(",\"").append(message).append("\")");

        return request.toString();
    }

    public static String listMessageByChannel(String channelName){
        return new StringBuilder().append("select * from ").append(channelName).toString();
    }

    public static String addUser(String name, String login, String password, String token){
        StringBuilder request = new StringBuilder();

        request.append("insert into users ")
                .append("(name, login, password, token) values ")
                .append("(\"").append(name).append("\",")
                .append("\"").append(login).append("\",")
                .append("\"").append(password).append("\",")
                .append("\"").append(token).append("\")");

        return request.toString();
    }

    public static String connectUser(String login, String password, String newToken){
        StringBuilder request = new StringBuilder();

        request.append("update users set token = \"")
                .append(newToken)
                .append("\" where login = \"")
                .append(login)
                .append("\" and password = \"")
                .append(password).append("\"");

        return request.toString();
    }

    public static String userExist(String login, String password){
        StringBuilder request = new StringBuilder();

        request.append("select login, password from users where login = \"")
                .append(login)
                .append("\"")
                .append("and password = \"")
                .append(password)
                .append("\"");

        return request.toString();
    }

    public static String retrieveChannelName(int id){
        return "select name from channels where id_channel = " + id;
    }

    public static String retrieveIdByToken(String token){
        return "select id_user from users where token = \"" + token + "\"";
    }
}
