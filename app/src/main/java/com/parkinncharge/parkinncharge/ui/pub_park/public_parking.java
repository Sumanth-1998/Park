package com.parkinncharge.parkinncharge.ui.pub_park;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;
import com.parkinncharge.parkinncharge.Payment_Activity;
import com.parkinncharge.parkinncharge.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import static android.view.View.VISIBLE;

public class public_parking extends Fragment implements AdapterView.OnItemSelectedListener, PaymentResultListener {
    private static final String TAG = "public_parking";

    private PublicViewModel publicViewModel;
    private FirebaseFirestore db;
    TextView spaces_available,howLong,toTextView,fromTextView;
    TextView startTime1,totalCost,cost;
    EditText startDate;
    ImageButton calendarButton,timeButton;
    Map<String, Long> malls;
    TimePickerDialog tpick;
    DatePickerDialog dpick;
    Spinner hourSpinner,minSpinner;
    ArrayList<String> hoursRem,minrem;
    Checkout checkout;
    int count = 0,hrsSet=0,minSet=0,countHrsSpinner=0,countMinSpinner=0;
    View root;//=(TextView) view.findViewById(R.id.space_available);
    private int mins,hours;
    int remHour,remMin;
    Calendar testcal;
    //TextView spaces_available;
    private static Handler myHandler=null;
    Button calcfare,payButton;
    String time,book_date;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //RazorpayClient razorpayClient = new RazorpayClient("key_id", "key_secret");
        publicViewModel = ViewModelProviders.of(this).get(PublicViewModel.class);
        root = inflater.inflate(R.layout.public_parking, container, false);
        startTime1 = (TextView) root.findViewById(R.id.startTime2);
        calendarButton=(ImageButton) root.findViewById(R.id.calendarButton);
        startDate=(EditText) root.findViewById(R.id.startDate);
        timeButton=(ImageButton) root.findViewById(R.id.timeButton);
        howLong=(TextView) root.findViewById(R.id.howLong);
        toTextView=(TextView) root.findViewById(R.id.toTextView);
        fromTextView=(TextView) root.findViewById(R.id.from_text_view);
        hourSpinner=(Spinner) root.findViewById(R.id.hourSpinner);
        minSpinner=(Spinner) root.findViewById(R.id.minuteSpinner);
        calcfare=(Button) root.findViewById(R.id.calcFare);
        cost=(TextView) root.findViewById(R.id.cost);
        totalCost=(TextView) root.findViewById(R.id.totalCost);
        payButton=(Button) root.findViewById(R.id.payButton);
        howLong.setVisibility(View.INVISIBLE);
        hourSpinner.setVisibility(View.INVISIBLE);
        minSpinner.setVisibility(View.INVISIBLE);
        checkout=new Checkout();
        checkout.setKeyID("rzp_test_Xk70LwijqMfKDb");
        malls = new HashMap<>();
        Spinner spinner = (Spinner) root.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        hourSpinner.setOnItemSelectedListener(this);
        minSpinner.setOnItemSelectedListener(this);

        ArrayList<String> mall_names = new ArrayList<String>();
        hoursRem = new ArrayList<>();
        minrem = new ArrayList<>();


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
        startTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

        calcfare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((String)hourSpinner.getSelectedItem())=="Hours" || ((String)minSpinner.getSelectedItem())=="Minutes")
                {
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    totalCost.setVisibility(VISIBLE);
                    double fare=calculate_fare();
                    cost.setText(String.format("%.2f",fare));
                    cost.setVisibility(VISIBLE);
                    payButton.setVisibility(VISIBLE);
                }
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),startTime1.getText().toString() , Toast.LENGTH_SHORT).show();
                if(startTime1.getText().toString().compareTo("")!=0) {
                    double amount = Double.parseDouble(cost.getText().toString());
                    amount = amount * 100;
                    //startPayment(amount);
                    Intent payIntent = new Intent(getActivity(), Payment_Activity.class);
                    payIntent.putExtra("amount", amount);
                    payIntent.putExtra("type","Public Parking");
                    payIntent.putExtra("booked_date",book_date);
                    payIntent.putExtra("time",time);
                    startActivity(payIntent);
                }
                else
                {
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return root;
    }


    public double calculate_fare(){
        double hrsparked,minsparked,tot_time_parked,price_min=0.35,total_cost;
        hrsparked=Double.parseDouble((String)hourSpinner.getSelectedItem());
        minsparked=Double.parseDouble((String)minSpinner.getSelectedItem());
        tot_time_parked=hrsparked*60+minsparked;
        total_cost=tot_time_parked*price_min;
        payButton.setVisibility(VISIBLE);
        return  total_cost;

    }

    public void showTime()
    {
        hoursRem.clear();
        minrem.clear();
        hoursRem.add("Hours");
        minrem.add("Minutes");
        minrem.add(Integer.toString(0));
        minrem.add(Integer.toString(30));
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        tpick = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                        //testcal.set(Calendar.DATE,)
                        testcal.set(Calendar.HOUR_OF_DAY,sHour);
                        testcal.set(Calendar.MINUTE,sMinute);
                        if(testcal.getTime().compareTo(cal.getTime())>0) {
                            String ampm = "AM", prefix = "";
                            int hour = sHour, hourInDay = 24;
                            String min = Integer.toString(sMinute);
                            hrsSet = sHour;
                            minSet = sMinute;
                            if (sMinute < 10)
                                min = "0" + sMinute;
                            if (sHour >= 12) {
                                ampm = "PM";
                            }
                            if (sHour != 12)
                                hour = sHour % 12;

                            if (hour < 10)
                                prefix = "0";
                            time=prefix + hour + ":" + min + " " + ampm;
                            startTime1.setText(time);

                            if (sMinute >= 30) {
                                remHour = hourInDay - sHour - 1;
                                remMin = 0;


                            } else {
                                remHour = hourInDay - sHour - 1;
                                remMin = 30;

                            }
                            howLong.setVisibility(VISIBLE);
                            calcfare.setVisibility(VISIBLE);
                            showHourMinSpinner();
                            for (int i = 0; i <= remHour; i++) {
                                hoursRem.add(Integer.toString(i));
                            }
                            Toast.makeText(getActivity(), remHour + "+" + remMin, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Invalid Time", Toast.LENGTH_SHORT).show();
                            howLong.setVisibility(View.INVISIBLE);
                            hourSpinner.setVisibility(View.INVISIBLE);
                            minSpinner.setVisibility(View.INVISIBLE);
                            calcfare.setVisibility(View.INVISIBLE);
                            totalCost.setVisibility(View.INVISIBLE);
                            cost.setVisibility(View.INVISIBLE);
                            payButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }, hour, min, false);

        Log.d("HoursAdapter",hoursRem+"");
        Log.d("MinutesAdapter",minrem+"");
        Toast.makeText(getActivity(), "before", Toast.LENGTH_SHORT).show();
        tpick.show();

    }
    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getActivity(), "Payment Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getActivity(), "Payment Failed", Toast.LENGTH_SHORT).show();

    }




    public void showHourMinSpinner(){
        ArrayAdapter<String> hourAdapter;

        hourAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, hoursRem);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);

        ArrayAdapter<String> minAdapter;
        minAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, minrem);
        minAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minSpinner.setAdapter(minAdapter);

        hourSpinner.setVisibility(View.VISIBLE);
        minSpinner.setVisibility(View.VISIBLE);
    }

    public void showCalendar()
    {
        Calendar cal = Calendar.getInstance();
        int date=cal.get(Calendar.DATE);
        int month=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);

        testcal= Calendar.getInstance();

        dpick = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startTime1.setText("");
                        toTextView.setVisibility(VISIBLE);
                        startTime1.setVisibility(VISIBLE);
                        timeButton.setVisibility(VISIBLE);
                        testcal.set(Calendar.DATE,dayOfMonth);
                        testcal.set(Calendar.MONTH,monthOfYear);
                        testcal.set(Calendar.YEAR,year);
                        startDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        book_date=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    }
                }, year, month, date);
        dpick.getDatePicker().setMinDate(System.currentTimeMillis());
        dpick.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spaces_available = (TextView) root.findViewById(R.id.space_available);
        switch (parent.getId()) {
            case R.id.spinner: if (++count > 1) {
                                    /*spaces_available.setVisibility(View.INVISIBLE);
                                    fromTextView.setVisibility(View.INVISIBLE);
                                    startDate.setVisibility(View.INVISIBLE);
                                    calendarButton.setVisibility(View.INVISIBLE);
                                    toTextView.setVisibility(View.INVISIBLE);
                                    startTime1.setVisibility(View.INVISIBLE);
                                    timeButton.setVisibility(View.INVISIBLE);
                                    howLong.setVisibility(View.INVISIBLE);
                                    hourSpinner.setVisibility(View.INVISIBLE);
                                    minSpinner.setVisibility(View.INVISIBLE);
                                    calcfare.setVisibility(View.INVISIBLE);
                                    totalCost.setVisibility(View.INVISIBLE);
                                    cost.setVisibility(View.INVISIBLE);*/
                                if (position != 0) {
                                    spaces_available.setVisibility(VISIBLE);
                                    fromTextView.setVisibility(VISIBLE);
                                    startDate.setVisibility(VISIBLE);
                                    calendarButton.setVisibility(VISIBLE);
                                    Log.d("Position", "" + position);
                                    String item = (String) parent.getItemAtPosition(position);
                                    Log.d("Item", "" + malls.get(item));
                                    spaces_available.setText("No of Slots Available: " + malls.get(item));
                                } else {
                                onNothingSelected(parent);
                                spaces_available.setVisibility(View.INVISIBLE);
                                fromTextView.setVisibility(View.INVISIBLE);
                                startDate.setText("");
                                startDate.setVisibility(View.INVISIBLE);
                                calendarButton.setVisibility(View.INVISIBLE);
                                toTextView.setVisibility(View.INVISIBLE);
                                startTime1.setText("");
                                startTime1.setVisibility(View.INVISIBLE);
                                timeButton.setVisibility(View.INVISIBLE);
                                howLong.setVisibility(View.INVISIBLE);
                                hourSpinner.setVisibility(View.INVISIBLE);
                                minSpinner.setVisibility(View.INVISIBLE);
                                calcfare.setVisibility(View.INVISIBLE);
                                totalCost.setVisibility(View.INVISIBLE);
                                cost.setVisibility(View.INVISIBLE);
                                payButton.setVisibility(View.INVISIBLE);
                                }
                            }
                            break;
            case R.id.hourSpinner:
                                    //String timestamp=startTime1.getText().toString();
                                    totalCost.setVisibility((View.INVISIBLE));
                                    cost.setVisibility(View.INVISIBLE);
                                    payButton.setVisibility(View.INVISIBLE);
                                    if (++countHrsSpinner > 1) {
                                        if (position != 0) {
                                            int item=Integer.parseInt((String)parent.getItemAtPosition(position));
                                            if (hrsSet + item == 23 && minSet >= 30) {
                                                Toast.makeText(getActivity(), "Reached Here", Toast.LENGTH_SHORT).show();
                                                minrem.remove("30");
                                                ArrayAdapter<String> minAdapter;
                                                minAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, minrem);
                                                minAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                minSpinner.setAdapter(minAdapter);
                                            }
                                            else{

                                                if(!minrem.contains("30"))

                                                    minrem.add("30");
                                            }
                                        }
                                        else{
                                            Toast.makeText(getActivity(),"Please Select Duration of Park",Toast.LENGTH_LONG).show();

                                        }
                                    }
                                    break;

            case R.id.minuteSpinner:totalCost.setVisibility((View.INVISIBLE));
                                    cost.setVisibility(View.INVISIBLE);
                                    payButton.setVisibility(View.INVISIBLE);
                                    if (++countMinSpinner > 1) {
                                        if (position == 0) {
                                            Toast.makeText(getActivity(), "Please Select Duration of Park", Toast.LENGTH_LONG).show();

                                        }
                                    }
                            }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(), "Please Select the Place", Toast.LENGTH_LONG).show();
    }



}