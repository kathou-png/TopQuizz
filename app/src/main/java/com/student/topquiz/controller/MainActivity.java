package com.student.topquiz.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;

import com.google.gson.Gson;
import com.student.topquiz.R;
import com.student.topquiz.model.User;

// empecher recréation orientation => Bundle, charger informations du bundle

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mGreetingTextView;
    private EditText mNameEditText;
    private Button mPlayButton;
    private Button mEasyButton;
    private Button mNormalButton;
    private Button mHardButton;
    private Button mEditButton;
    private User mUser;
    private int score;
    private  String mFirstname = "";
    private  int mLastScore = 0;
    private String mDifficulty = "Normal";
    private static final int GAME_ACTIVITY_REQUEST_CODE = 03;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";
    private static final String SHARED_PREF_USER_INFO_DIFFICULTY = "SHARED_PREF_USER_INFO_DIFFICULTY";

    public void setDifficulty(String difficulty)
    {
        switch (difficulty) {
            case "Easy":
                mUser.setDifficulty("Easy");
                mEasyButton.setBackgroundColor(Color.parseColor("#3341C3"));
                mNormalButton.setBackgroundColor(Color.parseColor("#808080"));
                mHardButton.setBackgroundColor(Color.parseColor("#808080"));
                break;
            case "Normal":
                mUser.setDifficulty("Normal");
                mEasyButton.setBackgroundColor(Color.parseColor("#808080"));
                mNormalButton.setBackgroundColor(Color.parseColor("#3341C3"));
                mHardButton.setBackgroundColor(Color.parseColor("#808080"));
                break;
            case "Hard":
                mEasyButton.setBackgroundColor(Color.parseColor("#808080"));
                mNormalButton.setBackgroundColor(Color.parseColor("#808080"));
                mHardButton.setBackgroundColor(Color.parseColor("#3341C3"));
                break;
        }

        getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREF_USER_INFO_DIFFICULTY, mUser.getDifficulty())
                .apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mNameEditText = findViewById(R.id.main_edittext_name);
        mPlayButton = findViewById(R.id.main_button_play);
        mEasyButton = findViewById(R.id.main_button_easy);
        mNormalButton = findViewById(R.id.main_button_normal);
        mHardButton = findViewById(R.id.main_button_hard);
        mEditButton = findViewById(R.id.edit_button);
        String previousFirstName = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);
        int previousScore = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_INFO_SCORE, 0);
        String difficulty = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_DIFFICULTY, "Normal");

        mUser = new User(previousFirstName, previousScore, difficulty);
        setDifficulty("Normal");
        if(previousScore == 0 && previousFirstName == null){
            mPlayButton.setEnabled(false);
        }
        else{
            mUser.setFirstName(previousFirstName);
            mGreetingTextView.setText( getString(R.string.welcomeback_text) + " "+ previousFirstName+" !\n"
                    + getString(R.string.welcomeback_text2) +" "+ previousScore +" "+ getString(R.string.welcomeback_text3));
            mNameEditText.setText(previousFirstName);
            mPlayButton.setEnabled(true);
        }
        //SharedPreferences settings = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE);
        //settings.edit().clear().commit();
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPlayButton.setEnabled(!s.toString().isEmpty());
            }
        });
        mPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mUser.setFirstName(mNameEditText.getText().toString());
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_NAME, mUser.getFirstName())
                        .apply();
                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE );
            }
        });

        mEasyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mUser.setDifficulty("Easy");
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_DIFFICULTY, mUser.getDifficulty())
                        .apply();
                setDifficulty("Easy");
            }
        });

        mNormalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mUser.setDifficulty("Normal");
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_DIFFICULTY, mUser.getDifficulty())
                        .apply();
                setDifficulty("Normal");
            }
        });

        mHardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mUser.setDifficulty("Hard");
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_DIFFICULTY, mUser.getDifficulty())
                        .apply();
                setDifficulty("Hard");
            }
        });
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameActivityIntent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE );
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode) {
            // Fetch the score from the Intent

            Gson gson = new Gson();
           // mFirstname = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);
            mFirstname = mUser.getFirstName();
            mLastScore =  data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0);
            mDifficulty = mUser.getDifficulty();
            String json = "{\"playerName\" : \"" + mFirstname + "\",\"playerScore\" : \"" + mLastScore + "\", \"difficulty\" : \"" + mDifficulty + "\"}";
            mUser = gson.fromJson(json, User.class);

            mGreetingTextView.setText( getString(R.string.welcomeback_text) + " "+mUser.getFirstName()+" !\n"
                    + getString(R.string.welcomeback_text2) +" "+ mUser.getScore() +" "+ getString(R.string.welcomeback_text3));
            mPlayButton.setEnabled(true);
        }
    }
}