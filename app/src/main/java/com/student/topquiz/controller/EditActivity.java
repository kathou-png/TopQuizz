package com.student.topquiz.controller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.student.topquiz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class EditActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final String TAG = "EditActivity";
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_THEME = "SHARED_PREF_THEME";

    private int defaultTheme;
    private EditText mQuestionEditText;
    private EditText mAnswer1EditText;
    private EditText mAnswer2EditText;
    private EditText mAnswer3EditText;
    private EditText mAnswer4EditText;
    private ImageView mInfoIcon;
    private ImageView mSettingsIcon;
    private ImageView mBackIcon;
    private int index;
    private Button mSubmitButton;
    private Boolean canSubmit = false;
    MediaPlayer buttonClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defaultTheme = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_THEME, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        buttonClick= MediaPlayer.create(EditActivity.this,R.raw.buttonclick);
        buttonClick.setLooping(false);
        Context context = this;
        mQuestionEditText = findViewById(R.id.questionEdit);
        mAnswer1EditText = findViewById(R.id.answer1Edit);
        mAnswer2EditText = findViewById(R.id.answer2Edit);
        mAnswer3EditText  = findViewById(R.id.answer3Edit);
        mAnswer4EditText = findViewById(R.id.answer4Edit);
        mSubmitButton = findViewById(R.id.submit);
        mInfoIcon = findViewById(R.id.infoicon);
        mSettingsIcon = findViewById(R.id.settingsicon);
        mBackIcon = findViewById(R.id.backicon);
        mSubmitButton.setEnabled(false);
        mInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick.start();
                showInfoView();
            }
        });
        mSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick.start();
                showSettingsView();
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(mQuestionEditText.getText()));
                Log.d(TAG, String.valueOf(mAnswer1EditText.getText()));
                Log.d(TAG, String.valueOf(mAnswer2EditText.getText()));
                Log.d(TAG, String.valueOf(mAnswer3EditText.getText()));
                Log.d(TAG, String.valueOf(mAnswer4EditText.getText()));
                Log.d(TAG, String.valueOf(index));

                try {
                    createQuestion();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_1:
                if (checked)
                    // Pirates are the best
                    canSubmit = true;
                    index = 1;
                    mSubmitButton.setEnabled(true);
                    break;
            case R.id.radio_2:
                if (checked)
                    // Ninjas rule
                    canSubmit = true;
                    index = 2;
                    mSubmitButton.setEnabled(true);
                    break;
            case R.id.radio_3:
                if (checked)
                    // Ninjas rule
                    canSubmit = true;
                index = 3;
                mSubmitButton.setEnabled(true);
                    break;
            case R.id.radio_4:
                if (checked)
                    // Ninjas rule
                    canSubmit = true;
                index = 4;
                mSubmitButton.setEnabled(true);
                    break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createQuestion() throws IOException, JSONException {

        //FIRST READ THE FILE
       /* String ret = "";

        try {
            String file_name= this.getFilesDir() + "/mydir/"+"question.json";
            InputStream inputStream = new FileInputStream(new File(file_name));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }*/

      //  JSONObject obj = new JSONObject(ret);

   //     Log.d(TAG, "READ FILE JSONOBJECT " + obj );


        JSONObject jsonObject = new JSONObject();
      //  int index = obj.length();
        try {
            jsonObject.put("question", mQuestionEditText.getText().toString());
            Log.d(TAG, "Question OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("answer1", mAnswer1EditText.getText().toString());
            Log.d(TAG, "answer1 OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("answer2", mAnswer2EditText.getText().toString());
            Log.d(TAG, "answer2 OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("answer3", mAnswer3EditText.getText().toString());
            Log.d(TAG, "answer3 OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("answer4", mAnswer4EditText.getText().toString());
            Log.d(TAG, "answer4 OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("index", index);
            Log.d(TAG, "index OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"JSON OBJECT" +  String.valueOf(jsonObject));
        JSONObject mainObject = new JSONObject();
       /// obj.put(Integer.toString(obj.length()), JSON.parse(mainObject));
        mainObject.put(Integer.toString(index), jsonObject);
        // Convert JsonObject to String Format
        String userString = mainObject.toString();
        // Define the File Path and its Name
        File dir = new File(this.getFilesDir(), "mydir");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, "question.json");
            FileWriter writer = new FileWriter(gpxfile);
            Log.d(TAG, "USER STRING : " + userString);
            writer.append(userString);
          //  writer.append()
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        endActivity();
    }
    public void endActivity(){
        Toast.makeText(this, "Question added!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 2_000); // LENGTH_SHORT is usually 2 second long
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onClick(View v){
    }
    private void showInfoView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Information about this application")
                .setMessage("This app was created by Alex VO, Sami OURABAH and Cathy TRUONG")
                .setNeutralButton("GOT IT!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // builder.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showSettingsView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"Light Mode","Dark Mode"};
        builder.setCancelable(false);
        builder.setTitle("Choose Theme")
                .setSingleChoiceItems(items, defaultTheme , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                defaultTheme = 0;
                                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                                        .edit()
                                        .putInt(SHARED_PREF_USER_THEME, 0)
                                        .apply();
                                break;
                            case 1:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                defaultTheme = 1;
                                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                                        .edit()
                                        .putInt(SHARED_PREF_USER_THEME, 1)
                                        .apply();
                                break;
                        }
                    }
                })
                .create()
                .show();
    }


}