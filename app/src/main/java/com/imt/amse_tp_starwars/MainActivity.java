package com.imt.amse_tp_starwars;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final float SPEED = 0.01f;
    boolean joystickIsPressed;
    float joystickCenterX;
    float joystickCenterY;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView joystick = findViewById(R.id.joystick_center);

        joystickCenterX = joystick.getTranslationX();
        joystickCenterY = joystick.getTranslationY();
        running = false;

    }

    @Override
    protected void onResume() {
        super.onResume();

        running = true;
        moveAsteroid(findViewById(R.id.asteroid2));
        moveAsteroid(findViewById(R.id.asteroid4));

        ImageView joystick = findViewById(R.id.joystick_center);
        ImageView vaisseau = findViewById(R.id.vaisseau);
        ImageView asteroid2 = findViewById(R.id.asteroid2);
        ImageView asteroid4 = findViewById(R.id.asteroid4);
        View game_zone = findViewById(R.id.game_zone);
        ConstraintLayout cl = findViewById(R.id.constraintLayout);


        game_zone.setOnTouchListener((v, event) -> {
            v.performClick();
            running = !running;

            vaisseau.setTranslationY(0.2f);
            vaisseau.setTranslationX(0.2f);
            return true;
        });

        Handler handlerForMovingTie = new Handler();
        Runnable movingTie = new Runnable() {
            @Override
            public void run() {


                if (joystickIsPressed) {
                    handlerForMovingTie.postDelayed(this, 10);

                    if (vaisseau.getX() + vaisseau.getWidth() < game_zone.getWidth() && joystick.getTranslationX() > 0)
                        vaisseau.setTranslationX(vaisseau.getTranslationX() + joystick.getTranslationX() * SPEED);
                    if (vaisseau.getX() > 0 && joystick.getTranslationX() < 0)
                        vaisseau.setTranslationX(vaisseau.getTranslationX() + joystick.getTranslationX() * SPEED);
                    if (vaisseau.getY() + vaisseau.getHeight() < game_zone.getHeight() && joystick.getTranslationY() > 0)
                        vaisseau.setTranslationY(vaisseau.getTranslationY() + joystick.getTranslationY() * SPEED);
                    if (vaisseau.getY() > 0 && joystick.getTranslationY() < 0)
                        vaisseau.setTranslationY(vaisseau.getTranslationY() + joystick.getTranslationY() * SPEED);
                }
            }
        };

        Handler handlerForDetection = new Handler();

        Runnable detectCollision = new Runnable() {
            @Override
            public void run() {

                handlerForDetection.postDelayed(this, 10);
                if (isCollision(vaisseau, asteroid2) || isCollision(vaisseau, asteroid4)) {
                    ImageView explosion = new ImageView(getApplicationContext());
                    explosion.setImageDrawable(getDrawable(R.drawable.explosion));
                    LayoutParams explosionParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                    explosionParams.addRule(RelativeLayout.ABOVE, vaisseau.getId());
                    explosion.setLayoutParams(explosionParams);
                    explosion.setTranslationX(vaisseau.getTranslationX());
                    explosion.setTranslationY(vaisseau.getTranslationY());

                    cl.addView(explosion);


                    Log.d("COLLISION", "COLLIDED");
                }

            }

        };

        detectCollision.run();

        /*
         * Problème joystock clignote
         * ça affecte aussi le vaisseau quand on reste appuyé
         * */
        joystick.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("MOTION_EVENT", "DOWN");
                    joystickIsPressed = true;
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("MOTION_EVENT", "UP");
                    joystick.setTranslationX(joystickCenterX);
                    joystick.setTranslationY(joystickCenterY);
                    joystickIsPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:

                    joystick.setX(event.getX());
                    joystick.setY(event.getY());
                    movingTie.run();
                    break;
                default:
                    return false;

            }
            return true;
        });

    }

    protected void moveAsteroid(View asteroid) {
        Path path = new Path();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int x_max = metrics.widthPixels;
        int y_max = metrics.heightPixels;
        asteroid.setX(new Random().nextFloat() * x_max);
        asteroid.setY(new Random().nextFloat() * y_max);
        path.arcTo(0f, 0f, new Random().nextFloat() * x_max, new Random().nextFloat() * y_max, new Random().nextFloat() * 360f * (new Random().nextInt() > 0 ? 1 : -1), 359f * (new Random().nextInt() > 0 ? 1 : -1), true);

        ObjectAnimator animation = ObjectAnimator.ofFloat(asteroid, View.X, View.Y, path);


        animation.setDuration(4000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.start();

    }

    boolean isCollision(ImageView view1, ImageView view2) {
        int[] position1 = new int[2];
        int[] position2 = new int[2];
        view1.getLocationOnScreen(position1);
        view2.getLocationOnScreen(position2);

        Rect rectView1 = new Rect(position1[0], position1[1], position1[0] + view1.getMeasuredWidth(), position1[1] + view1.getMeasuredHeight());
        Rect rectView2 = new Rect(position2[0], position2[1], position2[0] + view2.getMeasuredWidth(), position2[1] + view2.getMeasuredHeight());

        return rectView1.intersect(rectView2);
    }


}