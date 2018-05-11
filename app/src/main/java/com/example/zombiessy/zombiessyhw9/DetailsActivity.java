package com.example.zombiessy.zombiessyhw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager detailViewPager;
    private TextView err;

    private String place_id;
    private String detailTitle;
    private String placeIcon;
    private String placeAddress;
    private double placeLat;
    private double placeLng;

    private ProgressDialog progressDialog;
    private ImageView share_btn;
    private ImageView favo_btn;

    private String[] tabTitles = {"INFO", "PHOTOS", "MAP", "REVIEWS"};
    private Integer[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.photos,
            R.drawable.maps,
            R.drawable.review
    };

    public static JSONObject placeDetails;
    public static List<Reviews> googleReviewsList;
    public static List<Reviews> yelpReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();

        place_id = intent.getExtras().getString("place_id");
        detailTitle = intent.getExtras().getString("placeName");
        placeIcon = intent.getExtras().getString("placeIcon");
        placeAddress = intent.getExtras().getString("placeAdd");
        placeLat = intent.getExtras().getDouble("latitude");
        placeLng = intent.getExtras().getDouble("longitude");

        err = (TextView) findViewById(R.id.details_err);
        share_btn = (ImageView) findViewById(R.id.share_btn);
        favo_btn = (ImageView) findViewById(R.id.favo_btn);
        if (inFavorites(place_id)) {
            favo_btn.setImageResource(R.drawable.heart_fill_white);
        } else {
            favo_btn.setImageResource(R.drawable.heart_outline_white);
        }
        share_btn.setOnClickListener(this);
        favo_btn.setOnClickListener(this);

        placeDetails = new JSONObject();
        googleReviewsList = new ArrayList<Reviews>();
        yelpReviewsList = new ArrayList<Reviews>();
        requestDetail();

        toolbar = (Toolbar) findViewById(R.id.detailTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(detailTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching details");
        progressDialog.setCancelable(true);
        progressDialog.show();

//        ViewPagerAdapter pagerView = new ViewPagerAdapter(getSupportFragmentManager());
//        detailViewPager = (ViewPager) findViewById(R.id.details_fragment);
//        detailViewPager.setAdapter(pagerView);

        tabLayout = (TabLayout) findViewById(R.id.details_tabs);
        tabLayout.setVisibility(View.GONE);
//
//        detailViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(detailViewPager));
    }

    private void requestDetail() {
        String detailsURL = BackendURLs.PLACEDETAIL + "?placeId=" + place_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, detailsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    if (jsonObj.getInt("error") == 0) {
                        JSONArray data = jsonObj.getJSONArray("data");
                        placeDetails = (JSONObject) data.get(0);
                        try {
                            getGoogleReviews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        err.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    err.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                err.setVisibility(View.VISIBLE);
            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(stringRequest, "Info");
    }

    private void getGoogleReviews() throws JSONException {
        JSONArray reviewsList = placeDetails.getJSONArray("reviews");
        for (int i = 0; i < reviewsList.length(); i++) {
            JSONObject curr = reviewsList.getJSONObject(i);
            String currAuthor = "";
            String currAuthorURL = "";
            String currProfile = "";
            double currRating = -0.1;
            Long currTime = 0L;
            String currText = "";
            if (curr.has("author_name")) {
                currAuthor = curr.getString("author_name");
            }
            if (curr.has("author_url")) {
                currAuthorURL = curr.getString("author_url");
            }
            if (curr.has("profile_photo_url")) {
                currProfile = curr.getString("profile_photo_url");
            }
            if (curr.has("rating")) {
                currRating = curr.getDouble("rating");
            }
            if (curr.has("time")) {
                currTime = curr.getLong("time");
            }
            if (curr.has("text")) {
                currText = curr.getString("text");
            }
            Reviews temp = new Reviews(currAuthor, currAuthorURL, currProfile, currRating, currTime, currText);
            googleReviewsList.add(temp);
        }
        getYelpID();
    }

    private void getYelpID() throws JSONException {
        String format_add = placeDetails.getString("address");
        String[] add_compo = format_add.split(",");
        String address1 = add_compo[0];
        String address2 = add_compo[1] + "," + add_compo[2];
        String city = placeDetails.getString("city");
        String state = placeDetails.getString("state");
        String country = placeDetails.getString("country");
        String zipCode = placeDetails.getString("zipCode");

        String yelpMatchURL = BackendURLs.YELPMATCH + "?name=" + Uri.encode(detailTitle.trim()) + "&address1=" + Uri.encode(address1.trim()) + "&address2=" + Uri.encode(address2.trim()) + "&city=" + Uri.encode(city.trim()) + "&state=" + state + "&country=" + Uri.encode(country) + "&zipCode=" + zipCode;
        Log.d("url", yelpMatchURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, yelpMatchURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    if (jsonObj.getInt("error") == 0) {
                        String yelpID = jsonObj.getString("data");
                        Log.d("yelp_id", yelpID);
                        requestYelpReviews(yelpID);
                    } else {
                        yelpReviewsList = new ArrayList<>();
                        createTabs();
                    }
                } catch (JSONException e) {
                    yelpReviewsList = new ArrayList<>();
                    e.printStackTrace();
                    createTabs();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                yelpReviewsList = new ArrayList<>();
                error.printStackTrace();
            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(stringRequest, "yelpID");
    }

    private void requestYelpReviews(String yelp_id) {
        String yelpReviewsURL = BackendURLs.YELPREVIEW + "?businessID=" + yelp_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, yelpReviewsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    if (jsonObj.getInt("error") == 0) {
                        Log.d("yelp_reviews", response);
                        JSONArray data = jsonObj.getJSONArray("data");
                        if (data.length() > 0) {
                            try {
                                getYelpReviews(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            yelpReviewsList = new ArrayList<>();
                            createTabs();
                        }
                    } else {
                        yelpReviewsList = new ArrayList<>();
                        createTabs();
                    }
                } catch (JSONException e) {
                    yelpReviewsList = new ArrayList<>();
                    e.printStackTrace();
                    createTabs();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                yelpReviewsList = new ArrayList<>();
                error.printStackTrace();
            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(stringRequest, "yelpReviews");
    }

    private void getYelpReviews(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject curr = data.getJSONObject(i);
            JSONObject user = curr.getJSONObject("user");
            String currAuthor = user.getString("name");
            String currProfile = user.getString("image_url");
            String currAuthorURL = curr.getString("url");
            double currRating = curr.getDouble("rating");
            String currText = curr.getString("text");

            String currYelpTime = curr.getString("time_created");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long currTime = null;
            try {
                Date date = formatter.parse(currYelpTime);
                currTime = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                createTabs();
            }
            Reviews temp = new Reviews(currAuthor, currAuthorURL, currProfile, currRating, currTime, currYelpTime, currText);
            yelpReviewsList.add(temp);
        }
        createTabs();
    }

    private void createTabs() {
        progressDialog.dismiss();

        ViewPagerAdapter pagerView = new ViewPagerAdapter(getSupportFragmentManager());
        detailViewPager = (ViewPager) findViewById(R.id.details_fragment);
        detailViewPager.setAdapter(pagerView);

        for (int i = 0; i < tabLayout.getTabCount(); i++ ) {
            LinearLayout currTab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tab_text = (TextView) currTab.findViewById(R.id.tabContent);
            tab_text.setText("  " + tabTitles[i]);
            tab_text.setCompoundDrawablesWithIntrinsicBounds(tabIcons[i], 0, 0, 0);
            tabLayout.getTabAt(i).setCustomView(tab_text);
        }
        tabLayout.setVisibility(View.VISIBLE);

        detailViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(detailViewPager));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_btn: {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                String twit_url = "";
                String twitterMessage = "Check out " + detailTitle;
                try {
                    twitterMessage += " located at " + placeDetails.getString("address");
                    twit_url = placeDetails.getString("website");
                    if (twit_url.equals("")) {
                        twit_url = placeDetails.getString("url");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                twitterMessage += ". Website: ";
                String twitterURL = BackendURLs.TWITTER + "?text=" + Uri.encode(twitterMessage) + "&url=" + Uri.encode(twit_url) + "&hashtags=TravelAndEntertainmentSearch";
                intent.setData(Uri.parse(twitterURL));
                startActivity(intent);
                break;
            }
            case R.id.favo_btn: {
                if (inFavorites(place_id)) {
                    favo_btn.setImageResource(R.drawable.heart_outline_white);
                    String toastText = detailTitle + " was removed from favorites";
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                    removeFavorite(place_id);
                } else {
                    favo_btn.setImageResource(R.drawable.heart_fill_white);
                    String toastText = detailTitle + " was added to favorites";
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    Results result = new Results(placeIcon, detailTitle, placeAddress, place_id, placeLat, placeLng);
                    String favoJson = gson.toJson(result);
                    addFavorite(place_id, favoJson);
                }
                break;
            }
        }
    }

    private boolean inFavorites(String favoKey) {
        return SharedPreferenceManager.getInstance(getApplicationContext()).isFavourite(favoKey);
    }

    private void removeFavorite(String favoKey) {
        SharedPreferenceManager.getInstance(getApplicationContext()).removeFavourite(favoKey);
    }

    private void addFavorite(String favoKey, String favoJson) {
        SharedPreferenceManager.getInstance(getApplicationContext()).setFavourite(favoKey, favoJson);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private android.support.v4.app.Fragment infoFragment;
        private android.support.v4.app.Fragment photosFragment;
        private android.support.v4.app.Fragment mapFragment;
        private android.support.v4.app.Fragment reviewsFragment;

        public ViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    if (infoFragment == null) {
                        infoFragment = new InfoFragment();
                    }
                    return infoFragment;
                case 1:
                    if (photosFragment == null) {
                        photosFragment = new PhotosFragment();
                    }
                    return photosFragment;
                case 2:
                    if (mapFragment == null) {
                        mapFragment = new MapFragment();
                    }
                    return mapFragment;
                case 3:
                    if (reviewsFragment == null) {
                        reviewsFragment = new ReviewsFragment();
                    }
                    return reviewsFragment;
            }
            return null;
        }


        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
