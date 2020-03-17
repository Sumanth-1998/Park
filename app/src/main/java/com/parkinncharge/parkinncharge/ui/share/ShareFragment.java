package com.parkinncharge.parkinncharge.ui.share;

import android.annotation.SuppressLint;
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

import java.util.Random;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;

    @SuppressLint("FragmentLiveDataObserve")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        final TextView textView1 = root.findViewById(R.id.textView2);
        textView1.setText(generateString());
        shareViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    private String generateString()
    {
        char[] chars="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder stringbuilder=new StringBuilder();
        Random rand=new Random();
        for(int i=0;i<6;i++)
        {
            char c=chars[rand.nextInt(chars.length)];
            stringbuilder.append(c);
        }
        return stringbuilder.toString();
    }
}