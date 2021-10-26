package com.example.lab2.ui.phase4;

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

import com.example.lab2.databinding.FragmentPhase3Binding;

public class Phase4Fragment extends Fragment {

    private Phase4ViewModel testViewModel;
    private FragmentPhase3Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        testViewModel =
                new ViewModelProvider(this).get(Phase4ViewModel.class);

        binding = FragmentPhase3Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPhase3;
        testViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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