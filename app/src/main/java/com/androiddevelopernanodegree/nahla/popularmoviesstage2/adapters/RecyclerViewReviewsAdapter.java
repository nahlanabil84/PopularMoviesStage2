package com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Review;

import java.util.List;

/**
 * Created by Bolla on 12/28/2017.
 */

public class RecyclerViewReviewsAdapter extends RecyclerView.Adapter<RecyclerViewReviewsAdapter.ReviewHolder> {

    private List<Review> reviews;

    public RecyclerViewReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new ReviewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        holder.reviewTextTV.setText(reviews.get(position).getContent());
        holder.reviewAuthorTV.setText(reviews.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        TextView reviewTextTV;
        TextView reviewAuthorTV;

        public ReviewHolder(View itemView) {
            super(itemView);
            reviewTextTV = (TextView) itemView.findViewById(R.id.review_text);
            reviewAuthorTV = (TextView) itemView.findViewById(R.id.review_author);
        }
    }
}
