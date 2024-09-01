package com.example.KitchenRecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.KitchenRecipes.databinding.ActivityRecipeListBinding;
import com.google.android.material.snackbar.Snackbar;

public class RecipeListActivity extends AppCompatActivity {
    ActivityRecipeListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeController.applyTheme(this);
        binding = ActivityRecipeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addRecipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(binding.getRoot(), "Еще не реализовано!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_option_dinner) {
            Intent intent = new Intent(RecipeListActivity.this, WhatIsOnTheDinnerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_option_settings) {
            Intent intent = new Intent(RecipeListActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        else {
            Snackbar.make(binding.getRoot(), "Еще не реализовано!", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}