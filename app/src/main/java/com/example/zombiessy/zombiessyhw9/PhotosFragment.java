package com.example.zombiessy.zombiessyhw9;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private List<PlacePhotoMetadata> photos;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recycleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String placeId;
    private TextView err;



    public PhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photos, container, false);

        mGeoDataClient = Places.getGeoDataClient(getContext(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);
        photos = new ArrayList<PlacePhotoMetadata>();
        err = (TextView) v.findViewById(R.id.photos_err);
        placeId = getActivity().getIntent().getExtras().getString("place_id");

        recyclerView = (RecyclerView) v.findViewById(R.id.photos_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        getPhotos();

        return v;
    }

    private void getPhotos() {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photosList = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photosList.getPhotoMetadata();
                for (PlacePhotoMetadata photo : photoMetadataBuffer) {
                    photos.add(photo.freeze());
                }
                photoMetadataBuffer.release();
                setUpPhotos();
            }
        });
    }

    private void setUpPhotos(){
        if (photos.size() == 0) {
            err.setVisibility(View.VISIBLE);
        } else {
            recycleAdapter = new PhotosAdapter(photos, getContext());
            recyclerView.setAdapter(recycleAdapter);
        }
    }
}
