package com.example.lab2.ui.phase4;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Phase4ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Phase4ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is test fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}