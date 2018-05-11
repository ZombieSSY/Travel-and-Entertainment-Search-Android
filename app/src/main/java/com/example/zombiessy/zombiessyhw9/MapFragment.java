package com.example.zombiessy.zombiessyhw9;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.zombiessy.zombiessyhw9.AppController.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private double destLat;
    private double destLng;
    private String destPlaceId;
    private String destName;
    private double origLat;
    private double origLng;
    private String origPlaceId;
    private String origName;
    private MapView mMapView;
    private GoogleMap googleMap;
    protected GeoDataClient mGeoDataClient;
    private AutoCompleteAdapter mAdapter;
    private AutoCompleteTextView fromWhere;
    private Spinner modeChoice;
    private LatLngBounds bounds;
    private String travelMode;
    private LatLng destination;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        destLat = getActivity().getIntent().getExtras().getDouble("latitude");
        destLng = getActivity().getIntent().getExtras().getDouble("longitude");
        destPlaceId = getActivity().getIntent().getExtras().getString("place_id");
        destName = getActivity().getIntent().getExtras().getString("placeName");
        bounds = BackendURLs.BOUNDS;
        travelMode = "DRIVING";

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        fromWhere = (AutoCompleteTextView) v.findViewById(R.id.from_where);
//        fromWhere.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new AutoCompleteAdapter(getActivity(), mGeoDataClient, bounds, null);
        fromWhere.setAdapter(mAdapter);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        modeChoice = (Spinner) v.findViewById(R.id.mode_choice);
//        mMapView.onCreate(savedInstanceState);
//        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();// needed to get the map to display immediately
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For dropping a marker at a point on the Map
                    destination = new LatLng(destLat, destLng);
                    googleMap.addMarker(new MarkerOptions().position(destination).title(destName));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(destination).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    fromWhere.setOnItemClickListener(mAutocompleteClickListener);
                    modeChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            travelMode = modeChoice.getSelectedItem().toString();
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(destination).title(destName));
                            getRoutes(travelMode);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            });
        }


        return v;
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
            assert item != null;
            origPlaceId = item.getPlaceId();
            origName = item.getPrimaryText(null).toString();
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(destination).title(destName));
            getRoutes(travelMode);
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);
        }
    };

    private void getRoutes(String mode) {

        String routesURL = BackendURLs.ROUTESURL + "origin=place_id:" + origPlaceId + "&destination=place_id:" + destPlaceId + "&mode=" + mode.toLowerCase() + "&key=" + BackendURLs.GOOGLE_API_KEY;
        Log.d("routesURL", routesURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, routesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    Log.d("routes", response);
                    if (jsonObj.has("status") && jsonObj.getString("status").equals("OK")) {
                        if (jsonObj.has("routes") && jsonObj.getJSONArray("routes").length() > 0) {
                            JSONArray routes = jsonObj.getJSONArray("routes");
                            JSONObject element = routes.getJSONObject(0);
//                            JSONObject path = element.getJSONObject("overview_polyline");

                            if (element.has("bounds")) {
                                JSONObject bound = element.getJSONObject("bounds");
                                JSONObject northeast = bound.getJSONObject("northeast");
                                JSONObject southwest = bound.getJSONObject("southwest");
                                bounds = new LatLngBounds(new LatLng(southwest.getDouble("lat"), southwest.getDouble("lng")), new LatLng(northeast.getDouble("lat"), northeast.getDouble("lng")));
                            }
                            if (element.has("legs") && element.getJSONArray("legs").length() > 0) {
                                JSONArray legs = element.getJSONArray("legs");
                                for (int i = 0; i < legs.length(); i++) {
                                    JSONObject currLeg = legs.getJSONObject(i);
                                    if (currLeg.has("steps") && currLeg.getJSONArray("steps").length() > 0) {
                                        JSONArray steps = currLeg.getJSONArray("steps");
                                        for (int j = 0; j < steps.length(); j++) {
                                            JSONObject currStep = steps.getJSONObject(j);
                                            double startLat = currStep.getJSONObject("start_location").getDouble("lat");
                                            double startLng = currStep.getJSONObject("start_location").getDouble("lng");
                                            if (i == 0 && j == 0) {
                                                origLat = startLat;
                                                origLng = startLng;
                                            }
                                            JSONObject polyline = currStep.getJSONObject("polyline");
                                            drawPath(polyline);
                                        }
                                        //drawPath();
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(stringRequest, "Other");
    }

    private void drawPath(JSONObject polyline) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(origLat, origLng)).title(origName));
        int padding = 15; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);

        try{
            List<LatLng> decodedPath = PolyUtil.decode(polyline.getString("points"));
            PolylineOptions polyLineOptions = new PolylineOptions();
            polyLineOptions.addAll(decodedPath);
            polyLineOptions.width(15);
            polyLineOptions.color(Color.BLUE);
            googleMap.addPolyline(polyLineOptions);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
