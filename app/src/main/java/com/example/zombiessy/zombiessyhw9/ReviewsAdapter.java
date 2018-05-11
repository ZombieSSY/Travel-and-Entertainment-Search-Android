package com.example.zombiessy.zombiessyhw9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.zombiessy.zombiessyhw9.ResultsAdapter.getBitmapFromURL;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder>{
    private List<Reviews> reviews;
    private Context context;

    public ReviewsAdapter (List<Reviews> results, final Context context){
        this.reviews = results;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView reviewTime;
        public TextView reviewText;
        public RatingBar rating;
        public LinearLayout reviewItem;
        public ImageView profile;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            author = (TextView) v.findViewById(R.id.reviews_author);
            reviewTime = (TextView) v.findViewById(R.id.reviews_time);
            profile = (ImageView) v.findViewById(R.id.reviews_profile);
            reviewText = (TextView) v.findViewById(R.id.reviews_text);
            rating = (RatingBar) v.findViewById(R.id.reviews_rate);
            reviewItem = (LinearLayout) v.findViewById(R.id.review_item);
        }
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.reviews_list_item, parent, false);
        ReviewsAdapter.ViewHolder vh = new ReviewsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Reviews review = reviews.get(position);

        // Set author with link
        final String authorAndLink = "<a href=\"" + review.getReviewsAuthorUrl() + "\">" + review.getReviewsAuthor() + "</a>";
        holder.author.setAutoLinkMask(0);
        holder.author.setText(Html.fromHtml(authorAndLink));
        holder.author.setMovementMethod(LinkMovementMethod.getInstance());
        URLRemoveUnderline.removeUnderlines((Spannable) holder.author.getText());

        // set review time
        if (review.getYelpTime() != null) {
            holder.reviewTime.setText(review.getYelpTime());
        } else {
            Long time = review.getReviewsTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time * 1000);
            String timeString = formatter.format(calendar.getTime());
            holder.reviewTime.setText(timeString);
        }

        // set review text
        holder.reviewText.setText(review.getReviewsText());

        // set review rating
        holder.rating.setVisibility(View.VISIBLE);
        holder.rating.setRating((float) review.getReviewsRating());

        // set
        if(review.getReviewsProfile() != null){
            final String profileUrl = review.getReviewsProfile();
            holder.profile.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return getBitmapFromURL(profileUrl);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.profile.setImageBitmap(bitmap);
                }
            }.execute();
        } else{
            holder.profile.setVisibility(View.GONE);
        }

        holder.reviewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(review.getReviewsAuthorUrl()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

}
