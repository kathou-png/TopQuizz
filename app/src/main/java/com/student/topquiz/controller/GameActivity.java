package com.student.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.student.topquiz.R;
import com.student.topquiz.model.Question;
import com.student.topquiz.model.QuestionBank;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum GameState
{
    win, lose, pause
}

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private int mRemainingQuestionCount;
    private int mScore;

    private static final int GAME_ACTIVITY_REQUEST_CODE = 04;

    private static final String TAG = "GameActivity";
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_DIFFICULTY = "SHARED_PREF_USER_INFO_DIFFICULTY";
    private static final String SHARED_PREF_USER_THEME = "SHARED_PREF_THEME";
    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION = "BUNDLE_STATE_QUESTION";
    private TextView mQuestionTextView;
    private TextView mTimer;
    private TextView mScoreTextView;
    private ImageView mInfoIcon;
    private ImageView mSettingsIcon;
    private int counter;
    private Button mAnswer1Button;
    private Button mAnswer2Button;
    private Button mAnswer3Button;
    private Button mAnswer4Button;
    private QuestionBank mQuestionBank;
    private int nbQuestions;
    private Question mCurrentQuestion;
    private boolean mEnableTouchEvents;
    private int defaultTheme;

    MediaPlayer ring;
    MediaPlayer buttonClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ring = MediaPlayer.create(GameActivity.this, R.raw.fluffing);
        buttonClick= MediaPlayer.create(GameActivity.this,R.raw.buttonclick);
        buttonClick.setLooping(false);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
        } else {
            mScore = 0;
            mRemainingQuestionCount = 3;
        }
        setContentView(R.layout.activity_game);
        mEnableTouchEvents = true;
        try {
            mQuestionBank = generateQuestionBank();
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        mScoreTextView = findViewById(R.id.score);
        mScoreTextView.setText(String.valueOf(mScore));
        mQuestionTextView = findViewById(R.id.game_activity_textview_question);
        mInfoIcon = findViewById(R.id.infoicon);
        mSettingsIcon = findViewById(R.id.settingsicon);
        mAnswer1Button = findViewById(R.id.game_activity_button_1);
        mAnswer2Button = findViewById(R.id.game_activity_button_2);
        mAnswer3Button = findViewById(R.id.game_activity_button_3);
        mAnswer4Button = findViewById(R.id.game_activity_button_4);
        mCurrentQuestion = mQuestionBank.getNextQuestion();
        defaultTheme = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_THEME, 0);

        displayQuestion(mCurrentQuestion);

        // Use the same listener for the four buttons.
    // The view id value will be used to distinguish the button triggered
        mAnswer1Button.setOnClickListener(this);
        mAnswer2Button.setOnClickListener(this);
        mAnswer3Button.setOnClickListener(this);
        mAnswer4Button.setOnClickListener(this);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!ring.isPlaying()) {
            ring.setLooping(true);
            ring.start();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!ring.isPlaying()) {
            ring = MediaPlayer.create(GameActivity.this, R.raw.fluffing);
            ring.setLooping(true);
            ring.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ring.pause();
    }

    private void setTimer(Question question)
    {
        // Add a delay to the timer to counter the bug which allowed the timer to go on for
        // 3 seconds after the user answered correctly.
        int delay = 3;
        String difficulty = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_DIFFICULTY, "Normal").toString();
        switch (difficulty) {
            case "Easy":
                counter = 20;
                break;
            case "Normal":
                counter = 10;
                break;
            case "Hard":
                counter = 7;
                break;
        }
        mTimer = findViewById(R.id.timer);
        // Don't display the added delay to the user.
        mTimer.setText(String.valueOf(counter));
        new CountDownTimer((counter + delay) * 1_000L, 1_000){
            public void onTick(long millisUntilFinished)
            {
                // If user has moved on to the next question, cancel the timer.
                if(mCurrentQuestion != question){cancel();}
                // Only display positive values.
                if(counter >= 0){mTimer.setText(String.valueOf(counter));}
                counter--;
            }
            public  void onFinish()
            {
                if(mCurrentQuestion == question){endGame(GameState.lose);}
            }
        }.start();
    }

    private int addPoints(boolean positiveScore)
    {
        int points = 0;
        String difficulty = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                .getString(SHARED_PREF_USER_INFO_DIFFICULTY, "Normal").toString();
        if(positiveScore)
        {
            switch (difficulty) {
                case "Easy":
                    points = 1;
                    break;
                case "Normal":
                    points = 2;
                    break;
                case "Hard":
                    points = 3;
                    break;
            }
        }
        else
        {
            if(difficulty.equals("Hard")){points = -1;}
        }
        return points;
    }

    private void displayQuestion(final Question question){
        mQuestionTextView.setText(question.getQuestion());
        mAnswer1Button.setText(question.getChoiceByIndex(0));
        mAnswer2Button.setText(question.getChoiceByIndex(1));
        mAnswer3Button.setText(question.getChoiceByIndex(2));
        mAnswer4Button.setText(question.getChoiceByIndex(3));
        setTimer(question);
    }
    private void showInfoView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Information about this application")
                .setMessage("This app was created by Alex VO, Sami OURABAH and Cathy TRUONG")
                .setNegativeButton("GOT IT!", new DialogInterface.OnClickListener() {
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
    protected QuestionBank generateQuestionBank() throws FileNotFoundException, JSONException {
        List<Question> questionList = new ArrayList<>();
        //READ QUESTIONS
        String ret = "";

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
        }
        JSONObject obj = new JSONObject(ret).getJSONObject("1");

        //extracting data array from json string
        String question = obj.getString("question");
        String answer1 = obj.getString("answer1");
        String answer2 = obj.getString("answer2");
        String answer3 = obj.getString("answer3");
        String answer4 = obj.getString("answer4");
        int index = obj.getInt("index");

        questionList.add(
                new Question(
                        getString(R.string.question_0),
                                Arrays.asList(
                                        getString(R.string.answer_0_1),
                                        getString(R.string.answer_0_2),
                                        getString(R.string.answer_0_3),
                                        getString(R.string.answer_0_4)
                                ),
                        Integer.parseInt(getString(R.string.index_answer_0)
                        )

                )
        );
        questionList.add(
                new Question(
                        getString(R.string.question_1),
                        Arrays.asList(
                                getString(R.string.answer_1_1),
                                getString(R.string.answer_1_2),
                                getString(R.string.answer_1_3),
                                getString(R.string.answer_1_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_1)
                        )

                )
        );
        questionList.add(
                new Question(
                        getString(R.string.question_2),
                        Arrays.asList(
                                getString(R.string.answer_2_1),
                                getString(R.string.answer_2_2),
                                getString(R.string.answer_2_3),
                                getString(R.string.answer_2_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_2)
                        )

                )
        );
        questionList.add(
                new Question(
                        getString(R.string.question_3),
                        Arrays.asList(
                                getString(R.string.answer_3_1),
                                getString(R.string.answer_3_2),
                                getString(R.string.answer_3_3),
                                getString(R.string.answer_3_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_3)
                        )

                )
        );
        questionList.add(
                new Question(
                        question,
                        Arrays.asList(
                                answer1,
                                answer2,
                                answer3,
                                answer4
                        ),
                        index

                )
        );

        nbQuestions = questionList.size();
        return new QuestionBank(questionList);
    }

    @Override
    public void onClick(View v) {
        int index;
        buttonClick= MediaPlayer.create(GameActivity.this,R.raw.buttonclick);
        buttonClick.start();
        if (v == mAnswer1Button) {
            index = 0;
        } else if (v == mAnswer2Button) {
            index = 1;
        } else if (v == mAnswer3Button) {
            index = 2;
        } else if (v == mAnswer4Button) {
            index = 3;
        } else {
            throw new IllegalStateException("Unknown clicked view : " + v);
        }

        mEnableTouchEvents= false;
        if (index ==  mCurrentQuestion.getAnswerIndex()){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            mScore += addPoints(true);
            mScoreTextView.setText(String.valueOf(mScore));
            getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                    .edit()
                    .putInt(SHARED_PREF_USER_INFO_SCORE, mScore)
                    .apply();
            mRemainingQuestionCount--;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEnableTouchEvents = true;
                    if (mRemainingQuestionCount >= 0) {
                        mCurrentQuestion = mQuestionBank.getNextQuestion();
                        displayQuestion(mCurrentQuestion);
                    }
                    else{
                        endGame(GameState.win);
                    }
                }
            }, 2_000); // LENGTH_SHORT is usually 2 second long
        }
        else{

            Toast.makeText(this, "False!", Toast.LENGTH_SHORT).show();
            mScoreTextView.setText(String.valueOf(mScore));
            mScore += addPoints(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEnableTouchEvents = true;
                }
            }, 2_000); // LENGTH_SHORT is usually 2 second long
        }


    }
    private void endGame(GameState gameState){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String previousFirstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);

        if(gameState == GameState.win)
        {
            builder.setTitle("Well done, " + previousFirstName+ "!")
                    .setMessage(getString(R.string.score) + " "+mScore)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            Log.d(TAG,Integer.toString(mScore));
                            intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
        else if(gameState == GameState.lose)
        {
            builder.setTitle("Game Over ! Better luck next time, " + previousFirstName+ "!")
                    .setMessage(getString(R.string.score) + " "+mScore)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            Log.d(TAG,Integer.toString(mScore));
                            intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .create()
                    .show();
        }


        builder.setTitle("Well done, " + previousFirstName+ "!")
                .setMessage(getString(R.string.score) + " "+mScore)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        Log.d(TAG,Integer.toString(mScore));
                        intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setNeutralButton("SHARE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "My best score is "+ mScore+" on TopQuizz ! Try to beat me");
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);

                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_STATE_SCORE, mScore);
        outState.putInt(BUNDLE_STATE_QUESTION, mRemainingQuestionCount);
    }

}