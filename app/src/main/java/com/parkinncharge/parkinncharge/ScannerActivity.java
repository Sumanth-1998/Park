package com.parkinncharge.parkinncharge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    ZXingScannerView ScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        ScannerView = (ZXingScannerView) findViewById(R.id.zxscan);
        ScannerView.setResultHandler(this);

        final ImageButton flash_On=(ImageButton) findViewById(R.id.flashOn);
        final ImageButton flash_Off=(ImageButton) findViewById(R.id.flashOff);
        flash_Off.setVisibility(View.INVISIBLE);
        flash_On.setVisibility(View.VISIBLE);
        flash_On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScannerView.setFlash(true);
                //Log.d("button","pressed");
                flash_Off.setVisibility(View.VISIBLE);
                flash_On.setVisibility(View.INVISIBLE);

            }
        });
        flash_Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("button","pressed");
                ScannerView.setFlash(false);
                flash_Off.setVisibility(View.INVISIBLE);
                flash_On.setVisibility(View.VISIBLE);

            }
        });




    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(this,result.getText(),Toast.LENGTH_LONG).show();
        Calendar cal=Calendar.getInstance();
        String date=Integer.toString(cal.get(Calendar.DATE));
        String month=Integer.toString(cal.get(Calendar.MONTH));
        String year=Integer.toString(cal.get(Calendar.YEAR));
        String hours=Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String minute=Integer.toString(cal.get(Calendar.MINUTE));
        String second=Integer.toString(cal.get(Calendar.SECOND));


        //MainActivity.intimeTextView.setText(""+date+"/"+month+"/"+year+" "+hours+":"+minute+":"+second);
        //onBackPressed();



    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();

    }

}
