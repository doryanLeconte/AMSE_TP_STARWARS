package com.imt.amse_tp_starwars;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    boolean joystickIsPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView joystick = findViewById(R.id.joystick_center);
        ImageView vaisseau = findViewById(R.id.vaisseau);
        View game_zone = findViewById(R.id.game_zone);

        game_zone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vaisseau.setY(0);
                vaisseau.setX(0);
                return true;
            }
        });

        Handler handlerForMovingTie = new Handler();
        Runnable movingTie = new Runnable() {
            @Override
            public void run() {
                vaisseau.setTranslationX(0.2f);
//                vaisseau.setX(vaisseau.getTranslationX() - 0.2f);
//                vaisseau.setY(vaisseau.getTranslationY() - 0.2f);


                if (joystickIsPressed) {
                    handlerForMovingTie.postDelayed(this, 10);
                }
            }
        };

        joystick.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("MOTION_EVENT", "DOWN");
                        joystickIsPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("MOTION_EVENT", "UP");
                        joystickIsPressed = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("MOTION_EVENT : AXIS_X", event.getAxisValue(MotionEvent.AXIS_X) + "");
                        Log.d("MOTION_EVENT | AXIS_Y", event.getAxisValue(MotionEvent.AXIS_Y) + "");

                        movingTie.run();
                        break;
                    default:
                        return false;

                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        moveAsteroid(findViewById(R.id.asteroid2));
        moveAsteroid(findViewById(R.id.asteroid4));

    }

    protected void moveAsteroid(View asteroid) {
        Path path = new Path();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int x_max = metrics.widthPixels;
        int y_max = metrics.heightPixels;
        asteroid.setX(new Random().nextFloat() * x_max);
        asteroid.setY(new Random().nextFloat() * y_max);
        Log.d("Height", "" + x_max);
        path.arcTo(0f, 0f, new Random().nextFloat() * x_max, new Random().nextFloat() * y_max, new Random().nextFloat() * 360f * (new Random().nextInt() > 0 ? 1 : -1), 359f * (new Random().nextInt() > 0 ? 1 : -1), true);

        ObjectAnimator animation = ObjectAnimator.ofFloat(asteroid, View.X, View.Y, path);

        animation.setDuration(4000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.start();

    }


}