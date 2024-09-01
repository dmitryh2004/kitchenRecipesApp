package com.example.KitchenRecipes;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.KitchenRecipes.databinding.FragmentEditDinnerBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EditDinnerFragment extends Fragment {

    private FragmentEditDinnerBinding binding;
    Dinner dinner, newDinner = new Dinner();

    List<String> categories = new ArrayList<>();

    public EditDinnerFragment(Dinner dinner, List<String> categories) {
        this.dinner = dinner;
        this.categories = categories;
        this.categories.sort(Comparator.naturalOrder());
        if (!this.categories.contains("(нет категории)"))
            this.categories.add(0, "(нет категории)");
        this.categories.add("- Новая категория -");
    }

    public interface OnCloseFragmentListener {
        void onClose();
    }

    public interface OnDeleteDinnerListener {
        void onDelete(Dinner dinner);
    }

    public interface OnSaveDinnerListener {
        void onSave(Dinner oldDinner, Dinner newDinner);
    }

    OnCloseFragmentListener closeFragmentListener;
    OnDeleteDinnerListener deleteDinnerListener;
    OnSaveDinnerListener saveDinnerListener;

    public void setCloseFragmentListener(OnCloseFragmentListener closeFragmentListener) {
        this.closeFragmentListener = closeFragmentListener;
    }

    public void setDeleteDinnerListener(OnDeleteDinnerListener deleteDinnerListener) {
        this.deleteDinnerListener = deleteDinnerListener;
    }

    public void setSaveDinnerListener(OnSaveDinnerListener saveDinnerListener) {
        this.saveDinnerListener = saveDinnerListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditDinnerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dinnerCategorySpinner.setAdapter(adapter);

        if (dinner != null) {
            binding.dinnerNameEditText.setText(dinner.getName());
            if (dinner.getImage() != null)
                binding.dinnerImageView.setImageBitmap(dinner.getImage());
            if (!dinner.getCategory().isEmpty()) {
                binding.dinnerCategoryEditText.setText(dinner.getCategory());
                binding.dinnerCategorySpinner.setSelection(categories.indexOf(dinner.getCategory()));
            }
        }

        binding.dinnerCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == categories.indexOf("- Новая категория -")) {
                    binding.dinnerCategoryEditText.setVisibility(View.VISIBLE);
                    binding.dinnerCategoryEditText.setText("");
                }
                else {
                    binding.dinnerCategoryEditText.setVisibility(View.GONE);
                    binding.dinnerCategoryEditText.setText(categories.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.dinnerCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragmentListener.onClose();
            }
        });

        binding.dinnerDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDinnerListener.onDelete(dinner);
                closeFragmentListener.onClose();
            }
        });

        binding.dinnerSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDinner.setName(binding.dinnerNameEditText.getText().toString());
                String newCategory = binding.dinnerCategoryEditText.getText().toString();
                if (newCategory.isEmpty()) {
                    newDinner.setCategory("(нет категории)");
                }
                else if (newCategory.equals("- Новая категория -")) {
                    Snackbar.make(binding.getRoot(), "Вы не можете создать категорию с таким именем.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else {
                    newDinner.setCategory(newCategory);
                }
                if (newDinner.getImage() == null) {
                    if (dinner != null)
                        newDinner.setImage(dinner.getImage());
                }
                saveDinnerListener.onSave(dinner, newDinner);
                closeFragmentListener.onClose();
            }
        });

        binding.dinnerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Выберите источник изображения");
                builder.setItems(new CharSequence[]{"Камера", "Галерея"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchCameraIntent();
                                break;
                            case 1:
                                dispatchGalleryIntent();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void dispatchGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void dispatchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(takePictureIntent);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if ((result.getResultCode() == RESULT_OK) && (result.getData() != null)) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    setImage(bitmap);
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if ((result.getResultCode() == RESULT_OK) && (result.getData() != null)) {
                    Uri selectedImage = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                        setImage(bitmap);
                    } catch (Exception e) {
                        Snackbar.make(binding.getRoot(), R.string.image_set_error, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void setImage(Bitmap bitmap) {
        Bitmap resizedBitmap = ImageResizer.resizeImage(bitmap, 100, 100);
        binding.dinnerImageView.setImageBitmap(resizedBitmap);
        newDinner.setImage(resizedBitmap);
    }


}