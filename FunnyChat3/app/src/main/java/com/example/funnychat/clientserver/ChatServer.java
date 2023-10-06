package com.example.funnychat.clientserver;

import android.app.Activity;

import com.example.funnychat.util.AssetsHandler;
import com.example.funnychat.util.Hash;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatServer extends WebSocketServer {

    private JSONArray channels = null;
    private JSONArray users = null;
    private JSONArray messages = null;
    private Map<WebSocket, JSONObject> sockets = null;
    private OnActivityDataUpdateListener onActivityDataUpdateListener = null;

    public ChatServer(String host, Integer port) {
        super(new InetSocketAddress(host, port));
    }

    private String init () {
        final AssetsHandler assetsHandler =
                new AssetsHandler((Activity) onActivityDataUpdateListener);

        try {
            JSONObject channelsObj = assetsHandler.parseJSONFile("channels.json");
            channels = channelsObj.getJSONArray("channels");
        } catch (JSONException e) {
            return ChatCommandsHandler.wrapCommand(
                    ChatCommandsHandler.S_SERVER_INIT_ERROR).toString();
        }

        users = new JSONArray();
        messages = new JSONArray();
        sockets = new HashMap<>();

        return ChatCommandsHandler.wrapCommand(
                ChatCommandsHandler.S_SERVER_STARTED).toString();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        final JSONObject user = sockets.get(webSocket);
        if (user != null)
            try {
                user.put("loggedIn", false);
                sockets.remove(webSocket);
                broadcast(ChatCommandsHandler.wrapCommand(
                        ChatCommandsHandler.S_DATA_UPDATED
                ).toString());
            } catch (JSONException e) {}
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        try {
            final JSONObject jsonObject = new JSONObject(s);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                final String command = keys.next();
                final JSONObject data = jsonObject.getJSONObject(command);
                if (command.equals(ChatCommandsHandler.C_AUTH_REQUEST)) {
                    authenticate(data, webSocket);
                } else {
                    final Integer authId = data.getInt(ChatCommandsHandler.AUTH_ID);
                    if (getUserById(authId) == null) {
                        webSocket.send(ChatCommandsHandler.wrapCommand(
                                ChatCommandsHandler.S_AUTHENTICATION_ERROR
                        ).toString());
                    } else {
                        if (command.equals(ChatCommandsHandler.C_GET_CHANNELS)) {
                            webSocket.send(ChatCommandsHandler.
                                    makeCommandSendChannelsToClient(getChannels()).toString());
                        } else if (command.equals(ChatCommandsHandler.C_GET_USERS)) {
                            final Integer channelId = data.getInt(
                                    ChatCommandsHandler.CHANNEL_ID);
                            final JSONArray mUsers = getUsers(channelId);
                            webSocket.send(ChatCommandsHandler.
                                    makeCommandSendUsersToClient(mUsers).toString());
                        } else if (command.equals(ChatCommandsHandler.C_GET_MESSAGES)) {
                            final Integer channelId = data.getInt(ChatCommandsHandler.CHANNEL_ID);
                            final Integer rcpUserId = data.getInt(ChatCommandsHandler.RCP_USER_ID);
                            final JSONArray mMessages = getMessages(authId, channelId, rcpUserId);
                            webSocket.send(ChatCommandsHandler.
                                    makeCommandSendMessagesToClient(mMessages).toString());
                        } else if (command.equals(ChatCommandsHandler.C_SET_CHANNEL_ID)) {
                            final JSONObject user =
                                    data.getJSONObject(ChatCommandsHandler.USER);
                            updateUser(user);
                            broadcast(ChatCommandsHandler.wrapCommand(
                                    ChatCommandsHandler.S_DATA_UPDATED).toString());
                        } else if (command.equals(ChatCommandsHandler.C_SEND_MESSAGE)) {
                            final JSONObject message =
                                    data.getJSONObject(ChatCommandsHandler.MESSAGE);
                            createMessage(message);
                            broadcast(ChatCommandsHandler.wrapCommand(
                                    ChatCommandsHandler.S_DATA_UPDATED
                            ).toString());
                        } else {
                            // TO DO: Proceed other command types
                        }
                    }
                }
            }
        } catch (JSONException e) {
            webSocket.send(ChatCommandsHandler.wrapCommand(
                    ChatCommandsHandler.S_INVALID_REQUEST).toString());
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        final JSONObject user = sockets.get(webSocket);
        if (user != null)
            try {
                user.put("loggedIn", false);
                sockets.remove(webSocket);
                broadcast(ChatCommandsHandler.wrapCommand(
                        ChatCommandsHandler.S_DATA_UPDATED
                ).toString());
            } catch (JSONException exc) {}
    }

    @Override
    public void onStart() {
        final String result = init();
        if (onActivityDataUpdateListener != null)
            onActivityDataUpdateListener.onActivityDataUpdate(result);
    }

    public void setOnActivityDataUpdateListener(
            OnActivityDataUpdateListener onActivityDataUpdateListener) {
        this.onActivityDataUpdateListener = onActivityDataUpdateListener;
    }

    // Server API implementation methods

    private void authenticate (JSONObject data, WebSocket webSocket) {
        try {
            final String login = data.getString("login");
            final String password = data.getString("password");
            final String sex = data.getString("sex");

            if (login.equals("") || password.equals("") || sex.equals("")) {
                webSocket.send(ChatCommandsHandler.wrapCommand(
                        ChatCommandsHandler.S_AUTHENTICATION_ERROR).toString());
                return;
            }

            JSONObject userFound = null;

            for (int i=0; i<users.length(); i++) {
                final JSONObject user = (JSONObject) users.get(i);
                if (user.getString("name").equals(login)) {
                    userFound = user;
                    break;
                }
            }

            if (userFound == null) {
                System.out.println ("Registration....");

                final JSONObject newUser = createUser (login, password, sex);
                if (newUser != null) {
                    sockets.put(webSocket, newUser);


                    webSocket.send(ChatCommandsHandler.makeCommandAuthenticated(
                            newUser.getInt("id"), newUser.getInt("channelId"))
                            .toString());

                    broadcast(ChatCommandsHandler.wrapCommand(
                            ChatCommandsHandler.S_DATA_UPDATED).toString());
                } else {
                    webSocket.send(ChatCommandsHandler.wrapCommand(
                            ChatCommandsHandler.S_AUTHENTICATION_ERROR).toString());
                }
            } else {
                System.out.println ("Authentication...");

                if (Hash.checkHashedString(password, userFound.getString("password"))) {
                    userFound.put("loggedIn", true);
                    sockets.put(webSocket, userFound);

                    webSocket.send(ChatCommandsHandler.makeCommandAuthenticated(
                            userFound.getInt("id"), userFound.getInt("channelId"))
                            .toString());

                    broadcast(ChatCommandsHandler.wrapCommand(
                            ChatCommandsHandler.S_DATA_UPDATED).toString());
                } else {
                    webSocket.send(ChatCommandsHandler.wrapCommand(
                            ChatCommandsHandler.S_PASSWORD_MISMATCH).toString());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            webSocket.send(ChatCommandsHandler.wrapCommand(
                    ChatCommandsHandler.S_INVALID_REQUEST).toString());
        }

    }

    private Integer getNextUserId () {
        try {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            for (int i=0; i<users.length(); i++) {
                final JSONObject user = (JSONObject) users.get(i);
                ids.add(user.getInt("id"));
            }

            if (ids.size() == 0)
                return 1;
            else
                return Collections.max(ids) + 1;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject createUser (String login, String password, String sex) {
        synchronized (this) {
            final Integer userId = getNextUserId();
            final JSONObject newUser = new JSONObject();

            try {
                newUser.put("id", userId);
                newUser.put("channelId", 0);
                newUser.put("name", login);
                newUser.put("sex", sex);
                newUser.put("age", 0); // Not used
                newUser.put("password", Hash.getHashedString(password));
                newUser.put("loggedIn", true);

                users.put(newUser);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return newUser;
        }
    }

    private JSONArray getChannels () {
        return channels;
    }

    private JSONArray getUsers (Integer channelId) {
        try {
            JSONArray cUsers = new JSONArray();

            for (int i=0; i<users.length(); i++) {
                final JSONObject user = (JSONObject) users.get(i);
                if (user.getInt("channelId") == channelId)
                    cUsers.put(user);
            }

            return cUsers;
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONArray getMessages (Integer authId, Integer channelId, Integer rcpUserId) {
        try {
            JSONArray cMessages = new JSONArray();

            for (int i=0; i<messages.length(); i++) {
                final JSONObject message = (JSONObject) messages.get(i);

                if (rcpUserId == 0) {
                    // get public messages
                    if (message.getInt("channelId") == channelId &&
                            message.getInt("rcpUserId") == 0)

                        cMessages.put(message);
                } else {
                    if ((message.getInt("ownUserId") == authId &&
                            message.getInt("rcpUserId") == rcpUserId) ||
                            (message.getInt("ownUserId") == rcpUserId &&
                                    message.getInt("rcpUserId") == authId))

                        cMessages.put(message);
                }
            }

            return cMessages;
        } catch (JSONException e) {
            return null;
        }
    }

    private void updateUser (JSONObject user) {
        try {
            JSONObject mUser = getUserById(user.getInt("id"));
            mUser.put("channelId", user.getInt("channelId"));
        } catch (JSONException e) {}
    }

    private JSONObject getUserById (Integer id) {
        try {
            JSONObject userFound = null;

            for (int i = 0; i < users.length(); i++) {
                final JSONObject user = (JSONObject) users.get(i);
                if (user.getInt("id") == id) {
                    userFound = user;
                    break;
                }
            }

            return userFound;
        } catch (JSONException e) {
            return null;
        }
    }

    private void createMessage (JSONObject message) {
        synchronized (this) {
            try {
                message.put("time", System.currentTimeMillis() / 1000l);
                message.put("id", getNextMessageId());
                messages.put(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Integer getNextMessageId () {
        try {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            for (int i=0; i<messages.length(); i++) {
                final JSONObject message = (JSONObject) messages.get(i);
                ids.add(message.getInt("id"));
            }

            if (ids.size() == 0)
                return 1;
            else
                return Collections.max(ids) + 1;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
