package com.example.KitchenRecipes;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.KitchenRecipes.databinding.FragmentDinnerListBinding;
import com.google.android.material.snackbar.Snackbar;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DinnerListFragment extends Fragment {
    FragmentDinnerListBinding binding;
    HashMap<String, List<Dinner>> dinners;
    int code;

    private ActivityResultLauncher<Intent> createFileLauncher;
    private ActivityResultLauncher<Intent> readFileLauncher;

    public interface UploadListener {
        void onUpload(Uri uri);
    }

    public interface DownloadListener {
        void onDownload(Uri uri);
    }

    UploadListener uploadListener;
    DownloadListener downloadListener;

    public DinnerListFragment(HashMap<String, List<Dinner>> dinners, int code) {
        this.dinners = dinners;
        this.code = code;
    }

    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void setDinners(HashMap<String, List<Dinner>> dinners) {
        this.dinners = dinners;
    }

    public interface OnDinnerItemClickListener {
        void onDinnerItemClick(Dinner clickedDinner);
    }

    private OnDinnerItemClickListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        saveFile(uri);
                    }
                });
        readFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        readFile(uri);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDinnerListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void setListener(OnDinnerItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFragment();

        binding.addDinnerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDinnerItemClick(null);
            }
        });

        binding.uploadDinnersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFilePath();
            }
        });

        binding.downloadDinnersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFragment();
    }

    public void updateFragment() {
        if ((dinners != null) && (!dinners.isEmpty())) {
            binding.dinnerListLayout.setVisibility(View.VISIBLE);
            binding.dinnersNotFoundLayout.setVisibility(View.GONE);
            List<String> categories = new ArrayList<>(dinners.keySet());
            DinnerAdapter adapter = new DinnerAdapter(getContext(), categories, dinners);
            adapter.setListener(new DinnerAdapter.OnChildItemClickListener() {
                @Override
                public void onClick(Dinner clickedDinner) {
                    listener.onDinnerItemClick(clickedDinner);
                }
            });

            binding.dinnerList.setAdapter(adapter);
        }
        else {
            binding.dinnerListLayout.setVisibility(View.GONE);
            binding.dinnersNotFoundLayout.setVisibility(View.VISIBLE);
            if (code == -1) {
                binding.dinnersNotFoundReason.setText("Не удалось прочитать файл с ужинами");
            }
            else if (code == -2) {
                binding.dinnersNotFoundReason.setText("Ошибка при разборе файла с ужинами");
            }
            else {
                binding.dinnersNotFoundReason.setText(R.string.no_dinners_text);
            }
        }
    }

    //выгрузка файла
    private void chooseFilePath() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/plain");
        createFileLauncher.launch(intent);
    }

    private void saveFile(Uri uri) {
        uploadListener.onUpload(uri);
    }

    // загрузка файла
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        readFileLauncher.launch(intent);
    }

    private void readFile(Uri uri) {
        downloadListener.onDownload(uri);
        updateFragment();
    }
}