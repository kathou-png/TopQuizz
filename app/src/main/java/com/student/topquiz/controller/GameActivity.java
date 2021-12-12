package com.student.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
    win, lose, pause, play
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
    public static int MAIN_ACTIVITY_REQUEST_CODE = 02;
    private TextView mQuestionTextView;
    private TextView mTimer;
    private TextView mScoreTextView;
    private ImageView mInfoIcon;
    private ImageView mSettingsIcon;
    private ImageView mPlayIcon;
    private ImageView mHintIcon;
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
    private GameState mGameState;
    private int mLives;
    private ImageView mHeart1;
    private ImageView mHeart2;
    private ImageView mHeart3;
    private static CallbackManager callbackManager;
    private static ShareDialog shareDialog;

    MediaPlayer ring;
    MediaPlayer buttonClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        defaultTheme = this.getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_THEME, 0);

        initMusic();
        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
        } else {
            mScore = 0;
            mRemainingQuestionCount = 3;
        }
        linkComponents();
        initGameState();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRemainingQuestionCount = 3;
        if (!ring.isPlaying()) {
            ring.setLooping(true);
            ring.start();
        }
        try {
            mQuestionBank = generateQuestionBank();
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        displayQuestion(mCurrentQuestion);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mRemainingQuestionCount = 3;
        if (!ring.isPlaying()) {
            ring.setLooping(true);
            ring.start();
        }
        try {
            mQuestionBank = generateQuestionBank();
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        displayQuestion(mCurrentQuestion);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ring.pause();
    }

    private void initMusic(){
        ring = MediaPlayer.create(GameActivity.this, R.raw.fluffing);
        buttonClick= MediaPlayer.create(GameActivity.this,R.raw.buttonclick);
        buttonClick.setLooping(false);
    }

    private void initGameState(){
        mEnableTouchEvents = true;
        mGameState = GameState.play;
        mLives = 3;
        setHearts();
        try {
            mQuestionBank = generateQuestionBank();
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        mCurrentQuestion = mQuestionBank.getNextQuestion();
    }
    private void linkComponents(){
        mHeart1 = findViewById(R.id.heart1);
        mHeart2 = findViewById(R.id.heart2);
        mHeart3 = findViewById(R.id.heart3);
        mScoreTextView = findViewById(R.id.score);
        mScoreTextView.setText(String.valueOf(mScore));
        mQuestionTextView = findViewById(R.id.game_activity_textview_question);
        mInfoIcon = findViewById(R.id.infoicon);
        mSettingsIcon = findViewById(R.id.settingsicon);
        mPlayIcon = findViewById(R.id.playicon);
        mHintIcon = findViewById(R.id.hinticon);
        mAnswer1Button = findViewById(R.id.game_activity_button_1);
        mAnswer2Button = findViewById(R.id.game_activity_button_2);
        mAnswer3Button = findViewById(R.id.game_activity_button_3);
        mAnswer4Button = findViewById(R.id.game_activity_button_4);
    }
    private void setListeners(){
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
        mPlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayIcon.setImageResource(R.drawable.ic_baseline_pause_24);
                //onPause();
                showPauseView();
            }
        });
        mHintIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int correct_answer = mCurrentQuestion.getAnswerIndex();
                String difficulty = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .getString(SHARED_PREF_USER_INFO_DIFFICULTY, "Normal").toString();
                switch(difficulty)
                {
                    case "Easy":
                        switch (correct_answer)
                        {
                            case 0 :
                                mAnswer2Button.setVisibility(View.GONE);
                                mAnswer3Button.setVisibility(View.GONE);
                                break;
                            case 1 :
                                mAnswer1Button.setVisibility(View.GONE);
                                mAnswer3Button.setVisibility(View.GONE);
                                break;
                            case 2 :
                            case 3 :
                                mAnswer1Button.setVisibility(View.GONE);
                                mAnswer2Button.setVisibility(View.GONE);
                                break;
                        }
                        break;
                    case "Normal":
                        switch (correct_answer)
                        {
                            case 0 :
                                mAnswer2Button.setVisibility(View.GONE);
                                break;
                            case 1 :
                                mAnswer3Button.setVisibility(View.GONE);
                                break;
                            case 2 :
                            case 3 :
                                mAnswer1Button.setVisibility(View.GONE);
                                break;
                        }
                        break;
                    case "Hard":
                        mHintIcon.setVisibility(View.GONE);
                        Toast.makeText(GameActivity.this, "Joker indisponible en mode Hard!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
    }
    private void setHearts()
    {
        switch(mLives)
        {
            case 0:
                mHeart1.setImageResource(R.drawable.grey_heart);
            case 1:
                mHeart2.setImageResource(R.drawable.grey_heart);
                break;
            case 2:
                mHeart3.setImageResource(R.drawable.grey_heart);
                break;
            case 3:
                mHeart1.setColorFilter(0xff0000);
                mHeart2.setColorFilter(0xff0000);
                mHeart3.setColorFilter(0xff0000);
                break;
        }
    }
    public void setVisibilityBack()
    {
        mAnswer1Button.setVisibility(View.VISIBLE);
        mAnswer2Button.setVisibility(View.VISIBLE);
        mAnswer3Button.setVisibility(View.VISIBLE);
        mAnswer4Button.setVisibility(View.VISIBLE);
    }

    private void verifyLives()
    {
        Log.d(TAG,"lives");
        Log.d(TAG,Integer.toString(mLives));
        if(mLives <= 0)
        {
            mGameState = GameState.lose;
            endGame();
        }
        setHearts();
    }

    private void setTimer(Question question)
    {
        // Add a delay to the timer to counter the bug which allowed the timer to go on for
        // 2 seconds after the user answered correctly.
        int delay = 2;
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
                if(mCurrentQuestion != question || mGameState == GameState.lose){cancel();}
                // Only display positive values.
                if(counter >= 0){mTimer.setText(String.valueOf(counter));}
                counter--;
            }
            public  void onFinish()
            {
                if(mCurrentQuestion == question)
                {
                    // Decrement lives and verify if the end should end.
                    mLives--;
                    verifyLives();
                    // Reset timer by creating a new timer if it isn't gameover.
                    setTimer(question);
                    // Cancel the current timer.
                    cancel();
                }
                cancel();
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

    private void showPauseView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("What do you want to do ?")
                .setMessage("Going back will make you loose your points")
                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGameState = GameState.lose;
                        endGame();
                    }
                })
                .create()
                .show();
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

    protected QuestionBank generateQuestionBank() throws FileNotFoundException, JSONException {
        List<Question> questionList = new ArrayList<>();
        //READ QUESTIONS
        String ret = "";
        JSONObject obj;
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
                obj = new JSONObject(ret).getJSONObject("1");

                //extracting data array from json string
                String question = obj.getString("question");
                String answer1 = obj.getString("answer1");
                String answer2 = obj.getString("answer2");
                String answer3 = obj.getString("answer3");
                String answer4 = obj.getString("answer4");
                int index = obj.getInt("index");
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
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
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
        );/*
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
        );*/
        questionList.add(
                new Question(
                        getString(R.string.question_4),
                        Arrays.asList(
                                getString(R.string.answer_4_1),
                                getString(R.string.answer_4_2),
                                getString(R.string.answer_4_3),
                                getString(R.string.answer_4_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_4)
                        )

                )
        );

        questionList.add(
                new Question(
                        getString(R.string.question_5),
                        Arrays.asList(
                                getString(R.string.answer_5_1),
                                getString(R.string.answer_5_2),
                                getString(R.string.answer_5_3),
                                getString(R.string.answer_5_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_5)
                        )

                )
        );

        questionList.add(
                new Question(
                        getString(R.string.question_6),
                        Arrays.asList(
                                getString(R.string.answer_6_1),
                                getString(R.string.answer_6_2),
                                getString(R.string.answer_6_3),
                                getString(R.string.answer_6_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_6)
                        )

                )
        );

        questionList.add(
                new Question(
                        getString(R.string.question_7),
                        Arrays.asList(
                                getString(R.string.answer_7_1),
                                getString(R.string.answer_7_2),
                                getString(R.string.answer_7_3),
                                getString(R.string.answer_7_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_7)
                        )

                )
        );

        questionList.add(
                new Question(
                        getString(R.string.question_8),
                        Arrays.asList(
                                getString(R.string.answer_8_1),
                                getString(R.string.answer_8_2),
                                getString(R.string.answer_8_3),
                                getString(R.string.answer_8_4)
                        ),
                        Integer.parseInt(getString(R.string.index_answer_8)
                        )

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
                        setVisibilityBack();
                    }
                    else{
                        mGameState = GameState.win;
                        endGame();
                    }
                }
            }, 2_000); // LENGTH_SHORT is usually 2 second long
        }
        else{

            Toast.makeText(this, "False!", Toast.LENGTH_SHORT).show();
            mScoreTextView.setText(String.valueOf(mScore));
            mScore += addPoints(false);
            mLives--;
            verifyLives();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEnableTouchEvents = true;
                    setVisibilityBack();
                }
            }, 2_000); // LENGTH_SHORT is usually 2 second long

        }


    }

    private void endGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String previousFirstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);

        if(mGameState == GameState.win)
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
                    .setNeutralButton("SHARE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openShareDialog();
                           /* Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "My best score is "+ mScore+" on TopQuizz ! Try to beat me");
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                            */
                            Intent intent = new Intent();
                            intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
        else if(mGameState == GameState.lose)
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

                    .setNeutralButton("SHARE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openShareDialog();
                           /* Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "My best score is "+ mScore+" on TopQuizz ! Try to beat me");
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                            */
                            Intent intent = new Intent();
                            intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
    }

    public void openShareDialog(){
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onError(@NonNull FacebookException e) {

            }

            @Override
            public void onCancel() {

            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote("My best score on top quizz is " + mScore + ", try to beat me")
                    .setContentUrl(Uri.parse("https://openclassrooms.com/fr/courses/4517166-developpez-votre-premiere-application-android"))
                    .build();
            shareDialog.show(linkContent);
        }


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