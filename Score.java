package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

public class Score {
    private int score;
    public Score(){
        score = 0;
    }

    public void incrementScore(){
        score++;
    }

    public int getScore(){
        return score;
    }
}
