package com.example.KitchenRecipes;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeController {
    enum ThemeName {
        RED(0),
        PURPLE(1),
        BLUE(2);

        private int code;
        ThemeName(int code) {
            this.code = code;
        }

        public int getCode() { return this.code; }
    }
    public static void applyTheme(Context context) {
        SharedPreferences shPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String theme = shPref.getString("theme", "red");

        switch (theme) {
            case "blue":
                context.setTheme(R.style.KitchenRecipes_BlueTheme);
                break;
            case "purple":
                context.setTheme(R.style.KitchenRecipes_PurpleTheme);
                break;
            default:
                context.setTheme(R.style.KitchenRecipes_RedTheme);
        }
    }

    public static ThemeName getTheme(Context context) {
        SharedPreferences shPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String theme = shPref.getString("theme", "red");

        switch (theme) {
            case "blue":
                return ThemeName.BLUE;
            case "purple":
                return ThemeName.PURPLE;
            default:
                return ThemeName.RED;
        }
    }

    public static void setTheme(Context context, ThemeName theme) {
        SharedPreferences shPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();

        String themeColor = "red";
        switch (theme) {
            case RED:
                themeColor = "red";
                break;
            case BLUE:
                themeColor = "blue";
                break;
            case PURPLE:
                themeColor = "purple";
                break;
        }
        editor.putString("theme", themeColor);
        editor.apply();
    }
}
