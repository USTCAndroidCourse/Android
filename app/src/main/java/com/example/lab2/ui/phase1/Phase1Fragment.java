package com.example.lab2.ui.phase1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lab2.databinding.FragmentPhase1Binding;
//import com.example.lab2.databinding.FragmentPhase1Binding;

public class Phase1Fragment extends Fragment {

    private Phase1ViewModel homeViewModel;
    private FragmentPhase1Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(Phase1ViewModel.class);

        binding = FragmentPhase1Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPhase1;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}