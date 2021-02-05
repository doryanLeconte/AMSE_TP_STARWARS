package com.imt.amse_tp_starwars;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView joystick = findViewById(R.id.joystick_center);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FrameLayout gameZone = findViewById(R.id.game_zone);

        moveAsteroid(findViewById(R.id.asteroid2));
        moveAsteroid(findViewById(R.id.asteroid4));

    }

    protected void moveAsteroid(View asteroid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Path path = new Path();
            View game_zone = findViewById(R.id.game_zone);
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
}