package com.dmitryh2004Corp.KitchenRecipes;

import android.graphics.Bitmap;

public class Dinner {
    String name = null;
    Bitmap image = null;
    String category = null;

    public Dinner() {
    }

    public Dinner(String name, Bitmap image, String category) {
        this.name = name;
        this.image = image;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
