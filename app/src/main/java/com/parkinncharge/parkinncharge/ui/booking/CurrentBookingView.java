package com.parkinncharge.parkinncharge.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CurrentBookingView extends ViewModel {

    private MutableLiveData<String> mText;

    public CurrentBookingView() {
        mText = new MutableLiveData<>();
        mText.setValue("This is current booking fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}