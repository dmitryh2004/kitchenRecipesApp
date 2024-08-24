package com.dmitryh2004Corp.KitchenRecipes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmitryh2004Corp.KitchenRecipes.databinding.FragmentDinnerListBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DinnerListFragment extends Fragment {
    FragmentDinnerListBinding binding;
    HashMap<String, List<Dinner>> dinners;
    int code;

    public DinnerListFragment(HashMap<String, List<Dinner>> dinners, int code) {
        this.dinners = dinners;
        this.code = code;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFragment();
    }

    private void updateFragment() {
        if ((dinners != null) && (!dinners.isEmpty())) {
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
}