package com.example.zombiessy.zombiessyhw9;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private List<PlacePhotoMetadata> photos;
    private Context context;
    private GeoDataClient mGeoDataClient;



    public PhotosAdapter(List<PlacePhotoMetadata> res, final Context context){
        photos = res;
        this.context = context;
        mGeoDataClient = Places.getGeoDataClient(context, null);
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public ImageView imgView;
        public View layout;
        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            imgView = (ImageView) itemView.findViewById(R.id.photo_item);
        }
    }

    @Override
    public  PhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.photo_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(photos.get(position) != null){
            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photos.get(position));
            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                    PlacePhotoResponse photo = task.getResult();
                    Bitmap bitmap = photo.getBitmap();
                    holder.imgView.setMinimumWidth(bitmap.getWidth());
                    holder.imgView.setMinimumHeight(bitmap.getHeight());
                    holder.imgView.setVisibility(View.VISIBLE);
                    holder.imgView.setImageBitmap(bitmap);
                }
            });
        } else{
            holder.imgView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}
