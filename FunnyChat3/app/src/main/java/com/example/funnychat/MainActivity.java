package com.example.funnychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.funnychat.adapters.ChannelsAdapter;
import com.example.funnychat.adapters.MessagesAdapter;
import com.example.funnychat.adapters.UsersAdapter;
import com.example.funnychat.clientserver.ChatClient;
import com.example.funnychat.clientserver.ChatCommandsHandler;
import com.example.funnychat.clientserver.ChatServer;
import com.example.funnychat.clientserver.OnActivityDataUpdateListener;
import com.example.funnychat.models.Channel;
import com.example.funnychat.models.Message;
import com.example.funnychat.models.User;
import com.example.funnychat.util.AssetsHandler;
import com.example.funnychat.widgets.AnimatedToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnActivityDataUpdateListener {

    final List<Channel> channels = new ArrayList<Channel>();
    final List<User> users = new ArrayList<User>();
    final List<User> usersAll = new ArrayList<User>();
    final List<Message> messages = new ArrayList<Message>();

    ChannelsAdapter channelsAdapter = null;
    UsersAdapter usersAdapter = null;
    ArrayAdapter<String> goToAdapter = null;
    MessagesAdapter messagesAdapter = null;

    AssetsHandler assetsHandler = null;

    public int selfUserId = 0;
    private int selectedChannelId = 0;
    private int selectedUserId = 0;

    private List<String> channelNames = new ArrayList<String>();

    final public static int SERVER_PORT = 5555;
    public static String currentIP = null;
    public static String connectToServerIp = null;

    public static ChatServer chatServer = null;
    public static ChatClient chatClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println (this.getClass().getSimpleName() + ": onCreate()");

        if (savedInstanceState != null) {
            selfUserId = savedInstanceState.getInt("selfUserId");
            selectedChannelId = savedInstanceState.getInt("selectedChannelId");
            selectedUserId = savedInstanceState.getInt("selectedUserId");
        }

        loadMainScreen();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        System.out.println (this.getClass().getSimpleName() + ": onActivityResult()");

        if (requestCode == 0) {
            if (data != null)
                selfUserId = data.getIntExtra("selfUserId", 0);

            loadMainScreen();
        }
    }

    public void loadMainScreen () {
        if (selfUserId == 0) {
            Intent intent = new Intent (this, LoginActivity.class);
            startActivityForResult(intent, 0);
            return;
        }

        if (chatServer != null)
            chatServer.setOnActivityDataUpdateListener(this);

        if (chatClient != null)
            chatClient.setOnActivityDataUpdateListener(this);

        setContentView(R.layout.activity_main);

        assetsHandler = new AssetsHandler(this);

        channelsAdapter = new ChannelsAdapter(this, channels);
        usersAdapter = new UsersAdapter(this, users);
        messagesAdapter = new MessagesAdapter(this, messages);
        goToAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, (List) channelNames);

        final LinearLayout leftBlock = (LinearLayout) findViewById(R.id.left_block);
        final AnimatedToggleButton buttonMenu = (AnimatedToggleButton) findViewById(R.id.button_menu);
        final AutoCompleteTextView goToView =
                (AutoCompleteTextView) findViewById(R.id.goto_field);
        final ImageButton sendButton = (ImageButton) findViewById(R.id.send_button);
        final TextView selfName = findViewById(R.id.self_name);

        buttonMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    leftBlock.setVisibility(View.VISIBLE);
                else
                    leftBlock.setVisibility(View.GONE);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        ListView channelList = (ListView) findViewById(R.id.channel_list);
        ListView userList = (ListView) findViewById(R.id.user_list);
        ListView messageList = (ListView) findViewById(R.id.message_list);

        channelList.setAdapter (channelsAdapter);
        userList.setAdapter (usersAdapter);
        goToView.setAdapter(goToAdapter);
        messageList.setAdapter (messagesAdapter);

        refreshChannelList();
        refreshUserList();
        refreshMessageList();

        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Channel channelItem = (Channel) parent.getItemAtPosition(position);
                setSelectedChannelId(channelItem.getId());
            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User userItem = (User) parent.getItemAtPosition(position);
                setSelectedUserId(userItem.getId());
            }
        });

        goToView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                for (Channel c : channels) {
                    if (c.getName().equals(item)) {
                        goToView.setText("");
                        setSelectedChannelId(c.getId());
                        break;
                    }
                }
            }
        });

        selfName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu userMenu = new PopupMenu(MainActivity.this.getApplicationContext(),
                        selfName);
                userMenu.getMenuInflater().inflate(R.menu.user_menu, userMenu.getMenu());
                userMenu.show();
                userMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.exit:
                                chatClient.close();
                                break;
                            case R.id.ipinfo:
                                Toast toast = Toast.makeText(MainActivity.this,
                                        currentIP, Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                        }
                        
                        return false;
                    }
                });
            }
        });

        for (User user : usersAll) {
            if (user.getId() == selfUserId) {
                selfName.setText(user.getName());
                break;
            }
        }
    }

    public void refreshChannelList() {
        final JSONObject command = ChatCommandsHandler.makeCommandGetChannels(selfUserId);
        chatClient.send(command.toString());
    }

    public void refreshUserList() {
        final JSONObject command = ChatCommandsHandler.makeCommandGetUsers(
                selfUserId, selectedChannelId);
        chatClient.send(command.toString());
    }

    public void refreshMessageList() {
        final JSONObject command = ChatCommandsHandler.makeCommandGetMessages(
                selfUserId, selectedChannelId, selectedUserId);
        chatClient.send(command.toString());
    }

    public void loadChannelList (final JSONArray channelsArray) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    channels.clear();
                    channelNames.clear();
                    for (int i=0; i<channelsArray.length(); i++) {
                        final JSONObject channel = (JSONObject) channelsArray.get (i);

                        channelNames.add(channel.getString("name"));
                        final Channel newChannel = new Channel (
                                channel.getInt("id"),
                                channel.getString("name")
                        );
                        if (newChannel.getId() == selectedChannelId)
                            newChannel.setSelected(true);
                        else
                            newChannel.setSelected(false);

                        channels.add(newChannel);
                    }

                    channelsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadUserList (final JSONArray usersArray) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    users.clear();

                    final User ALL_USER = new User(
                            0,
                            selectedChannelId,
                            "<ALL>",
                            "",
                            0);
                    usersAll.add(ALL_USER);
                    users.add(ALL_USER);

                    for (int i=0; i<usersArray.length(); i++) {
                        final JSONObject user = (JSONObject) usersArray.get (i);

                        final User newUser = new User(
                                user.getInt("id"),
                                user.getInt("channelId"),
                                user.getString("name"),
                                user.getString("sex"),
                                user.getInt("age"));
                        newUser.setLoggedIn(user.getBoolean("loggedIn"));

                        if (newUser.getId() == selfUserId) {
                            newUser.setLoggedIn(true);
                            newUser.setChannelId(selectedChannelId);

                            final TextView selfName = findViewById(R.id.self_name);
                            selfName.setText(newUser.getName());
                        }

                        if (newUser.getId() == selectedUserId)
                            newUser.setSelected(true);
                        else
                            newUser.setSelected(false);

                        usersAll.add(newUser);

                        if (newUser.getChannelId() == selectedChannelId ||
                                user.getInt("id") == 0)
                            users.add (newUser);
                    }

                    usersAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadMessageList (final JSONArray messagesArray) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    messages.clear();
                    for (int i=0; i<messagesArray.length(); i++) {
                        final JSONObject message = (JSONObject) messagesArray.get(i);

                        if (selectedUserId == 0) {
                            if (message.getInt("channelId") != selectedChannelId)
                                continue;
                        } else {
                            if (message.getInt("ownUserId") != selfUserId ||
                                    message.getInt("rcpUserId") != selectedUserId)
                                if (message.getInt("ownUserId") != selectedUserId ||
                                        message.getInt("rcpUserId") != selfUserId)
                                    continue;
                        }

                        final Message newMessage = new Message (
                                message.getInt ("id"),
                                message.getInt ("ownUserId"),
                                message.getInt ("channelId"),
                                message.getInt ("rcpUserId"),
                                message.getLong ("time"),
                                message.getString ("text")
                        );
                        messages.add (newMessage);
                    }

                    Collections.sort (messages, new Comparator<Message>() {
                        @Override
                        public int compare (Message m1, Message m2) {
                            return m1.getTime() > m2.getTime()
                                    ? 1 : m1.getTime() < m2.getTime() ? -1 : 0;
                        }
                    });

                    messagesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getSelectedChannelId() {
        return selectedChannelId;
    }

    public void setSelectedChannelId(int selectedChannelId) {
        this.selectedChannelId = selectedChannelId;
        this.selectedUserId = 0;

        final JSONObject command = ChatCommandsHandler.makeCommandSelectChannel(
                selfUserId, selectedChannelId);
        chatClient.send(command.toString());
    }

    public List<User> getUsersAll() {
        return usersAll;
    }

    public int getNewMessageId (int userId) {
        final List<Integer> myMessages = new ArrayList<Integer>();
        for (Message i : messages) {
            if (i.getOwnUserId() == userId)
                myMessages.add (i.getId());
        }

        Collections.sort (myMessages);

        if (myMessages.size() == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            final String stringId = stringBuilder.append(userId).append(1).toString();

            return Integer.parseInt(stringId);
        } else {
            return myMessages.get (myMessages.size() - 1) + 1;
        }
    }

    public void sendMessage () {
        final EditText messageField = (EditText) findViewById(R.id.message_field);

        if (messageField.getText().toString().equals(""))
            return;

        JSONObject command = ChatCommandsHandler.makeCommandSendMessage(
                selfUserId, selectedChannelId, selectedUserId,
                messageField.getText().toString());
        chatClient.send(command.toString());

        messageField.setText("");

        InputMethodManager inputMethodManager = (InputMethodManager)
                MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(messageField.getWindowToken(), 0);
    }

    public int getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(int selectedUserId) {
        this.selectedUserId = selectedUserId;

        refreshUserList();
        refreshMessageList();
    }

    // Activity lifecycle
    @Override
    protected void onStart() {
        super.onStart();
        System.out.println (this.getClass().getSimpleName() + ": onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println (this.getClass().getSimpleName() + ": onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println (this.getClass().getSimpleName() + ": onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println (this.getClass().getSimpleName() + ": onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println (this.getClass().getSimpleName() + ": onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println (this.getClass().getSimpleName() + ": onDestroy()");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        System.out.println (this.getClass().getSimpleName() + ": onSaveInstanceState()");

        savedInstanceState.putInt("selfUserId", selfUserId);
        savedInstanceState.putInt("selectedChannelId", selectedChannelId);
        savedInstanceState.putInt("selectedUserId", selectedUserId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println (this.getClass().getSimpleName() + ": onRestoreInstanceState()");
    }

    private void showToast (final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this,
                        message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onActivityDataUpdate(String message) {
        try {
            final JSONObject jsonObject = new JSONObject(message);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                final String command = keys.next();
                if (command.equals(ChatCommandsHandler.S_SERVER_STARTED)) {
                    showToast("Local server started");
                } else if (command.equals(ChatCommandsHandler.S_SERVER_DISCONNECTED)) {
                    showToast("Local server disconnected");
                } else if (command.equals(ChatCommandsHandler.S_SERVER_INIT_ERROR)) {
                    showToast("Local server init error");
                } else if (command.equals(ChatCommandsHandler.S_SERVER_SYSTEM_ERROR)) {
                    showToast("Local server system error");
                } else if (command.equals(ChatCommandsHandler.S_INVALID_REQUEST)) {
                    showToast("Invalid request");
                } else if (command.equals(ChatCommandsHandler.S_CLIENT_CONNECTED)) {
                    showToast("Client connected");
                } else if (command.equals(ChatCommandsHandler.S_CLIENT_DISCONNECTED)) {
                    showToast("Client disconnected");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selfUserId = 0;
                            finish();
                            startActivity(getIntent());
                        }
                    });
                } else if (command.equals(ChatCommandsHandler.S_AUTHENTICATION_ERROR)) {
                    showToast("Authentication error");
                    chatClient.close();
                } else if (command.equals(ChatCommandsHandler.S_PASSWORD_MISMATCH)) {
                    showToast("Password mismatch");
                    chatClient.close();
                } else if (command.equals(ChatCommandsHandler.S_NOTIFICATION)) {
                    final JSONObject data = jsonObject.getJSONObject(command);
                    final String text = data.getString(ChatCommandsHandler.TEXT);
                    showToast(text);
                } else if (command.equals(ChatCommandsHandler.S_DATA_UPDATED)) {
                    refreshChannelList();
                    refreshUserList();
                    refreshMessageList();
                } else if (command.equals(ChatCommandsHandler.S_DATA_CHANNELS_RECEIVED)) {
                    final JSONObject data = jsonObject.getJSONObject(command);
                    loadChannelList(data.getJSONArray(ChatCommandsHandler.CHANNELS));
                } else if (command.equals(ChatCommandsHandler.S_DATA_USERS_RECEIVED)) {
                    final JSONObject data = jsonObject.getJSONObject(command);
                    loadUserList(data.getJSONArray(ChatCommandsHandler.USERS));
                } else if (command.equals(ChatCommandsHandler.S_DATA_MESSAGES_RECEIVED)) {
                    final JSONObject data = jsonObject.getJSONObject(command);
                    loadMessageList(data.getJSONArray(ChatCommandsHandler.MESSAGES));
                } else {
                    // Process other commands
                }
            }
        } catch (JSONException e) {
            showToast("Error server data format");
        }
    }
}
