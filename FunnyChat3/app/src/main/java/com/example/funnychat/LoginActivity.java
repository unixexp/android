package com.example.funnychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funnychat.clientserver.ChatClient;
import com.example.funnychat.clientserver.ChatCommandsHandler;
import com.example.funnychat.clientserver.ChatServer;
import com.example.funnychat.clientserver.OnActivityDataUpdateListener;
import com.example.funnychat.util.AssetsHandler;
import com.example.funnychat.util.Hash;
import com.example.funnychat.util.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements OnActivityDataUpdateListener  {

    private EditText login = null;
    private EditText password = null;
    private Spinner sex = null;
    private EditText serverIPView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println (this.getClass().getSimpleName() + ": onCreate()");
        setContentView(R.layout.activity_login);

        // Login form
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        sex = findViewById(R.id.sex);
        serverIPView = findViewById(R.id.server);
        // Login form

        final List<String> sexList = new ArrayList<String>(Arrays.asList("male", "female"));
        final ArrayAdapter<String> sexAdapter = new ArrayAdapter<String> (this,
                android.R.layout.simple_spinner_dropdown_item, (List) sexList);
        sex.setAdapter(sexAdapter);

        MainActivity.currentIP = Network.determineCurrentIP(this);
        final TextView currentIPView = findViewById(R.id.current_ip);
        currentIPView.setText (MainActivity.currentIP);
        serverIPView.setText(MainActivity.currentIP);

        runChatServer();

        final Button signInButton = findViewById(R.id.sign_in_button);

        final AssetsHandler assetsHandler = new AssetsHandler(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (serverIPView.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(LoginActivity.this,
                            "Please enter server IP", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    MainActivity.connectToServerIp = serverIPView.getText().toString();

                    try {
                        MainActivity.chatClient = new ChatClient(
                                MainActivity.connectToServerIp, MainActivity.SERVER_PORT);
                    } catch (URISyntaxException e) {
                        Toast toast = Toast.makeText(LoginActivity.this,
                                "Error create server URI", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    MainActivity.chatClient.setOnActivityDataUpdateListener(
                            LoginActivity.this);
                    MainActivity.chatClient.connect();
                }
            }
        });
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println (this.getClass().getSimpleName() + ": onRestoreInstanceState()");
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
                    authenticate();
                } else if (command.equals(ChatCommandsHandler.S_CLIENT_DISCONNECTED)) {
                    showToast("Client disconnected");
                } else if (command.equals(ChatCommandsHandler.S_AUTHENTICATION_ERROR)) {
                    showToast("Authentication error");
                    // chatClient.close();
                } else if (command.equals(ChatCommandsHandler.S_PASSWORD_MISMATCH)) {
                    showToast("Password mismatch");
                    // chatClient.close();
                } else if (command.equals(ChatCommandsHandler.S_NOTIFICATION)) {
                    final JSONObject data = jsonObject.getJSONObject(command);
                    final String text = data.getString(ChatCommandsHandler.TEXT);
                    showToast(text);
                } else if (command.equals(ChatCommandsHandler.S_AUTHENTICATED)) {
                    final JSONObject data = jsonObject.getJSONObject(command);
                    final int id = data.getInt(ChatCommandsHandler.AUTH_ID);
                    final int channelId = data.getInt(ChatCommandsHandler.CHANNEL_ID);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("selfUserId", id);
                            intent.putExtra("selectedChannelId", channelId);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                } else {
                    // Process other commands
                }
            }
        } catch (JSONException e) {
            showToast("Error server data format");
        }
    }

    private void showToast (final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(LoginActivity.this,
                        message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void runChatServer () {
        if (MainActivity.chatServer == null) {
            MainActivity.chatServer = new ChatServer(
                    MainActivity.currentIP, MainActivity.SERVER_PORT);
            MainActivity.chatServer.setOnActivityDataUpdateListener(this);
            MainActivity.chatServer.start();
        }
    }

    private void authenticate() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String loginValue = login.getText().toString();
                final String passwordValue = password.getText().toString();
                final String sexValue = sex.getSelectedItem().toString();

                if (loginValue.equals("")) {
                    showToast("Please enter login");
                    return;
                }

                if (passwordValue.equals("")) {
                    showToast("Please enter password");
                    return;
                }

                if (sexValue.equals("")) {
                    showToast("Please select your sex");
                    return;
                }

                final JSONObject command = ChatCommandsHandler.makeCommandAuthRequest(
                        loginValue, passwordValue, sexValue);
                MainActivity.chatClient.send(command.toString());
            }
        });
    }

}
