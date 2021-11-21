package com.student.topquiz.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionBank {

    private List<Question> mQuestionList;
    private int mNextQuestionIndex;

    public QuestionBank(List<Question> questionList) {
        // Shuffle the question list before storing it
        mNextQuestionIndex = -1;
        mQuestionList = questionList;
        Collections.shuffle(questionList);
    }

    public Question getNextQuestion() {
        // Loop over the questions and return a new one at each call
        mNextQuestionIndex++;
        return mQuestionList.get(mNextQuestionIndex);
    }
}
