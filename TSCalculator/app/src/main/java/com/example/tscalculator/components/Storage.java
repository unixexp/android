package com.example.tscalculator.components;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.tscalculator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Storage {

    private Context context;
    private File saveFile;
    private boolean available = true;

    public Storage(Context context) {
        this.context = context;
        saveFile = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath() + File.separator + "save.json");
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            this.available = false;
            Toast toast = Toast.makeText(
                    context,
                    context.getString(R.string.no_ext_storate_found),
                    Toast.LENGTH_LONG);
            toast.show();
        }

        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                this.available = false;
                e.printStackTrace();
                Toast toast = Toast.makeText(
                        context,
                        context.getString(R.string.init_save_error_str),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public String readTextFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder text = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            text.append(line);
            text.append("\n");
        }
        reader.close();
        return text.toString();
    }

    public void save(Object data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
            writer.write(data.toString());
            writer.close();
            Toast toast = Toast.makeText(
                    context,
                    context.getString(R.string.save_str),
                    Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(
                    context,
                    context.getString(R.string.save_error_str),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public JSONObject load() {
        String fileData = "";
        try {
            fileData = this.readTextFile(saveFile);
        } catch (IOException e){
            fileData = "";
        }

        if (fileData == "") {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("speakers", new JSONArray());
                return jsonObject;
            } catch (JSONException e) {
                return null;
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject(fileData);
                if (jsonObject.has("speakers")) {
                    return jsonObject;
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("speakers", new JSONArray());
                    return jsonObject;
                }
            } catch (JSONException e) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("speakers", new JSONArray());
                    return jsonObject;
                } catch (JSONException e2) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public boolean isAvailable() {
        return available;
    }

}
