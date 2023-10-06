package com.example.funnychat.clientserver;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ChatClient extends WebSocketClient {

    private OnActivityDataUpdateListener onActivityDataUpdateListener = null;

    public ChatClient(String host, Integer port) throws URISyntaxException {
        super(new URI("ws://" + host + ":" + port.toString()));
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        if (onActivityDataUpdateListener != null)
            onActivityDataUpdateListener.onActivityDataUpdate(
                    ChatCommandsHandler.wrapCommand(
                            ChatCommandsHandler.S_CLIENT_CONNECTED
                    ).toString()
            );
    }

    @Override
    public void onMessage(String s) {
        if (onActivityDataUpdateListener != null)
            onActivityDataUpdateListener.onActivityDataUpdate(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if (onActivityDataUpdateListener != null)
            onActivityDataUpdateListener.onActivityDataUpdate(
                    ChatCommandsHandler.wrapCommand(
                            ChatCommandsHandler.S_CLIENT_DISCONNECTED
                    ).toString()
            );
    }

    @Override
    public void onError(Exception e) {
        if (onActivityDataUpdateListener != null)
            onActivityDataUpdateListener.onActivityDataUpdate(
                    ChatCommandsHandler.wrapCommand(
                            ChatCommandsHandler.S_CLIENT_DISCONNECTED
                    ).toString()
            );
    }

    public void setOnActivityDataUpdateListener(
            OnActivityDataUpdateListener onActivityDataUpdateListener) {
        this.onActivityDataUpdateListener = onActivityDataUpdateListener;
    }
}
