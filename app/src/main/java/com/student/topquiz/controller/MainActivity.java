package com.student.topquiz.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private User mUser;
    private int score;
    private  String mFirstname = "";
    private  int mLastScore = 0;
    private static final int GAME_ACTIVITY_REQUEST_CODE = 03;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mNameEditText = findViewById(R.id.main_edittext_name);
        mPlayButton = findViewById(R.id.main_button_play);
        String previousFirstName = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);
        int previousScore = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_INFO_SCORE, 0);
    Log.d(TAG, "here in create scire us " + score);

        mUser = new User(previousFirstName, previousScore);
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
            String json = "{\"playerName\" : \"" + mFirstname + "\",\"playerScore\" : \"" + mLastScore +"\"}";
            mUser = gson.fromJson(json, User.class);

            mGreetingTextView.setText( getString(R.string.welcomeback_text) + " "+mUser.getFirstName()+" !\n"
                    + getString(R.string.welcomeback_text2) +" "+ mUser.getScore() +" "+ getString(R.string.welcomeback_text3));
            mPlayButton.setEnabled(true);
        }

    }
}