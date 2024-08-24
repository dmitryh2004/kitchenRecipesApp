package com.dmitryh2004Corp.KitchenRecipes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dmitryh2004Corp.KitchenRecipes.databinding.ActivityWelcomeScreenBinding;

public class WelcomeScreenActivity extends AppCompatActivity {
    ActivityWelcomeScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeScreenBinding.bind(
                getLayoutInflater().inflate(R.layout.activity_welcome_screen, null));
        setContentView(binding.getRoot());

        SharedPreferences shPref = getSharedPreferences("welcomeScreen", MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();
        editor.putBoolean("firstRun", false);
        editor.apply();

        //todo: добавить 3 привествтенных экрана из фигмы, убрать автопереход
        Intent intent = new Intent(WelcomeScreenActivity.this, RecipeListActivity.class);
        startActivity(intent);
        finish();
    }
}