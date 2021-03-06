package com.student.topquiz.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;

import com.google.gson.Gson;
import com.student.topquiz.R;
import com.student.topquiz.model.User;


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
    private ImageView mInfoIcon;
    private ImageView mSettingsIcon;
    private int score;
    private  String mFirstname = "";
    private  int mLastScore = 0;
    private String mDifficulty = "Normal";
    private static final int GAME_ACTIVITY_REQUEST_CODE = 03;
    private static final int EDIT_ACTIVITY_REQUEST_CODE = 04;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";
    private static final String SHARED_PREF_USER_INFO_DIFFICULTY = "SHARED_PREF_USER_INFO_DIFFICULTY";
    private static final String SHARED_PREF_USER_THEME = "SHARED_PREF_THEME";
    private int defaultTheme = 0;
    private int previousScore;
    private String previousFirstName;
    private String difficulty;
    MediaPlayer ring;
    MediaPlayer buttonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ring = MediaPlayer.create(MainActivity.this, R.raw.fluffing);

        linkButtons();
        sharedPref();

        mUser = new User(previousFirstName, previousScore, difficulty);
        setDifficulty("Normal");
        getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREF_USER_INFO_DIFFICULTY, mUser.getDifficulty())
                .apply();

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
        setListeners();
    }
    @Override
    protected void onStart() {
        super.onStart();

        initButton();
        if (!ring.isPlaying()) {
            ring.setLooping(true);
            ring.start();
        }
        animateButton();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        initButton();
        if (!ring.isPlaying()) {
            ring = MediaPlayer.create(MainActivity.this, R.raw.fluffing);
            ring.setLooping(true);
            ring.start();
        }
        animateButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ring.stop();
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
        else if (EDIT_ACTIVITY_REQUEST_CODE == requestCode){

        }
    }

    private void linkButtons(){
        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mNameEditText = findViewById(R.id.main_edittext_name);
        mPlayButton = findViewById(R.id.main_button_play);

        mEasyButton = findViewById(R.id.main_button_easy);
        mNormalButton = findViewById(R.id.main_button_normal);
        mHardButton = findViewById(R.id.main_button_hard);
        mEditButton = findViewById(R.id.edit_button);

        mInfoIcon = findViewById(R.id.infoicon);
        mSettingsIcon = findViewById(R.id.settingsicon);

        initButton();
    }

    private void sharedPref(){
        previousFirstName = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);
        previousScore = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_INFO_SCORE, 0);
        difficulty = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_DIFFICULTY, "Normal");
        defaultTheme = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_THEME, 1) ;

    }

    private void setListeners(){

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
                buttonClick= MediaPlayer.create(MainActivity.this,R.raw.buttonclick);
                buttonClick.start();
                mPlayButton.setBackgroundColor(Color.YELLOW);
                mUser.setFirstName(mNameEditText.getText().toString());
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_NAME, mUser.getFirstName())
                        .apply();
                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE );
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        mPlayButton.setBackgroundColor(Color.parseColor("#FF3700B3"));
                    }
                }, 500);
            }
        });

        mEasyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonClick= MediaPlayer.create(MainActivity.this,R.raw.buttonclick);
                buttonClick.start();
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

                buttonClick= MediaPlayer.create(MainActivity.this,R.raw.buttonclick);
                buttonClick.start();
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

                buttonClick= MediaPlayer.create(MainActivity.this,R.raw.buttonclick);
                buttonClick.start();
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
                buttonClick= MediaPlayer.create(MainActivity.this,R.raw.buttonclick);
                buttonClick.start();
                mEditButton.setBackgroundColor(Color.YELLOW);
                Intent EditActivityIntent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(EditActivityIntent, EDIT_ACTIVITY_REQUEST_CODE );
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        mEditButton.setBackgroundColor(Color.parseColor("#FF3700B3"));
                    }
                }, 500);
            }
        });
        mInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonClick= MediaPlayer.create(MainActivity.this,R.raw.buttonclick);
                buttonClick.start();
                showInfoView();
            }
        });
        mSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonClick= MediaPlayer.create(MainActivity.this,R.raw.buttonclick);
                buttonClick.start();
                showSettingsView();
            }
        });

    }

    public void initButton(){
        mPlayButton.setAlpha(0f);
        mPlayButton.setTranslationY(50);

        mEasyButton.setAlpha(0f);
        mEasyButton.setTranslationY(-50);

        mNormalButton.setAlpha(0f);
        mNormalButton.setTranslationY(-50);

        mHardButton.setAlpha(0f);
        mHardButton.setTranslationY(-50);

        mEditButton.setAlpha(0f);
        mEditButton.setTranslationY(50);
    }

    private void animateButton(){
        mPlayButton.animate().alpha(1f).translationYBy(-50).setDuration(1500);
        mEditButton.animate().setStartDelay(400).alpha(1f).translationYBy(-50).setDuration(1500);
        mEasyButton.animate().alpha(1f).translationYBy(50).setDuration(1500);
        mNormalButton.animate().setStartDelay(200).alpha(1f).translationYBy(50).setDuration(1500);
        mHardButton.animate().setStartDelay(400).alpha(1f).translationYBy(50).setDuration(1500);
    }

    public void setDifficulty(String difficulty)
    {
        switch (difficulty) {
            case "Easy":
                mUser.setDifficulty("Easy");
                mEasyButton.setBackgroundColor(getResources().getColor(R.color.easy));
                mNormalButton.setBackgroundColor(Color.parseColor("#808080"));
                mHardButton.setBackgroundColor(Color.parseColor("#808080"));
                break;
            case "Normal":
                mUser.setDifficulty("Normal");
                mEasyButton.setBackgroundColor(Color.parseColor("#808080"));
                mNormalButton.setBackgroundColor(getResources().getColor(R.color.intermediate));
                mHardButton.setBackgroundColor(Color.parseColor("#808080"));
                break;
            case "Hard":
                Log.d(TAG, "click hard button" + difficulty);
                mEasyButton.setBackgroundColor(Color.parseColor("#808080"));
                mNormalButton.setBackgroundColor(Color.parseColor("#808080"));
                mHardButton.setEnabled(true);
                mHardButton.setBackgroundColor(getResources().getColor(R.color.hard));
                break;
        }

        getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREF_USER_INFO_DIFFICULTY, mUser.getDifficulty())
                .apply();
    }

    private void showInfoView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.info))
                .setMessage(getString(R.string.info2))
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
        builder.setTitle(getString(R.string.settings))
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