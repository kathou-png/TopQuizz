package com.student.topquiz.model;

import com.google.gson.annotations.SerializedName;


public class User {

    @SerializedName("playerName")
    private String mFirstName;

    @SerializedName("playerScore")
    private int mScore;

    @SerializedName("playerDifficulty")
    private String mDifficulty;

    public User(){
        mFirstName = "";
        mScore = 0;
        mDifficulty = "Normal";
    }
    public User(String firstName, int score, String difficulty){
        mFirstName = firstName;
        mScore = score;
        mDifficulty = difficulty;
    }
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public int getScore() {
        return mScore;
    }

    public void setDifficulty(String difficulty) {
        mDifficulty = difficulty;
    }

    public String getDifficulty() {
        return mDifficulty;
    }
}
