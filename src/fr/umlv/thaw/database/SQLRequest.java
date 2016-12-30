package fr.umlv.thaw.database;

/**
 * Created by Quentin Béacco and Charles Da Silva Costa
 * Thaw Project M1 Informatique
 */
class SQLRequest {

    /**
     * Static method build string request for list user
     *
     * @return list user request
     */
    public static String listUsers() {
        return "select id_user, name from users";
    }

    /**
     * Static method build string request for add channel to channels table
     *
     * @param channelName channel name
     * @param ownerId     owner id
     * @return add channel to channels table request
     */
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

    /**
     * Static method build string request for creating channel table
     *
     * @param channelName channel name
     * @return add table channel request
     */
    public static String addChannelTable(String channelName) {
        StringBuilder request = new StringBuilder();

        request.append("CREATE TABLE \"ch_").append(channelName).append("\" ( ")
                .append("author_id integer not null,")
                .append("post_date datetime not null,")
                .append("message varchar(140) not null,")
                .append("foreign key (author_id) references users(id_users))");

        return request.toString();
    }

    /**
     * Static method build string request for list all channels
     *
     * @return list all channels request
     */
    public static String listChannels() {
        return "select * from channels";
    }

    /**
     * Static method build string request for retrieving id of last channel
     *
     * @return sequence channel request
     */
    public static String getSeqChannel() {
        return "select seq from sqlite_sequence where name=\"channels\"";
    }

    /**
     * Static method build string request for adding message
     *
     * @param userId      user id
     * @param channelName channel name
     * @param message     message
     * @return add message request
     */
    public static String addMessage(Integer userId, String channelName, String message) {
        StringBuilder request = new StringBuilder();

        request.append("insert into ")
                .append(channelName)
                .append(" values")
                .append("(").append(userId).append(",").append("datetime(CURRENT_TIMESTAMP,'LOCALTIME')").append(",\"").append(message).append("\")");

        return request.toString();
    }

    /**
     * Static method build string request
     *
     * @param channelName channel name
     * @return list message by channel request
     */
    public static String listMessageByChannel(String channelName) {
        return new StringBuilder().append("select * from ").append(channelName).toString();
    }

    /**
     * Static method build string request for adding user
     *
     * @param name     name of user
     * @param login    lofin of user
     * @param password password of user
     * @param token    access token of user
     * @return add user request
     */
    public static String addUser(String name, String login, String password, String token) {
        StringBuilder request = new StringBuilder();

        request.append("insert into users ")
                .append("(name, login, password, token) values ")
                .append("(\"").append(name).append("\",")
                .append("\"").append(login).append("\",")
                .append("\"").append(password).append("\",")
                .append("\"").append(token).append("\")");

        return request.toString();
    }

    /**
     * Static method build string request for connecting user
     *
     * @param login    login or user
     * @param password password og user
     * @param newToken new access token of user
     * @return connect user request
     */
    public static String connectUser(String login, String password, String newToken) {
        StringBuilder request = new StringBuilder();

        request.append("update users set token = \"")
                .append(newToken)
                .append("\" where login = \"")
                .append(login)
                .append("\" and password = \"")
                .append(password).append("\"");

        return request.toString();
    }

    /**
     * Static method build string request to retrieve info if user exist
     *
     * @param login    login of user
     * @param password password of user
     * @return user exist request
     */
    public static String userExist(String login, String password) {
        StringBuilder request = new StringBuilder();

        request.append("select login, password from users where login = \"")
                .append(login)
                .append("\"")
                .append("and password = \"")
                .append(password)
                .append("\"");

        return request.toString();
    }

    /**
     * Static method build string request to retrieve channel name by his id
     *
     * @param id id of channel
     * @return retrieve channel name by his id request
     */
    public static String retrieveChannelName(int id) {
        return "select name from channels where id_channel = " + id;
    }

    /**
     * Static method build string request to retrieve user id by his token
     *
     * @param token user token
     * @return retrieve id by token request
     */
    public static String retrieveUserIdByToken(String token) {
        return "select id_user from users where token = \"" + token + "\"";
    }

    /**
     * Static method build string request to delete channel table
     *
     * @param name channel name
     * @return delete channel table
     */
    public static String deleteChannelFromChannels(String name) {
        return "DROP TABLE ch_" + name;
    }

    /**
     * Static method build string request to delete channel from channels table
     *
     * @param name channel name
     * @return delete channel from channels table
     */
    public static String deleteChannelTable(String name) {
        StringBuilder request = new StringBuilder();

        request.append("DELETE FROM CHANNELS WHERE name like \"ch_").append(name).append("\";");

        return request.toString();

    }
}
