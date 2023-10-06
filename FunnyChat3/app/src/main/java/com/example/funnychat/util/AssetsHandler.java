package com.example.funnychat.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsHandler {

    private AssetManager assetManager;

    public AssetsHandler (Context context) {
        this.assetManager = context.getAssets();
    }

    public String readTextFile (String fileName) {
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            inputStream = assetManager.open (fileName);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            System.out.println ("Couldn't load file");
            return "";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println ("Couldn't close file");
                    return "";
                }
            } else {
                System.out.println ("Error read input stream");
            }

            return stringBuilder.toString();
        }
    }

    public JSONObject parseJSONFile (String fileName) {
        final String fileData = this.readTextFile(fileName);

        if (fileData == "") {
            return null;
        } else {
            try {
                final JSONObject jsonObject = new JSONObject (fileData);
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
