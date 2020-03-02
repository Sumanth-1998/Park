package com.parkinncharge.parkinncharge.ui.pub_park;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.parkinncharge.parkinncharge.R;
import com.parkinncharge.parkinncharge.Time_Picker_Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class public_parking extends DialogFragment implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "public_parking";

    private PublicViewModel publicViewModel;
    private FirebaseFirestore db;
    TextView spaces_available;
    TextView startTime1;
    Map<String, Long> malls;
    int count = 0;
    View root;//=(TextView) view.findViewById(R.id.space_available);
    private int mins,hours;
    //TextView spaces_available;
    private static Handler myHandler=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        publicViewModel =
                ViewModelProviders.of(this).get(PublicViewModel.class);
        root = inflater.inflate(R.layout.public_parking, container, false);
        startTime1 = (TextView) root.findViewById(R.id.startTime2);
        //
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                startTime1.setText("Kothi");
            }
        };
        malls = new HashMap<>();
        Spinner spinner = (Spinner) root.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayList<String> mall_names = new ArrayList<String>();
        ;
        mall_names.add("Select you place");

        ArrayAdapter<String> arrayAdapter;
        db = FirebaseFirestore.getInstance();
        db.collection("malls").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Output:", "" + document.get("Available Parking"));

                                malls.put(document.getId(), (Long) document.get("Available Parking"));
                                mall_names.add(document.getId());
                                //Log.d("Malls",malls.toString());
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, mall_names);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(arrayAdapter);

                        }
                    }
                });

        //Log.d("Malls",malls.toString());

        //Log.d("ArrayList",""+mall_names.toString());


        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spaces_available = (TextView) root.findViewById(R.id.space_available);

        if (++count > 1) {
            if (position != 0) {
                spaces_available.setVisibility(VISIBLE);
                Log.d("Position", "" + position);
                String item = (String) parent.getItemAtPosition(position);
                Log.d("Item", "" + malls.get(item));
                spaces_available.setText("No of Slots Available: " + malls.get(item));

                startTime1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment timePicker = new public_parking();
                        timePicker.show(getChildFragmentManager(), "time picker");
                        
                        Toast.makeText(getActivity(), "before", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                onNothingSelected(parent);
                spaces_available.setVisibility(View.INVISIBLE);
            }
        }
        //spaces_available.setVisibility(View.VISIBLE);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(), "Please Select the Place", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Log.d("Hours", "" + hourOfDay);
        Log.d("Mins", "" + minute);
        hours=hourOfDay;
        mins=minute;
        Toast.makeText(getActivity(), ""+hours+":"+minute, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTimeSet: " + hours);
        myHandler.sendEmptyMessage(1);
        Toast.makeText(getActivity(), startTime1.getText().toString(), Toast.LENGTH_SHORT).show();;

    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        //startTime1 = (TextView) root.findViewById(R.id.startTime2);
        //startTime1.setText("Hello");
        return new TimePickerDialog(getActivity(), this, hour, min, DateFormat.is24HourFormat(getActivity()));
    }


}