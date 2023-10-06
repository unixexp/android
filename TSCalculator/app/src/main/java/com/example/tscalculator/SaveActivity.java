package com.example.tscalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tscalculator.adapters.SaveListAdapter;
import com.example.tscalculator.components.Calculator;
import com.example.tscalculator.components.Storage;
import com.example.tscalculator.models.Speaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class SaveActivity extends AppCompatActivity implements View.OnTouchListener {

    private Storage storage;
    private ListView saveList;
    private List<Speaker> speakers;
    private SaveListAdapter saveListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_save);

        final Button backButton = findViewById(R.id.backButton);
        final Button saveButton = findViewById(R.id.saveButton);
        final EditText saveNameField = findViewById(R.id.saveNameField);

        saveNameField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            SaveActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(saveNameField.getWindowToken(),
                            0);
                }
                return false;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        storage = new Storage(this);
        speakers = new ArrayList<Speaker>();
        saveListAdapter = new SaveListAdapter(this, speakers);
        saveList = findViewById(R.id.save_list);
        saveList.setAdapter(saveListAdapter);

        if (storage != null && storage.isAvailable()) {
            updateSaveList();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (saveNameField.getText().toString().equals("")) {
                        Toast toast = Toast.makeText(
                                SaveActivity.this,
                                SaveActivity.this.getString(R.string.enter_name),
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        JSONObject saves = storage.load();
                        if (saves == null) {
                            Toast toast = Toast.makeText(
                                    SaveActivity.this,
                                    SaveActivity.this.getString(R.string.data_load_error),
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }

                        try {
                            JSONArray speakerArray = saves.getJSONArray("speakers");
                            JSONObject speakerObject = Calculator.driver.serializeToJSON();
                            if (speakerObject == null) {
                                Toast toast = Toast.makeText(
                                        SaveActivity.this,
                                        SaveActivity.this.getString(
                                                R.string.unsuccessfully_measurements),
                                        Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                JSONObject toSave = new JSONObject();
                                speakerObject.put("Name", saveNameField.getText().toString());
                                speakerArray.put(speakerObject);
                                toSave.put("speakers", speakerArray);
                                storage.save(toSave);
                                saveNameField.setText("");
                                updateSaveList();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(
                                    SaveActivity.this,
                                    SaveActivity.this.getString(R.string.data_load_error),
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }
            });
        }
    }

    private void updateSaveList() {
        JSONObject saves = storage.load();

        if (saves == null) {
            Toast toast = Toast.makeText(
                    SaveActivity.this,
                    SaveActivity.this.getString(R.string.data_load_error),
                    Toast.LENGTH_LONG);
            toast.show();
        }

        try {
            speakers.clear();
            JSONArray speakerArray = saves.getJSONArray("speakers");
            for (int i = 0; i < speakerArray.length(); i++) {
                final JSONObject speakerObj = (JSONObject) speakerArray.get(i);
                final Speaker speaker = new Speaker();
                speaker.setName(speakerObj.getString("Name"));
                speaker.setFs(speakerObj.getInt("Fs"));
                speaker.setDiameter(speakerObj.getDouble("Diameter"));
                speaker.setQms(speakerObj.getDouble("Qms"));
                speaker.setQes(speakerObj.getDouble("Qes"));
                speaker.setQts(speakerObj.getDouble("Qts"));
                speaker.setSd(speakerObj.getDouble("Sd"));
                speaker.setVas(speakerObj.getDouble("Vas"));
                speaker.setEbp(speakerObj.getDouble("EBP"));
                speakers.add(speaker);
            }
            saveListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(
                    this,
                    this.getString(R.string.data_load_error),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void deleteSave(String name) {
        JSONObject saves = storage.load();

        if (saves == null) {
            Toast toast = Toast.makeText(
                    SaveActivity.this,
                    SaveActivity.this.getString(R.string.data_load_error),
                    Toast.LENGTH_LONG);
            toast.show();
        }

        try {
            JSONArray speakerArrayNew = new JSONArray();
            JSONArray speakerArray = saves.getJSONArray("speakers");
            for (int i = 0; i < speakerArray.length(); i++) {
                final JSONObject speakerObj = (JSONObject) speakerArray.get(i);
                if (!speakerObj.getString("Name").equals(name))
                    speakerArrayNew.put(speakerObj);
            }
            JSONObject toSave = new JSONObject();
            toSave.put("speakers", speakerArrayNew);
            storage.save(toSave);
            updateSaveList();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(
                    this,
                    this.getString(R.string.data_load_error),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideSystemUI();
        return false;
    }

    private void hideSystemUI() {
        // Hide navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

}
