package com.example.KitchenRecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.KitchenRecipes.databinding.ActivityWhatIsOnTheDinnerBinding;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
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
    List<String> categories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeController.applyTheme(this);
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
        dinnerListFragment.setUploadListener(new DinnerListFragment.UploadListener() {
            @Override
            public void onUpload(Uri uri) {
                try {
                    saveDinnersToXML(new saveLocalDinnersCallback() {
                        @Override
                        public void onComplete() {
                            try {
                                InputStream is = openFileInput(dinners_file_location);
                                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                                reader.close();
                                is.close();

                                OutputStream os = getContentResolver().openOutputStream(uri);
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                                writer.write(sb.toString());
                                writer.close();
                                os.close();
                            }
                            catch (Exception e) {
                                Snackbar.make(binding.getRoot(), "Ошибка при выгрузке списка ужинов: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                            Snackbar.make(binding.getRoot(), "Список ужинов успешно выгружен.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e) {
                    Snackbar.make(binding.getRoot(), "Ошибка при выгрузке списка ужинов: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        dinnerListFragment.setDownloadListener(new DinnerListFragment.DownloadListener() {
            @Override
            public void onDownload(Uri uri) {
                try {
                    transferDinnersFromUri(uri, new transferDinnersCallback() {
                        @Override
                        public void onComplete() {
                            readLocalDinners(new readLocalDinnersCallback() {
                                @Override
                                public void onComplete(List<Dinner> dinners, int code) {
                                    dinnerList = dinners;
                                    HashMap<String, List<Dinner>> data = getDinnersHashMap(dinners);
                                    dinnerListFragment.setDinners(data);
                                    // dinnerListFragment.updateFragment();
                                    Snackbar.make(binding.getRoot(), "Список ужинов успешно загружен.", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                catch (Exception e) {
                    Snackbar.make(binding.getRoot(), "Ошибка при загрузке: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
        editDinnerFragment = new EditDinnerFragment(clickedDinner, categories);
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
    private HashMap<String, List<Dinner>> getDinnersHashMap(List<Dinner> dinners) {
        HashMap<String, List<Dinner>> data = new HashMap<>();
        List<String> categories = new ArrayList<>();

        dinners.sort(Comparator.comparing(Dinner::getCategory).thenComparing(Dinner::getName));
        //first run - creating categories
        for (int i = 0; i < dinners.size(); i++) {
            Dinner dinner = dinners.get(i);
            if (!categories.contains(dinner.getCategory())) {
                categories.add(dinner.getCategory());
            }
        }

        categories.sort(Comparator.naturalOrder());
        this.categories = categories;
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
        else if (item.getItemId() == R.id.menu_option_settings) {
            Intent intent = new Intent(WhatIsOnTheDinnerActivity.this, SettingsActivity.class);
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

    private interface transferDinnersCallback {
        void onComplete();
    }
    private void transferDinnersFromUri(Uri uri, transferDinnersCallback callback) throws Exception {
        try {
            OutputStream outputStream = openFileOutput(dinners_file_location, Context.MODE_PRIVATE);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String xml = new String(bytes, StandardCharsets.UTF_8);
            if (DinnerXMLValidator.validateXML(xml)) {
                outputStream.write(bytes);
                outputStream.close();
                inputStream.close();
            }
            else {
                throw new Exception("Этот файл не является списком ужинов.");
            }
        } catch (Exception e) {
            if (e.getMessage() == "Этот файл не является списком ужинов.") {
                throw e;
            }
            e.printStackTrace();
        }

        callback.onComplete();
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