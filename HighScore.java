package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.TextView;

public class HighScore extends AppCompatActivity {
    private DatabaseHelper dh;
    private TextView scoreDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        dh = dh.getInstance(this);
        scoreDisplay = findViewById(R.id.scoreDisplay);
        Cursor allData = dh.getAllData();
        if(allData.moveToNext()){
            scoreDisplay.setText("High Score: " + Integer.parseInt(allData.getString(1)));
        }
        else{
            scoreDisplay.setText("High Score: " + 0);
        }

    }
}
