package com.imt.amse_tp_starwars;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final float SPEED = 0.01f;
    boolean joystickIsPressed;
    float joystickCenterX;
    float joystickCenterY;
    private View game_zone;
    private ConstraintLayout constraintLayout;
    private ConstraintLayout joystickConstrainLayout;
    private Boolean calibrateSensor;
    private ImageView joystick;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private SwitchMaterial accelerometre_switch;
    private ImageView joystick1;
    private ImageView vaisseau;
    private ImageView asteroid2;
    private ImageView asteroid4;
    private float initialAccelerometreValueX;
    private float initialAccelerometreValueY;
    private float initialAccelerometreValueZ;
    private FloatingActionButton calibrateAccelerometre;
    private float dx;
    private float dy;
    private boolean isAccelerometreChecked;
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        calibrateAccelerometre = findViewById(R.id.floatingActionButton);
        calibrateAccelerometre.setVisibility(View.GONE);

        isAccelerometreChecked = false;

        joystick = findViewById(R.id.joystick_center);
        joystickConstrainLayout = findViewById(R.id.joystick);

        accelerometre_switch = findViewById(R.id.switch_accelerometre);
        accelerometre_switch.setOnCheckedChangeListener(this::onSwitchChange);

        calibrateSensor = true;

        joystickCenterX = joystick.getTranslationX();
        joystickCenterY = joystick.getTranslationY();


    }

    @Override
    protected void onResume() {
        super.onResume();
        started = false;
        moveAsteroid(findViewById(R.id.asteroid2));
        moveAsteroid(findViewById(R.id.asteroid4));

        joystick1 = findViewById(R.id.joystick_center);
        vaisseau = findViewById(R.id.vaisseau);
        asteroid2 = findViewById(R.id.asteroid2);
        asteroid4 = findViewById(R.id.asteroid4);
        game_zone = findViewById(R.id.game_zone);
        constraintLayout = findViewById(R.id.constraintLayout);


        Handler handlerForMovingTie = new Handler();
        Runnable movingTie = new Runnable() {
            @Override
            public void run() {


                if (joystickIsPressed) {
                    handlerForMovingTie.postDelayed(this, 10);

                    float translationX = joystick1.getTranslationX();
                    float translationY = joystick1.getTranslationY();

                    moveVaisseau(translationX, translationY, vaisseau);
                }
            }
        };

        Handler handlerForDetection = new Handler();

        Runnable detectCollision = new Runnable() {
            @Override
            public void run() {
                handlerForDetection.postDelayed(this, 10);

                if (started) {

                    if (isCollision(vaisseau, asteroid2) || isCollision(vaisseau, asteroid4)) {
                        ImageView explosion = new ImageView(getApplicationContext());
                        explosion.setImageDrawable(getDrawable(R.drawable.explosion));
                        LayoutParams explosionParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                        explosionParams.addRule(RelativeLayout.ABOVE, vaisseau.getId());
                        explosion.setLayoutParams(explosionParams);
                        explosion.setTranslationX(vaisseau.getTranslationX());
                        explosion.setTranslationY(vaisseau.getTranslationY());

                        constraintLayout.addView(explosion);


                        getApplicationContext().startActivity(new Intent(MainActivity.this, GameOver.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        started = false;
                        finish();
                        Log.d("COLLISION", "COLLIDED");
                    }

                }
            }

        };

        detectCollision.run();

        calibrateAccelerometre.setOnClickListener((view) -> {
            calibrateSensor = true;
        });

        joystick1.setOnTouchListener((v, event) -> {
            float x;
            float y;

            x = event.getRawX() + dx;
            y = event.getRawY() + dy;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    started = true;
                    dx = joystick1.getX() - event.getRawX();
                    dy = joystick1.getY() - event.getRawY();
                    joystickIsPressed = true;
                    break;
                case MotionEvent.ACTION_UP:
                    joystick1.setTranslationX(joystickCenterX);
                    joystick1.setTranslationY(joystickCenterY);
                    joystickIsPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    joystick1.setX(x);
                    joystick1.setY(y);
                    movingTie.run();
                    break;
                default:
                    return false;

            }
            return true;
        });

    }

    private void moveVaisseau(float translationX, float translationY, ImageView vaisseau) {
        if (vaisseau.getX() + vaisseau.getWidth() < game_zone.getWidth() && translationX > 0)
            vaisseau.setTranslationX(vaisseau.getTranslationX() + translationX * SPEED);
        if (vaisseau.getX() > 0 && translationX < 0)
            vaisseau.setTranslationX(vaisseau.getTranslationX() + translationX * SPEED);
        if (vaisseau.getY() + vaisseau.getHeight() < game_zone.getHeight() && translationY > 0)
            vaisseau.setTranslationY(vaisseau.getTranslationY() + translationY * SPEED);
        if (vaisseau.getY() > 0 && translationY < 0)
            vaisseau.setTranslationY(vaisseau.getTranslationY() + translationY * SPEED);
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (calibrateSensor) {
            initialAccelerometreValueX = event.values[0];
            initialAccelerometreValueY = event.values[1];
            initialAccelerometreValueZ = event.values[2];
            calibrateSensor ^= calibrateSensor;
        }
        float gammaX = event.values[0];
        float gammaY = event.values[1];
        float gammaZ = event.values[2];
        Log.d("Valeurs accelerometre", gammaX + ", " + gammaY + ", " + gammaZ);
        moveVaisseau(-(gammaX - initialAccelerometreValueX) * 5000, -(gammaZ - initialAccelerometreValueZ) * 5000, vaisseau);
    }

    public void onSwitchChange(CompoundButton buttonView, boolean isChecked) {
        this.isAccelerometreChecked = isChecked;
        Log.d("SWITCH", "onSwitchChange: " + this.isAccelerometreChecked);
        if (isChecked) {
            started = true;
            joystickConstrainLayout.setVisibility(View.GONE);
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            calibrateAccelerometre.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Appuyer sur le bouton pour calibrer l'accéléromètre", Toast.LENGTH_SHORT).show();

        } else {
            joystickConstrainLayout.setVisibility(View.VISIBLE);
            calibrateAccelerometre.setVisibility(View.GONE);
            mSensorManager.unregisterListener(this);
        }
    }

}