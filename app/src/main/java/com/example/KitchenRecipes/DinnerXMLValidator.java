package com.example.KitchenRecipes;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.*;

public class DinnerXMLValidator {
    public static boolean validateXML(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
            document.getDocumentElement().normalize();

            NodeList dinnerList = document.getElementsByTagName("dinner");

            for (int i = 0; i < dinnerList.getLength(); i++) {
                Element dinner = (Element) dinnerList.item(i);

                if (dinner.getElementsByTagName("name").getLength() == 0 ||
                        dinner.getElementsByTagName("category").getLength() == 0) {
                    return false; // name and category are mandatory
                }

                // image is optional, so no need to check if it exists
                NodeList imageList = dinner.getElementsByTagName("image");
                // You can check for specific conditions for the image here if needed
            }
            return true; // Structure is valid
        } catch (Exception e) {
            e.printStackTrace();
            return false; // In case of any exception, return false
        }
    }
}

