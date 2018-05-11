package com.example.zombiessy.zombiessyhw9;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView priceTextView;
    private RatingBar ratingTextView;
    private TextView googlePageTextView;
    private TextView websiteView;
    private TextView err;
    private LinearLayout addressLayout;
    private LinearLayout phoneLayout;
    private LinearLayout priceLayout;
    private LinearLayout ratingLayout;
    private LinearLayout googlePageLayout;
    private LinearLayout websiteLayout;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        addressTextView = (TextView) v.findViewById(R.id.info_add);
        phoneTextView = (TextView) v.findViewById(R.id.info_phone);
        priceTextView = (TextView) v.findViewById(R.id.info_price);
        ratingTextView = (RatingBar) v.findViewById(R.id.info_rate);
        googlePageTextView = (TextView) v.findViewById(R.id.info_google);
        websiteView = (TextView) v.findViewById(R.id.info_webpage);
        err = (TextView) v.findViewById(R.id.info_err);
        addressLayout = (LinearLayout) v.findViewById(R.id.add_title);
        phoneLayout = (LinearLayout) v.findViewById(R.id.phone_title);
        priceLayout = (LinearLayout) v.findViewById(R.id.price_title);
        ratingLayout = (LinearLayout) v.findViewById(R.id.rating_title);
        googlePageLayout = (LinearLayout) v.findViewById(R.id.google_title);
        websiteLayout = (LinearLayout) v.findViewById(R.id.web_title);

        try {
            setUpInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private void setUpInfo () throws JSONException {
        JSONObject placeDetails = DetailsActivity.placeDetails;

        String address = placeDetails.getString("address");
        String phone = placeDetails.getString("phone");
        Integer price = placeDetails.getInt("price");
        double rating = placeDetails.getDouble("rating");
        String url = placeDetails.getString("url");
        String website = placeDetails.getString("website");

        if (address.equals("") && phone.equals("") && price == -1 && rating == (-0.01) && url.equals("") && website.equals("")) {
            err.setVisibility(View.VISIBLE);
        } else {
            if (!address.equals("")) {
                addressLayout.setVisibility(View.VISIBLE);
                addressTextView.setText(address);
            }
            if (!phone.equals("")) {
                phoneLayout.setVisibility(View.VISIBLE);
                phoneTextView.setText(phone);
            }
            if (price != -1) {
                String level = "";
                for (int i = 1; i <= price; i++) {
                    level += "$";
                }
                priceLayout.setVisibility(View.VISIBLE);
                priceTextView.setText(level);
            }
            if (rating != (-0.01)) {
                ratingLayout.setVisibility(View.VISIBLE);
                ratingTextView.setRating((float) rating);
            }
            if (!url.equals("")) {
                googlePageLayout.setVisibility(View.VISIBLE);
                googlePageTextView.setText(url);
            }
            if (!website.equals("")) {
                websiteLayout.setVisibility(View.VISIBLE);
                websiteView.setText(website);
            }
        }
    }

}
