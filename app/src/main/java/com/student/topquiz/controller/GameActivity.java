package com.student.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.student.topquiz.R;
import com.student.topquiz.model.Question;
import com.student.topquiz.model.QuestionBank;
import com.student.topquiz.model.User;

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
    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION = "BUNDLE_STATE_QUESTION";
    private TextView mQuestionTextView;
    private TextView mTimer;
    private int counter;
    private Button mAnswer1Button;
    private Button mAnswer2Button;
    private Button mAnswer3Button;
    private Button mAnswer4Button;
    private QuestionBank mQuestionBank;
    private int nbQuestions;
    private Question mCurrentQuestion;
    private boolean mEnableTouchEvents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        mQuestionBank = generateQuestionBank();
        mQuestionTextView = findViewById(R.id.game_activity_textview_question);

        mAnswer1Button = findViewById(R.id.game_activity_button_1);
        mAnswer2Button = findViewById(R.id.game_activity_button_2);
        mAnswer3Button = findViewById(R.id.game_activity_button_3);
        mAnswer4Button = findViewById(R.id.game_activity_button_4);
        mCurrentQuestion = mQuestionBank.getNextQuestion();
        displayQuestion(mCurrentQuestion);

        // Use the same listener for the four buttons.
    // The view id value will be used to distinguish the button triggered
        mAnswer1Button.setOnClickListener(this);
        mAnswer2Button.setOnClickListener(this);
        mAnswer3Button.setOnClickListener(this);
        mAnswer4Button.setOnClickListener(this);
    }

    private void setTimer(Question question)
    {
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
        Log.d(TAG, Integer.toString(counter));


        mTimer = findViewById(R.id.timer);
        mTimer.setText(String.valueOf(counter));
        new CountDownTimer(counter * 1000L, 1000){
            public void onTick(long millisUntilFinished){
                mTimer.setText(String.valueOf(counter));
                counter--;
            }
            public  void onFinish(){
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

    protected QuestionBank generateQuestionBank(){
        List<Question> questionList = new ArrayList<Question>();
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

        nbQuestions = questionList.size();
        return new QuestionBank(questionList);
    }

    @Override
    public void onClick(View v) {
        int index;

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