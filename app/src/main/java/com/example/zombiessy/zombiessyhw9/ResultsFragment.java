package com.example.zombiessy.zombiessyhw9;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static List<Results> results;
    private double[] geolocation;
    private List<String> nextPageTokens;
    private TextView err;
    private Integer page = 1;
    private Button prev_btn;
    private Button next_btn;
    private LinearLayout page_btn;
    private ProgressDialog progressDialog;


    public ResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching results");
        progressDialog.setCancelable(true);
        progressDialog.show();

        err = (TextView) view.findViewById(R.id.resultsErrorMessage);
        recyclerView = (RecyclerView) view.findViewById(R.id.result_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        results = new ArrayList<Results>();
        geolocation = new double[2];
        nextPageTokens = new ArrayList<String>();
        prev_btn = (Button) view.findViewById(R.id.previous_btn);
        next_btn = (Button) view.findViewById(R.id.next_btn);
        page_btn = (LinearLayout) view.findViewById(R.id.page_btn);
        prev_btn.setEnabled(false);
        next_btn.setEnabled(false);
        prev_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);

        String inputLoc = getActivity().getIntent().getExtras().getString("from");
        if (inputLoc.equals("here")) {
            requestForHere();
        } else {
            requestForOther(inputLoc);
        }

        return view;
    }


    private void requestForHere() {
        geolocation[0] = getActivity().getIntent().getExtras().getDouble("currLat");
        geolocation[1] = getActivity().getIntent().getExtras().getDouble("currLng");
        requestForFirstPage();
    }

    // get other lat and lng
    private void requestForOther(String inputLoc) {
        String input = inputLoc.replaceAll(", ", " ");
        String queryURL = BackendURLs.INPUTLOCATION + "?inputLoc=" + Uri.encode(input);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, queryURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    if (jsonObj.getInt("error") == 0) {
                        JSONArray data = jsonObj.getJSONArray("data");
                        geolocation[0] = data.getDouble(0);
                        geolocation[1] = data.getDouble(1);
                        requestForFirstPage();
                    } else {
                        err.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    err.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                err.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(stringRequest, "Other");
    }

    // get first page results
    private void requestForFirstPage() {
        String keyword = getActivity().getIntent().getExtras().getString("keyword");

        String type = getActivity().getIntent().getExtras().getString("type");
        String distance = getActivity().getIntent().getExtras().getString("distance");
        Double radius = 1609.344 * 10;
        if (!distance.equals("")) {
            radius = (Double) 1609.344 * Integer.parseInt(distance);
        }

        String firstURL = BackendURLs.FIRSTPAGE + "?lat=" + geolocation[0] + "&lng=" + geolocation[1] + "&radius=" + radius
                + "&type=" + Uri.encode(type) + "&keyword=" + Uri.encode(keyword);
        page = 1;
        Log.d("url", firstURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, firstURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    if (jsonObj.getInt("error") == 0) {
                        JSONArray res = jsonObj.getJSONArray("data");
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject curr = res.getJSONObject(i);
                            String currIcon = curr.getString("icon");
                            String currName = curr.getString("name");
                            String currAddress = curr.getString("address");
                            String currPlaceID = curr.getString("place_id");
                            Double currLat = curr.getDouble("lat");
                            Double currLng = curr.getDouble("lng");
                            Results temp = new Results(currIcon, currName, currAddress, currPlaceID, currLat, currLng);
                            results.add(temp);
                        }
                        setUpResults(results);
                        progressDialog.dismiss();
                        page_btn.setVisibility(View.VISIBLE);

                        if (jsonObj.has("nextPageToken")) {
                            nextPageTokens.add(jsonObj.getString("nextPageToken"));
                            next_btn.setEnabled(true);
                            next_btn.setTextColor(Color.BLACK);
                        }
                    } else {
                        //setUpResults(results);
                        err.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    setUpResults(new ArrayList<Results>());
                    err.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setUpResults(new ArrayList<Results>());
                err.setVisibility(View.VISIBLE);
            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(stringRequest, "results");
    }

    private void requestForNextPage() {
        String nextURL = BackendURLs.NEXTPAGE + "?nextPageToken=";
        next_btn.setEnabled(false);
        if (page == 2) {
            nextURL += nextPageTokens.get(0);
        } else if (page == 3) {
            nextURL += nextPageTokens.get(1);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, nextURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    if (jsonObj.getInt("error") == 0) {
                        JSONArray res = jsonObj.getJSONArray("data");
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject curr = res.getJSONObject(i);
                            String currIcon = curr.getString("icon");
                            String currName = curr.getString("name");
                            String currAddress = curr.getString("address");
                            String currPlaceID = curr.getString("place_id");
                            Double currLat = curr.getDouble("lat");
                            Double currLng = curr.getDouble("lng");
                            Results temp = new Results(currIcon, currName, currAddress, currPlaceID, currLat, currLng);
                            results.add(temp);
                        }

                        if (page == 2) {
                            err.setVisibility(View.GONE);
                            setUpResults(results.subList(20, results.size()));
                        } else if (page == 3) {
                            err.setVisibility(View.GONE);
                            setUpResults(results.subList(40, results.size()));
                        }

                        if (jsonObj.has("nextPageToken")) {
                            nextPageTokens.add(jsonObj.getString("nextPageToken"));
                            next_btn.setEnabled(true);
                            next_btn.setTextColor(Color.BLACK);
                        }
                        progressDialog.dismiss();
                    } else {
                        page -= 1;
                        if (page == 1) {
                            setUpResults(results.subList(0, 20));
                        } else if (page == 2) {
                            setUpResults(results.subList(20, 40));
                        }
                        //err.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Network errors. Please try again.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    setUpResults(new ArrayList<Results>());
                    err.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setUpResults(new ArrayList<Results>());
                err.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(stringRequest, "results");
    }

    private void setUpResults(List<Results> subResult) {
        if (page == 1) {
            prev_btn.setEnabled(false);
            prev_btn.setTextColor(Color.GRAY);
            if (nextPageTokens.size() > 0) {
                next_btn.setEnabled(true);
                next_btn.setTextColor(Color.BLACK);
            } else {
                next_btn.setEnabled(false);
                next_btn.setTextColor(Color.GRAY);
            }
        } else if (page == 2) {
            prev_btn.setEnabled(true);
            prev_btn.setTextColor(Color.BLACK);
            if (nextPageTokens.size() > 1) {
                next_btn.setEnabled(true);
                next_btn.setTextColor(Color.BLACK);
            } else {
                next_btn.setEnabled(false);
                next_btn.setTextColor(Color.GRAY);
            }
        } else {
            prev_btn.setEnabled(true);
            prev_btn.setTextColor(Color.BLACK);
            next_btn.setEnabled(false);
            next_btn.setTextColor(Color.GRAY);
        }

        mAdapter = new ResultsAdapter(subResult, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.previous_btn: {
                page -= 1;
                if (page == 1) {
                    setUpResults(results.subList(0, 20));
                } else if (page == 2) {
                    setUpResults(results.subList(20, 40));
                }
                break;
            }
            case R.id.next_btn: {
                progressDialog.setMessage("Fetching next page");
                progressDialog.setCancelable(true);
                page += 1;
                if (page == 2) {
                    if (results.size() > 20 && results.size() <= 40) {
                        setUpResults(results.subList(20, results.size()));
                    } else if (results.size() > 40) {
                        setUpResults(results.subList(20, 40));
                    } else {
                        progressDialog.show();
                        requestForNextPage();
                    }
                } else if (page == 3) {
                    if (results.size() > 40) {
                        setUpResults(results.subList(40, results.size()));
                    } else {
                        progressDialog.show();
                        requestForNextPage();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (page == 1) {
            if (results.size() >= 20) {
                setUpResults(results.subList(0, 20));
            } else {
                setUpResults(results.subList(0, results.size()));
            }
        } else if (page == 2) {
            if (results.size() >= 40) {
                setUpResults(results.subList(20, 40));
            } else {
                setUpResults(results.subList(20, results.size()));
            }
        } else {
            setUpResults(results.subList(40, results.size()));
        }
    }

}
