package com.example.zombiessy.zombiessyhw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private List<Results> results;
    private Context context;

    public ResultsAdapter (List<Results> results, final Context context){
        this.results = results;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView address;
        public ImageView icon;
        public View layout;
        public ImageView addFavo;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            name = (TextView) v.findViewById(R.id.item_name);
            address = (TextView) v.findViewById(R.id.item_address);
            icon = (ImageView) v.findViewById(R.id.item_icon);
            addFavo = (ImageView) v.findViewById(R.id.add_favo);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Results res = results.get(position);
        holder.name.setText(res.getName());
        holder.address.setText(res.getAddress());

        if(res.getIcon() != null){
            final String imageUrl = res.getIcon();
            holder.icon.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return getBitmapFromURL(imageUrl);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.icon.setImageBitmap(bitmap);
                }
            }.execute();
        } else{
            holder.icon.setVisibility(View.GONE);
        }

        final String favoKey = res.getPlace_id();
        if (inFavorites(favoKey)) {
            holder.addFavo.setImageResource(R.drawable.heart_fill_red);
        } else {
            holder.addFavo.setImageResource(R.drawable.heart_outline_black);
        }

        holder.addFavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inFavorites(favoKey)) {
                    holder.addFavo.setImageResource(R.drawable.heart_outline_black);
                    String toastText = res.getName() + " was removed from favorites";
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    removeFavorite(favoKey);
                } else {
                    holder.addFavo.setImageResource(R.drawable.heart_fill_red);
                    String toastText = res.getName() + " was added to favorites";
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    String favoJson = gson.toJson(res);
                    addFavorite(favoKey, favoJson);
                }
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("place_id", res.getPlace_id());
                bundle.putString("placeName", res.getName());
                bundle.putString("placeIcon", res.getIcon());
                bundle.putString("placeAdd", res.getAddress());
                bundle.putDouble("latitude", res.getLat());
                bundle.putDouble("longitude", res.getLng());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.result_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return results.size();
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

    private boolean inFavorites(String favoKey) {
        return SharedPreferenceManager.getInstance(context.getApplicationContext()).isFavourite(favoKey);
    }

    private void removeFavorite(String favoKey) {
        SharedPreferenceManager.getInstance(context.getApplicationContext()).removeFavourite(favoKey);
    }

    private void addFavorite(String favoKey, String favoJson) {
        SharedPreferenceManager.getInstance(context.getApplicationContext()).setFavourite(favoKey, favoJson);
    }
}
