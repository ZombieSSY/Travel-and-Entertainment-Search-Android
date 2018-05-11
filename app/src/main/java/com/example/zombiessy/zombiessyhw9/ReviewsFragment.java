package com.example.zombiessy.zombiessyhw9;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String GOOGLE = "Google reviews";
    private static final String YELP = "Yelp reviews";
    private static final String DEFAULT_ORDER = "Default order";
    private static final String HIGHEST_RATING = "Highest rating";
    private static final String LOWEST_RATING = "Lowest rating";
    private static final String MOST_RECENT = "Most recent";
    private static final String LEAST_RECENT = "Least recent";

    private List<Reviews> googleReviews;
    private List<Reviews> yelpReviews;
    private List<Reviews> defaultGoogle;
    private List<Reviews> defaultYelp;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recycleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView err;
    private String reviewType;
    private String sortOrder;
    private Spinner typeChoice;
    private Spinner orderChoice;


    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reviews, container, false);

        googleReviews = DetailsActivity.googleReviewsList;
        yelpReviews = DetailsActivity.yelpReviewsList;
        defaultGoogle = new ArrayList<>(DetailsActivity.googleReviewsList);
        defaultYelp = new ArrayList<>(DetailsActivity.yelpReviewsList);

        err = (TextView) v.findViewById(R.id.reviews_err);
        reviewType = GOOGLE;
        sortOrder = DEFAULT_ORDER;

        typeChoice = (Spinner) v.findViewById(R.id.reviews_choice);
        orderChoice = (Spinner) v.findViewById(R.id.sort_choice);
        typeChoice.setOnItemSelectedListener(this);
        orderChoice.setOnItemSelectedListener(this);

        recyclerView = (RecyclerView) v.findViewById(R.id.reviews_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    private void setUpReviews(List<Reviews> finalReviews) {
        recycleAdapter = new ReviewsAdapter(finalReviews, getContext());
        recyclerView.setAdapter(recycleAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == typeChoice) {
            reviewType = parent.getItemAtPosition(position).toString();
        } else if (parent == orderChoice) {
            sortOrder = parent.getItemAtPosition(position).toString();
        }

        switch (reviewType) {
            case GOOGLE: {
                if (defaultGoogle.size() == 0) {
                    err.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    err.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    changeOrder(googleReviews);
                }
                break;
            }
            case YELP: {
                if (defaultYelp.size() == 0) {
                    err.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    err.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    changeOrder(yelpReviews);
                }
                break;
            }
        }
    }

    private void changeOrder(List<Reviews> reviews) {
        switch (sortOrder) {
            case DEFAULT_ORDER:
                if (reviewType.equals(GOOGLE)) {
                    setUpReviews(defaultGoogle);
                } else if (reviewType.equals(YELP)) {
                    setUpReviews(defaultYelp);
                }
                break;
            case HIGHEST_RATING:
                Collections.sort(reviews, new Comparator<Reviews>() {
                    @Override
                    public int compare(Reviews r1, Reviews r2) {
                        if (r1.getReviewsRating() == r2.getReviewsRating()) {
                            return 0;
                        }
                        return r1.getReviewsRating() > r2.getReviewsRating() ? -1 : 1;
                    }
                });
                setUpReviews(reviews);
                break;
            case LOWEST_RATING:
                Collections.sort(reviews, new Comparator<Reviews>() {
                    @Override
                    public int compare(Reviews r1, Reviews r2) {
                        if (r1.getReviewsRating() == r2.getReviewsRating()) {
                            return 0;
                        }
                        return r1.getReviewsRating() < r2.getReviewsRating() ? -1 : 1;
                    }
                });
                setUpReviews(reviews);
                break;
            case MOST_RECENT:
                Collections.sort(reviews, new Comparator<Reviews>() {
                    @Override
                    public int compare(Reviews r1, Reviews r2) {
                        if (r1.getReviewsTime().equals(r2.getReviewsTime())) {
                            return 0;
                        }
                        return r1.getReviewsTime() > r2.getReviewsTime() ? -1 : 1;
                    }
                });
                setUpReviews(reviews);
                break;
            case LEAST_RECENT:
                Collections.sort(reviews, new Comparator<Reviews>() {
                    @Override
                    public int compare(Reviews r1, Reviews r2) {
                        if (r1.getReviewsTime().equals(r2.getReviewsTime())) {
                            return 0;
                        }
                        return r1.getReviewsTime() < r2.getReviewsTime() ? -1 : 1;
                    }
                });
                setUpReviews(reviews);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
