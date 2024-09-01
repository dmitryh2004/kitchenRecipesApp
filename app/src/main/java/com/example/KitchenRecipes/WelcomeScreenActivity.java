package com.example.KitchenRecipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.KitchenRecipes.databinding.ActivityWelcomeScreenBinding;

public class WelcomeScreenActivity extends AppCompatActivity {
    ActivityWelcomeScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeController.applyTheme(this);
        binding = ActivityWelcomeScreenBinding.bind(
                getLayoutInflater().inflate(R.layout.activity_welcome_screen, null));
        setContentView(binding.getRoot());

        SharedPreferences shPref = getSharedPreferences("welcomeScreen", MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();
        editor.putBoolean("firstRun", false);
        editor.apply();

        //todo: добавить 3 привествтенных экрана из фигмы, убрать автопереход
        Intent intent = new Intent(WelcomeScreenActivity.this, WhatIsOnTheDinnerActivity.class);
        startActivity(intent);
        finish();
    }
}