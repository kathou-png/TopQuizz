package com.student.topquiz.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("playerName")
    private String mFirstName;

    @SerializedName("playerScore")
    private int mScore;

    public User(){
        mFirstName = "";
        mScore = 0;
    }
    public User(String firstName, int score){
        mFirstName = firstName;
        mScore = score;
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



}
