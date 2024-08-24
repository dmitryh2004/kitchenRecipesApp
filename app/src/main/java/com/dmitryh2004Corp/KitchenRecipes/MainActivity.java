package com.dmitryh2004Corp.KitchenRecipes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.dmitryh2004Corp.KitchenRecipes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int welcomeScreenDuration = 2500;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences shPref = getSharedPreferences("welcomeScreen", MODE_PRIVATE);
        boolean firstRun = shPref.getBoolean("firstRun", true);
        //todo: add animation for <welcomeScreenDuration>/1000 s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if (firstRun) {
                    intent = new Intent(MainActivity.this, WelcomeScreenActivity.class);
                }
                else {
                    intent = new Intent(MainActivity.this, RecipeListActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, welcomeScreenDuration);
    }
}