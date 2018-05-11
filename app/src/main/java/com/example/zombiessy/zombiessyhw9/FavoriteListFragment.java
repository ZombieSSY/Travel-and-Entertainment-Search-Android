package com.example.zombiessy.zombiessyhw9;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteListFragment extends Fragment implements FavoritesAdapter.RefreshFavorites {
    private TextView err;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Results> favoritesList;

    public FavoriteListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favorite_list, container, false);

        err = (TextView) v.findViewById(R.id.favoriteErrorMessage);
        recyclerView = (RecyclerView) v.findViewById(R.id.favorite_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        favoritesList = new ArrayList<>();

        getFavorites();

        return v;
    }

    private void getFavorites() {
        Map<String,?> favorites = SharedPreferenceManager.getInstance(getContext().getApplicationContext()).getAll();
        if(favorites.isEmpty() || favorites.size() < 1){
            err.setVisibility(View.VISIBLE);
        } else{
            err.setVisibility(View.GONE);
            for (Map.Entry<String,?> entry : favorites.entrySet()){
                if (entry.getValue() instanceof String){
                    String value = (String) entry.getValue();
                    Gson gson = new Gson();
                    Results res = gson.fromJson(value, Results.class);
                    favoritesList.add(res);
                }
            }
        }
        setUpFavoriteList();
    }

    private void setUpFavoriteList() {
        mAdapter = new FavoritesAdapter(favoritesList, getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        favoritesList = new ArrayList<>();
        getFavorites();
    }

    @Override
    public void refreshFavorites() {
        favoritesList = new ArrayList<>();
        getFavorites();
    }
}
