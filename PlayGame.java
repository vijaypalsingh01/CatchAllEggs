package com.example.myapplication;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Random;

public class PlayGame extends AppCompatActivity {
    private ImageView nest;
    private ImageView egg;
    private TextView scoreView;
    private ArrayList<ImageView> eggs;
    private ImageView brokenEgg;
    private float nestPositionX;
    private float nestPositionY;
    private float dx;
    private float dy;
    private ArrayList<Rect> eggRec;
    private Rect nestRec;
    private ArrayList<ValueAnimator> animation;
    private boolean first;
    private ConstraintLayout constraintLayout;
    private ConstraintLayout.LayoutParams layoutParams;
    private int i;
    private Score score;
    private DatabaseHelper dbHelp;

    private static final int NUMBER_OF_EGGS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        score = new Score();
        eggs = new ArrayList<>();
        animation = new ArrayList<>();
        eggRec = new ArrayList<>();
        nest = findViewById(R.id.nest);
        scoreView = findViewById(R.id.scoreView);
        nestRec = new Rect();
        first = true;
        egg = new ImageView(this);
        egg.setImageResource(R.drawable.egg);
        brokenEgg = new ImageView(this);
        brokenEgg.setImageResource(R.drawable.brokenegg);
        constraintLayout = findViewById(R.id.layout);
        layoutParams = new ConstraintLayout.LayoutParams(new ViewGroup.LayoutParams(70,80));
        nest.setOnTouchListener(onTouchListener());
        dbHelp = new DatabaseHelper(this);

    }

    public View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Random randomX = new Random();
                final Random randomSpeed = new Random();
                final int minimumSpeed = 1500;
                final int maximumSpeed = 2000;
                final int low = 0+ egg.getWidth();
                final int high = Resources.getSystem().getDisplayMetrics().widthPixels - 200;
                float x = event.getRawX();
                float y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = x - v.getX();
                        dy = y - v.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() - dx);
                        nestPositionX = v.getX();
                        v.setY(event.getRawY() - dy);
                        nestPositionY = v.getY();
                        if(first){
                            for(i =0;i<NUMBER_OF_EGGS;i++){
                                eggs.add(new ImageView(v.getContext()));
                                eggs.get(i).setImageResource(R.drawable.egg);
                                eggRec.add(new Rect());
                                eggs.get(i).setX(randomX.nextInt(high-low) + low);
                                constraintLayout.addView(eggs.get(i), i,layoutParams);
                                final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

                                animation.add(ObjectAnimator.ofFloat(eggs.get(i), "y", screenHeight));
                                animation.get(i).setDuration(randomSpeed.nextInt(maximumSpeed - minimumSpeed) + minimumSpeed);
                                animation.get(i).setInterpolator(new LinearInterpolator());
                                animation.get(i).setupStartValues();
                                animation.get(i).start();
                                animation.get(i).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                                        boolean t = new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Rect rec = new Rect();
                                                int index = animation.indexOf(valueAnimator);
                                                nestRec.set((int) nestPositionX,(int) nestPositionY,(int) nestPositionX +nest.getWidth(),(int) nestPositionY +nest.getHeight());
                                                rec.set((int)eggs.get(index).getX(), (int) eggs.get(index).getY(),(int)eggs.get(index).getX()+70,(int)eggs.get(index).getY()+80);
                                                if(nestRec.intersect(rec)){
                                                    eggs.get(index).setX( randomX.nextInt(high-low) + low);
                                                    eggs.get(index).setY(0);
                                                    animation.get(index).setupStartValues();
                                                    animation.get(index).setDuration(randomSpeed.nextInt(maximumSpeed - minimumSpeed) + minimumSpeed);
                                                    animation.get(index).start();
                                                    score.incrementScore();
                                                    scoreView.setText("Score: "+score.getScore());
                                                }

                                                else if(eggs.get(index).getY() >screenHeight - 135){
                                                    animation.get(index).cancel();
                                                    brokenEgg.setX(eggs.get(index).getX());
                                                    brokenEgg.setY(eggs.get(index).getY());
                                                    constraintLayout.removeView(eggs.get(index));
                                                    constraintLayout.addView(brokenEgg,layoutParams);
                                                    notifyGameOver();
                                                }

                                            }
                                        },0);
                                    }
                                });
                            }
                            first = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        dy = y - v.getY();
                        dx = x - v.getX();
                        break;
                }
                return true;
            }
        };
    }

    public void notifyGameOver() {
        for(int i=0;i<NUMBER_OF_EGGS;i++){
            animation.get(i).cancel();
        }

        String[] options = {"PLAY AGAIN", "MAIN MENU"};
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("You Scored: "+ score.getScore());
        b.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    finish();
                    startActivity(getIntent());
                }
                else if (which == 1){
                    Intent intent = new Intent(PlayGame.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        b.show();

        Cursor allData = dbHelp.getAllData();
        if(allData.getCount() == 0){
            add();
        }
        else{
            while(allData.moveToNext()){
                if(Integer.parseInt(allData.getString(1)) < score.getScore()){
                    update();
                    break;
                }
            }
        }
    }

    public void add(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Score: " + score.getScore() +"\n" + "Congratulations on setting a new high score");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelp.insertData(""+score.getScore());
            }
        });
        builder.show();
    }

    public void update(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Score: " + score.getScore() +"\n" + "Congratulations on setting a new high score");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelp.updateData("1",""+score.getScore());
            }
        });
        builder.show();
    }
}

