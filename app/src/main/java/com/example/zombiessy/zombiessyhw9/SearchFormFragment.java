package com.example.zombiessy.zombiessyhw9;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import static com.example.zombiessy.zombiessyhw9.AppController.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFormFragment extends Fragment implements View.OnClickListener {

    private EditText keywordTextView;
    private Spinner categorySpinner;
    private EditText distanceTextView;
    private RadioButton here;
    private RadioButton other;
    private AutoCompleteTextView inputLocTextView;
    private AutoCompleteAdapter mAdapter;
    protected GeoDataClient mGeoDataClient;
    private TextView keywordErr;
    private TextView locErr;
    private Button searchButton;
    private Button clearButton;

    private static final int ACCESS_TO_GPS = 200;
    private LocationManager locationManager;
    private String locationProvider;
    private double currLat;
    private double currLng;


    public SearchFormFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search_form, container, false);

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d("Location", "ACCESS-FINE-LOC: " + permissionCheck+" (0 is good, -1 is bad)");

        if(permissionCheck == 0) {
            getHereLocation();
        } else {  // ask for permission
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_TO_GPS);
                    }
                }
            }
        }

        keywordTextView = (EditText) view.findViewById(R.id.keyword);
        categorySpinner = (Spinner) view.findViewById(R.id.category);
        distanceTextView = (EditText) view.findViewById(R.id.distance);

        here = (RadioButton) view.findViewById(R.id.here);
        other = (RadioButton) view.findViewById(R.id.other);
        here.setOnClickListener(this);
        other.setOnClickListener(this);

        inputLocTextView = (AutoCompleteTextView) view.findViewById(R.id.inputLoc);
        inputLocTextView.setEnabled(false);
        inputLocTextView.setOnItemClickListener(mAutocompleteClickListener);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mAdapter = new AutoCompleteAdapter(getActivity(), mGeoDataClient, BackendURLs.BOUNDS, null);
        inputLocTextView.setAdapter(mAdapter);

        keywordErr = (TextView) view.findViewById(R.id.keywordErr);
        keywordErr.setVisibility(View.GONE);
        locErr = (TextView) view.findViewById(R.id.locErr);
        locErr.setVisibility(View.GONE);

        searchButton = (Button) view.findViewById(R.id.search_btn);
        clearButton = (Button) view.findViewById(R.id.clear_btn);
        searchButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.here: {
                inputLocTextView.setEnabled(false);
                inputLocTextView.getText().clear();
                locErr.setVisibility(View.GONE);
                break;
            }
            case R.id.other: {
                inputLocTextView.setEnabled(true);
                //inputLocTextView.setFocusable(true);
                break;
            }
            case R.id.search_btn: {
                String keyword = keywordTextView.getText().toString();
                String inputLoc = inputLocTextView.getText().toString();
                String type = categorySpinner.getSelectedItem().toString();
                Log.d("type", type);
                String distance = distanceTextView.getText().toString();

                if (keyword.trim().matches("^[a-zA-z\\s;'/.,]+$")) {
                    keywordErr.setVisibility(View.GONE);
                } else {
                    keywordErr.setVisibility(View.VISIBLE);
                }

                if (other.isChecked()) {
                    if (inputLoc.trim().matches("^[a-zA-z\\s,]+$")) {
                        locErr.setVisibility(View.GONE);
                    } else {
                        locErr.setVisibility(View.VISIBLE);
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString("keyword", keyword);
                bundle.putString("distance", distance);
                bundle.putString("type", type);
                if (here.isChecked()) {
                    bundle.putString("from", "here");
                } else {
                    bundle.putString("from", inputLoc.trim());
                }

                bundle.putDouble("currLat", currLat);
                bundle.putDouble("currLng", currLng);

                if ((keywordErr.getVisibility() == View.GONE) && (locErr.getVisibility() == View.GONE)) {
                    Intent myIntent = new Intent(getActivity(), ResultsActivity.class);
                    myIntent.putExtras(bundle);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.clear_btn: {
                keywordTextView.getText().clear();
                //keywordTextView.setFocusable(true);
                categorySpinner.setSelection(0);
                distanceTextView.getText().clear();
                here.setChecked(true);
                other.setChecked(false);
                inputLocTextView.getText().clear();
                inputLocTextView.setEnabled(false);
                //inputLocTextView.setFocusable(false);
                inputLocTextView.clearFocus();
                keywordErr.setVisibility(View.GONE);
                locErr.setVisibility(View.GONE);
                break;
            }
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final CharSequence primaryText = item.getPrimaryText(null);
            Log.i(TAG, "Autocomplete item selected: " + primaryText);
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_TO_GPS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getHereLocation();
                } else {
                    Toast.makeText(getActivity(), "Cannot get access to GPS!", Toast.LENGTH_SHORT);
                    Log.d("Location-ERROR: ","Access to GPS is denied!!!");
                }
                break;
            }
        }
    }

    // get here lat and lng
    @SuppressLint("MissingPermission")
    private void getHereLocation() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.d("Location", "GPS");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.d("Location", "Network");
        } else {
            Log.d("Location", "NO Network");
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLat = location.getLatitude();
                currLng = location.getLongitude();
                Log.v("Location","Latitude: "+location.getLatitude());
                Log.v("Location", "Longitude: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }

            @Override
            public void onProviderEnabled(String s) { }

            @Override
            public void onProviderDisabled(String s) { }
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            Log.d("Location-ERROR: ","GPS is disabled!");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        keywordTextView.getText().clear();
        categorySpinner.setSelection(0);
        distanceTextView.getText().clear();
        here.setChecked(true);
        other.setChecked(false);
        inputLocTextView.getText().clear();
        inputLocTextView.setEnabled(false);
        inputLocTextView.clearFocus();
        keywordErr.setVisibility(View.GONE);
        locErr.setVisibility(View.GONE);
    }
}
