package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button play;
    private Button highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.playNow);
        play.setOnClickListener(this);
        highScore = findViewById(R.id.highscore);
        highScore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == play.getId()) {
            System.out.println("play");
            openActivityPlay();
        }
        if (view.getId() == highScore.getId()) {
            System.out.println("highScore");
            openActivityScore();
        }
    }

    public void openActivityPlay(){
        Intent intent = new Intent(this, PlayGame.class);
        startActivity(intent);
    }

    public void openActivityScore(){
        Intent intent = new Intent(this, HighScore.class);
        startActivity(intent);
    }
}
