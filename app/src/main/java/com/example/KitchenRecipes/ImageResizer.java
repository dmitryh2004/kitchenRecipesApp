package com.example.KitchenRecipes;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;

public class ImageResizer {

    public static Bitmap resizeImage(File imageFile, int w, int h) {
        // Получение bitmap из файла
        Bitmap originalBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        // Вычисление масштаба
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        float scale = Math.min(w / width, h / height);

        // Создание матрицы для изменения размера
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Ресайзинг изображения
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    public static Bitmap resizeImage(Bitmap originalBitmap, int w, int h) {

        // Вычисление масштаба
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        float scale = Math.min((float) w / width, (float) h / height);

        // Создание матрицы для изменения размера
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Ресайзинг изображения
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }
}

