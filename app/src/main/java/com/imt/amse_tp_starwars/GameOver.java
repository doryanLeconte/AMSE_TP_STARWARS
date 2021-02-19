package com.imt.amse_tp_starwars;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {


    private Button restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        restart = findViewById(R.id.restart_button);


    }

    @Override
    protected void onResume() {
        super.onResume();
        restart.setOnClickListener((View v) -> {
            finish();
            getApplicationContext().startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


        });
    }
}