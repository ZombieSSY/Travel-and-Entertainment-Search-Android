package com.example.zombiessy.zombiessyhw9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Results> favorites;
    private Context context;
    private static RefreshFavorites refresh;

    public FavoritesAdapter (List<Results> favorites, final Context context, RefreshFavorites refresh){
        this.favorites = favorites;
        this.context = context;
        this.refresh = refresh;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.favorite_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Results favo = favorites.get(position);
        holder.favo_name.setText(favo.getName());
        holder.favo_address.setText(favo.getAddress());

        if(favo.getIcon() != null){
            final String imageUrl = favo.getIcon();
            holder.favo_icon.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return getBitmapFromURL(imageUrl);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.favo_icon.setImageBitmap(bitmap);
                }
            }.execute();
        } else{
            holder.favo_icon.setVisibility(View.GONE);
        }

        final String favoKey = favo.getPlace_id();

        holder.favo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastText = favo.getName() + " was removed from favorites";
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                removeFavorite(favoKey);
                refresh.refreshFavorites();
            }
        });

        holder.favo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("place_id", favo.getPlace_id());
                bundle.putString("placeName", favo.getName());
                bundle.putDouble("latitude", favo.getLat());
                bundle.putDouble("longitude", favo.getLng());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView favo_name;
        public TextView favo_address;
        public ImageView favo_icon;
        public View favo_layout;
        public ImageView favo_btn;

        public ViewHolder(View v) {
            super(v);
            favo_layout = v;
            favo_name = (TextView) v.findViewById(R.id.favo_item_name);
            favo_address = (TextView) v.findViewById(R.id.favo_item_address);
            favo_icon = (ImageView) v.findViewById(R.id.favo_item_icon);
            favo_btn = (ImageView) v.findViewById(R.id.remove_favo);
        }
    }

    public static Bitmap getBitmapFromURL(String imageUrl) {
        Bitmap bitmap = null;

        if (bitmap == null) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage().toString());
            }
        }

        return bitmap;
    }

    private void removeFavorite(String favoKey) {
        SharedPreferenceManager.getInstance(context.getApplicationContext()).removeFavourite(favoKey);
    }

    public interface RefreshFavorites {
        void refreshFavorites();
    }
}
