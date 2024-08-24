package com.dmitryh2004Corp.KitchenRecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.dmitryh2004Corp.KitchenRecipes.databinding.ActivityWhatIsOnTheDinnerBinding;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class WhatIsOnTheDinnerActivity extends AppCompatActivity {
    ActivityWhatIsOnTheDinnerBinding binding;
    private final int CONTENT_VIEW_ID = 10101010;
    private final String dinners_file_location = "dinners.xml";
    DinnerListFragment dinnerListFragment;
    EditDinnerFragment editDinnerFragment;
    
    List<Dinner> dinnerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWhatIsOnTheDinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final int[] readCode = {0};
        readLocalDinners(new readLocalDinnersCallback() {
            @Override
            public void onComplete(List<Dinner> dinners, int code) {
                dinnerList = dinners;
                readCode[0] = code;
            }
        });
        HashMap<String, List<Dinner>> data = getDinnersHashMap(dinnerList);
        dinnerListFragment = new DinnerListFragment(data, readCode[0]);
        dinnerListFragment.setListener(new DinnerListFragment.OnDinnerItemClickListener() {
            @Override
            public void onDinnerItemClick(Dinner clickedDinner) {
                editDinner(clickedDinner);
            }
        });

        binding.fragmentContainer.setId(CONTENT_VIEW_ID);

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(CONTENT_VIEW_ID, dinnerListFragment);
        fTrans.commit();
    }

    private void editDinner(Dinner clickedDinner) {
        editDinnerFragment = new EditDinnerFragment(clickedDinner);
        editDinnerFragment.setCloseFragmentListener(new EditDinnerFragment.OnCloseFragmentListener() {
            @Override
            public void onClose() {
                readLocalDinners(new readLocalDinnersCallback() {
                    @Override
                    public void onComplete(List<Dinner> dinners, int code) {
                        dinnerList = dinners;
                        HashMap<String, List<Dinner>> data = getDinnersHashMap(dinners);
                        dinnerListFragment.setDinners(data);
                        onBackPressed();
                    }
                });

                /*FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
                fTrans.replace(CONTENT_VIEW_ID, dinnerListFragment);
                fTrans.commit();*/
            }
        });
        editDinnerFragment.setSaveDinnerListener(new EditDinnerFragment.OnSaveDinnerListener() {
            @Override
            public void onSave(Dinner oldDinner, Dinner newDinner) {
                int index = dinnerList.indexOf(oldDinner);
                if (index != -1) {
                    dinnerList.set(index, newDinner);
                }
                else {
                    dinnerList.add(newDinner);
                }
                try {
                    saveDinnersToXML(new saveLocalDinnersCallback() {
                        @Override
                        public void onComplete() {

                        }
                    });
                } catch (Exception e) {
                    Snackbar.make(binding.getRoot(), "Ошибка при сохранении ужинов.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        editDinnerFragment.setDeleteDinnerListener(new EditDinnerFragment.OnDeleteDinnerListener() {
            @Override
            public void onDelete(Dinner dinner) {
                dinnerList.remove(dinner);
                try {
                    saveDinnersToXML(new saveLocalDinnersCallback() {
                        @Override
                        public void onComplete() {

                        }
                    });
                } catch (Exception e) {
                    Snackbar.make(binding.getRoot(), "Ошибка при сохранении ужинов.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(CONTENT_VIEW_ID, editDinnerFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @NonNull
    private static HashMap<String, List<Dinner>> getDinnersHashMap(List<Dinner> dinners) {
        HashMap<String, List<Dinner>> data = new HashMap<>();
        List<String> categories = new ArrayList<>();

        dinners = AlphabeticSorter.AlphabeticSort(dinners);
        //first run - creating categories
        for (Dinner dinner: dinners) {
            if (!categories.contains(dinner.getCategory())) {
                categories.add(dinner.getCategory());
            }
        }

        categories.sort(Comparator.naturalOrder());
        //second run - adding to hash table
        for (Dinner dinner: dinners) {
            String category = dinner.getCategory();
            if (!data.containsKey(category)) {
                List<Dinner> temp = new ArrayList<>();
                temp.add(dinner);
                data.put(category, temp);
            }
            else {
                List<Dinner> temp = data.get(category);
                temp.add(dinner);
                data.put(category, temp);
            }
        }
        return data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.what_is_on_the_dinner_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_option_recipes) {
            Intent intent = new Intent(WhatIsOnTheDinnerActivity.this, RecipeListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        else {
            Snackbar.make(binding.getRoot(), "Еще не реализовано!", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            saveDinnersToXML(new saveLocalDinnersCallback() {
                @Override
                public void onComplete() {

                }
            });
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), "Ошибка при сохранении вариантов ужина.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private interface readLocalDinnersCallback {
        void onComplete(List<Dinner> dinners, int code);
    }

    private interface saveLocalDinnersCallback {
        void onComplete();
    }
    private void readLocalDinners(readLocalDinnersCallback callback) {
        int code = 0;
        /*xml format:
        *
        * <dinners>
            <dinner>
                <title>(text)</title>
                <image>(bitmap)</image> // optional
            </dinner>
          </dinners>
        * */
        List<Dinner> dinners = null;

        // Проверка существует ли файл, если нет - создаем пустой
        File file = new File(getFilesDir(), dinners_file_location);

        if (!file.exists()) {
            try {
                FileOutputStream fos = openFileOutput(dinners_file_location, Context.MODE_PRIVATE);
                fos.write("".getBytes()); // Создаем пустой файл
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // чтение xml-файла с ужинами
        try {
            InputStream inputStream = openFileInput(dinners_file_location);
            DinnersXMLParser parser = new DinnersXMLParser();
            parser.parse(this, inputStream);
            dinners = parser.getDinners();
        }
        catch (IOException e) {
            code = -1; // can't read dinners file
        } catch (XmlPullParserException e) {
            code = -2; // xml parse error
        }

        callback.onComplete(dinners, code);
    }

    private void saveDinnersToXML(saveLocalDinnersCallback callback) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Create a new Document
        Document document = builder.newDocument();

        // Create root element
        Element root = document.createElement("dinners");

        for (Dinner dinner: dinnerList) {
            Element child = document.createElement("dinner");
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(dinner.getName()));
            child.appendChild(name);

            if (dinner.getImage() != null) {
                Element bitmap = document.createElement("image");

                Bitmap image = dinner.getImage();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                bitmap.appendChild(document.createTextNode(encoded));
                child.appendChild(bitmap);
            }

            Element category = document.createElement("category");
            category.appendChild(document.createTextNode(dinner.getCategory()));
            child.appendChild(category);

            root.appendChild(child);
        }

        document.appendChild(root);

        // Write to XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        // Specify your local file path
        StreamResult result = new StreamResult(new File(getFilesDir(), dinners_file_location));
        transformer.transform(source, result);

        callback.onComplete();
    }
}