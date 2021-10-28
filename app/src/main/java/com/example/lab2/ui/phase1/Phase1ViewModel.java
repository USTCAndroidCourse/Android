package com.example.lab2.ui.phase1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Phase1ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Phase1ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("USTC Desserts");
    }

    public LiveData<String> getText() {
        return mText;
    }
}