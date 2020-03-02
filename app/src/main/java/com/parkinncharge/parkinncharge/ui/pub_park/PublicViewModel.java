package com.parkinncharge.parkinncharge.ui.pub_park;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PublicViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PublicViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is public park fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}