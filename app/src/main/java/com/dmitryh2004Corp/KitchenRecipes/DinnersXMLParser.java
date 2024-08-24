package com.dmitryh2004Corp.KitchenRecipes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DinnersXMLParser {
    private final List<Dinner> dinners = new ArrayList<>();

    public List<Dinner> getDinners() {
        return dinners;
    }

    public void parse (Context context, InputStream inputStream) throws XmlPullParserException, java.io.IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        Dinner currentDinner = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equals("dinner")) {
                        currentDinner = new Dinner();
                    } else if (currentDinner != null) {
                        if (tagName.equals("name")) {
                            currentDinner.setName(parser.nextText());
                        } else if (tagName.equals("image")) {
                            // Преобразование строки в Bitmap. Например, если это base64:
                            String base64Image = parser.nextText();
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            currentDinner.setImage(bitmap);
                        } else if (tagName.equals("category")) {
                            currentDinner.setCategory(parser.nextText());
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (tagName.equals("dinner") && currentDinner != null) {
                        dinners.add(currentDinner);
                    }
                    break;
            }
            eventType = parser.next();
        }
    }
}
