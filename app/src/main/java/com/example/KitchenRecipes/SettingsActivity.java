package com.example.KitchenRecipes;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.KitchenRecipes.databinding.ActivitySettingsBinding;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

    Button currentButton = null, prevButton = null;

    OnBackPressedCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeController.applyTheme(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.redThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeController.setTheme(SettingsActivity.this, ThemeController.ThemeName.RED);
                update();
            }
        });

        binding.blueThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeController.setTheme(SettingsActivity.this, ThemeController.ThemeName.BLUE);
                update();
            }
        });

        binding.purpleThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeController.setTheme(SettingsActivity.this, ThemeController.ThemeName.PURPLE);
                update();
            }
        });

        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.remove();
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finishAffinity();
            }
        });

        applyCurrentButton();
    }

    private void applyCurrentButton() {
        ThemeController.ThemeName themeName = ThemeController.getTheme(this);
        if (themeName == ThemeController.ThemeName.RED) {
            currentButton = binding.redThemeBtn;
        }
        else if (themeName == ThemeController.ThemeName.BLUE) {
            currentButton = binding.blueThemeBtn;
        }
        else if (themeName == ThemeController.ThemeName.PURPLE) {
            currentButton = binding.purpleThemeBtn;
        }
        else {
            currentButton = binding.redThemeBtn;
        }

        currentButton.setEnabled(false);
        currentButton.setText(new String(Character.toChars(0x2705)));
    }

    private void update() {
        ThemeController.applyTheme(this);

        prevButton = currentButton;
        applyCurrentButton();

        if (prevButton != null) {
            prevButton.setEnabled(true);
            prevButton.setText("");
        }


        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Snackbar.make(binding.getRoot(), "Перезапустите приложение через кнопку \"Перезапуск\"", Snackbar.LENGTH_LONG).show();
            }
        };

        getOnBackPressedDispatcher().addCallback(callback);
        binding.restartBtn.setEnabled(true);
    }
}