package com.example.funnychat.clientserver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final public class ChatCommandsHandler {

    // ***************** SERVER TO CLIENT COMMANDS *****************

    // Connection and request data commands
    public static final String S_SERVER_STARTED = "S_SERVER_STARTED";
    public static final String S_SERVER_DISCONNECTED = "S_SERVER_DISCONNECTED";
    public static final String S_SERVER_INIT_ERROR = "S_SERVER_INIT_ERROR";
    public static final String S_SERVER_SYSTEM_ERROR = "S_SERVER_SYSTEM_ERROR";
    public static final String S_CLIENT_CONNECTED = "S_CLIENT_CONNECTED";
    public static final String S_CLIENT_DISCONNECTED = "S_CLIENT_DISCONNECTED";
    public static final String S_INVALID_REQUEST = "S_INVALID_REQUEST";

    // Authentication commands
    public static final String S_AUTHENTICATION_ERROR = "S_AUTHENTICATION_ERROR";
    public static final String S_PASSWORD_MISMATCH = "S_PASSWORD_MISMATCH";
    public static final String S_AUTHENTICATED = "S_AUTHENTICATED";

    // Data commands
    public static final String S_DATA_UPDATED = "S_DATA_UPDATED";
    public static final String S_DATA_CHANNELS_RECEIVED = "S_DATA_CHANNELS_RECEIVED";
    public static final String S_DATA_USERS_RECEIVED = "S_DATA_USERS_RECEIVED";
    public static final String S_DATA_MESSAGES_RECEIVED = "S_DATA_MESSAGES_RECEIVED";
    public static final String S_NOTIFICATION = "S_NOTIFICATION";

    // ***************** CLIENT TO SERVER COMMANDS *****************

    // Authentication commands
    public static final String C_AUTH_REQUEST = "C_AUTH_REQUEST";

    // Data commands
    public static final String C_SET_CHANNEL_ID = "C_SET_CHANNEL_ID";
    public static final String C_SEND_MESSAGE = "C_SEND_MESSAGE";
    public static final String C_GET_CHANNELS = "C_GET_CHANNELS";
    public static final String C_GET_USERS = "C_GET_USERS";
    public static final String C_GET_MESSAGES = "C_GET_MESSAGES";

    // Data field names
    public static final String ID = "id";
    public static final String AUTH_ID = "authId";
    public static final String OWN_USER_ID = "ownUserId";
    public static final String CHANNEL_ID = "channelId";
    public static final String RCP_USER_ID = "rcpUserId";
    public static final String CHANNELS = "channels";
    public static final String USERS = "users";
    public static final String USER = "user";
    public static final String MESSAGES = "messages";
    public static final String MESSAGE = "message";
    public static final String TEXT = "text";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String SEX = "sex";

    // ***************** COMMAND FUNCTIONS *****************

    /*
        Simple command

        { "$COMMAND": $Object }
    */
    final public static JSONObject wrapCommand(String command, Object val) {
        try {
            final JSONObject DATA = new JSONObject();
            DATA.put(command, val);

            return DATA;
        } catch (JSONException e) {
            return null;
        }
    }

    final public static JSONObject wrapCommand(String command) {
        return wrapCommand (command, 0);
    }

    /*
        Send new message command structure

        {
            "C_SEND_MESSAGE": {
                "authId": 100,
                "message": {"ownUserId": 100, "channelId": 0, "rcpUserId": 0, "text": "..."}
            }
        }
    */
    final public static JSONObject makeCommandSendMessage (
            Integer authId, Integer channelId, Integer rcpUserId, String text) {
        try {
            final JSONObject MESSAGE_OBJ = new JSONObject();
            MESSAGE_OBJ.put(OWN_USER_ID, authId);
            MESSAGE_OBJ.put(CHANNEL_ID, channelId);
            MESSAGE_OBJ.put(RCP_USER_ID, rcpUserId);
            MESSAGE_OBJ.put(TEXT, text);

            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(AUTH_ID, authId);
            COMMAND_OBJ.put(MESSAGE, MESSAGE_OBJ);

            return wrapCommand(C_SEND_MESSAGE, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Set new channel command structure

        {
            "C_SET_CHANNEL_ID": {
                "authId": 100,
                "user": {"channelId": 2}
            }
        }
    */
    final public static JSONObject makeCommandSelectChannel (Integer authId, Integer channelId) {
        try {
            final JSONObject USER_OBJ = new JSONObject();
            USER_OBJ.put(ID, authId);
            USER_OBJ.put(CHANNEL_ID, channelId);

            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(AUTH_ID, authId);
            COMMAND_OBJ.put(USER, USER_OBJ);

            return wrapCommand(C_SET_CHANNEL_ID, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Get channels command structure

        { "C_GET_CHANNELS": {"authId": 100} }
    */
    final public static JSONObject makeCommandGetChannels (Integer authId) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(AUTH_ID, authId);

            return wrapCommand(C_GET_CHANNELS, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Get user list command structure

        { "C_GET_USERS": {"authId": 100, "channelId": 1} }
    */
    final public static JSONObject makeCommandGetUsers (Integer authId, Integer channelId) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(AUTH_ID, authId);
            COMMAND_OBJ.put(CHANNEL_ID, channelId);

            return wrapCommand(C_GET_USERS, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Get message list command structure

        { "C_GET_MESSAGES": {"authId": 100, "channelId": 1, "rcpUserId": 0} }
    */
    final public static JSONObject makeCommandGetMessages (Integer authId, Integer channelId,
                                                           Integer rcpUserId) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(AUTH_ID, authId);
            COMMAND_OBJ.put(CHANNEL_ID, channelId);
            COMMAND_OBJ.put(RCP_USER_ID, rcpUserId);

            return wrapCommand(C_GET_MESSAGES, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Client authentication request command structure

        { "C_AUTH_REQUEST": {"login": "Bob", "password": "...", "sex": "male"} }
    */
    final public static JSONObject makeCommandAuthRequest(String login, String pwd, String sex) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(LOGIN, login);
            COMMAND_OBJ.put(PASSWORD, pwd);
            COMMAND_OBJ.put(SEX, sex);

            return wrapCommand(C_AUTH_REQUEST, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Server authentication successfully message structure

        { "S_AUTHENTICATED": {"authId": 100} }
    */
    final public static JSONObject makeCommandAuthenticated(Integer authId, Integer channelId) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(AUTH_ID, authId);
            COMMAND_OBJ.put(CHANNEL_ID, channelId);

            return wrapCommand(S_AUTHENTICATED, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Server send channels to client

        { "S_DATA_CHANNELS_RECEIVED": {"channels": [{..}, {..}]} }
    */
    final public static JSONObject makeCommandSendChannelsToClient(JSONArray channels) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(CHANNELS, channels);

            return wrapCommand(S_DATA_CHANNELS_RECEIVED, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Server send channels to client

        { "S_DATA_USERS_RECEIVED": {"users": [{..}, {..}]} }
    */
    final public static JSONObject makeCommandSendUsersToClient(JSONArray users) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(USERS, users);

            return wrapCommand(S_DATA_USERS_RECEIVED, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

    /*
        Server send messages to client

        { "S_DATA_MESSAGES_RECEIVED": {"users": [{..}, {..}]} }
    */
    final public static JSONObject makeCommandSendMessagesToClient (JSONArray messages) {
        try {
            final JSONObject COMMAND_OBJ = new JSONObject();
            COMMAND_OBJ.put(MESSAGES, messages);

            return wrapCommand(S_DATA_MESSAGES_RECEIVED, COMMAND_OBJ);
        } catch (JSONException e) {
            return null;
        }
    }

}