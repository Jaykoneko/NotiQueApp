package com.ut.notique.ui.memes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MemesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MemesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Momazos");
    }

    public LiveData<String> getText() {
        return mText;
    }
}