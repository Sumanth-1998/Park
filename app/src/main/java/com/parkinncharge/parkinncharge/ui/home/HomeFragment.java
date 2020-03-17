package com.parkinncharge.parkinncharge.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.errors.ApiException;
import com.parkinncharge.parkinncharge.R;
import com.parkinncharge.parkinncharge.ScannerActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    MapView mMapView;
    FirebaseFirestore db;
    Button reached_button;
    String marker_name;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> autoAdapter;
    ArrayList<String> autoArrayList ;//= new ArrayList<String>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        db= FirebaseFirestore.getInstance();
        reached_button=(Button) root.findViewById(R.id.reached_button);
        autoCompleteTextView=root.findViewById(R.id.autoCompleteTextView);
        autoArrayList = new ArrayList<String>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Button Pressed", Toast.LENGTH_SHORT).show();
                Log.d("Adress","Pressed");
                autoCompleteText();
            }
        });

        addmarkers();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView=(MapView)view.findViewById(R.id.mapView);
        if(mMapView!=null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        locationManager=(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener =new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //LatLng userLoc=new LatLng(location.getLatitude(),location.getLongitude());
               // mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location").snippet("Hi Hello").icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                reached_button.setVisibility(View.VISIBLE);
                marker_name=marker.getTitle();
                mMap.setPadding(0,0,0,150);
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                reached_button.setVisibility(View.INVISIBLE);
            }
        });
        reached_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanner_intent=new Intent(getActivity(), ScannerActivity.class);
                scanner_intent.putExtra("Title",marker_name);
                Toast.makeText(getActivity(), "Passed:"+marker_name, Toast.LENGTH_SHORT).show();
                startActivity(scanner_intent);
            }
        });






        if (Build.VERSION.SDK_INT<24)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }else{
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastknownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng userLoc=new LatLng(lastknownLocation.getLatitude(),lastknownLocation.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,15));
            }
        }
    }
    private void addmarkers()
    {
        //Log.d("Reached","here");
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Hello", document.getId() + " => " + document.getData());
                                String name=document.getId();
                                Double latitude_park =Double.parseDouble(document.getString("Latitude"));
                                Double longitude_park= Double.parseDouble(document.getString("Longitude"));
                                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_park,longitude_park)).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
                            }
                        } else {
                            Log.d("Hello", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void autoCompleteText()
    {
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(getActivity(), "Text Changed", Toast.LENGTH_SHORT).show();

                Toast.makeText(getActivity(), s+"", Toast.LENGTH_SHORT).show();
                // Create a new Places client instance.
                PlacesClient placesClient = Places.createClient(getActivity());
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                // Create a RectangularBounds object.
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(-33.880490, 151.184363), //dummy lat/lng
                        new LatLng(-33.858754, 151.229596));
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry("IN")//Nigeria
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();


                placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    StringBuilder mResult = new StringBuilder();
                    //Toast.makeText(getActivity(), "Reached Here", Toast.LENGTH_SHORT).show();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        mResult.append(" ").append(prediction.getFullText(null) + "\n");
                        //Log.i("Hello", prediction.getPlaceId());
                        //Log.i("Hello", prediction.getPrimaryText(null).toString());
                        //Toast.makeText(getActivity(), prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
                        autoArrayList.add(prediction.getFullText(null).toString());

                    }
                    Log.d("List:",""+autoArrayList.toString());
                    autoAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.select_dialog_item,autoArrayList);
                    autoCompleteTextView.setAdapter(autoAdapter);
                    // mSearchResult.setText(String.valueOf(mResult));
                }).addOnFailureListener((exception) -> {
                    Log.e("Hello", "Place not found: ");

                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("Hello1", "Place not found: " + apiException.getMessage());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}