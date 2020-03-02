package com.parkinncharge.parkinncharge.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.parkinncharge.parkinncharge.R;

public class current_booking extends Fragment {

    private CurrentBookingView currentBookingView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        currentBookingView =
                ViewModelProviders.of(this).get(CurrentBookingView.class);
        View root = inflater.inflate(R.layout.current_booking, container, false);
        final TextView textView = root.findViewById(R.id.text_send);
        currentBookingView.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}