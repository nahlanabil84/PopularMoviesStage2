package com.androiddevelopernanodegree.nahla.popularmoviesstage2.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.RecyclerViewReviewsAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieReviewsResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Review;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiClient;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.MovieDataActivity.MOVIE_ID;
import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.ShowMoviesActivity.API_KEY;

/**
 * Created by NAHLA on 3/24/2018.
 */

public class ReviewsDialog extends DialogFragment {

    @BindView(R.id.movie_data_recycler_reviews)
    RecyclerView movieDataRecyclerReviews;

    @BindView(R.id.sizeZero_tv)
    TextView noReviewsTV;

    private RecyclerViewReviewsAdapter recyclerViewReviewsAdapter;
    private List<Review> reviews = new ArrayList<>();
    private int movieID;

    public static ReviewsDialog newInstance(int movieID) {
        ReviewsDialog reviewsDialog = new ReviewsDialog();

        Bundle args = new Bundle();
        args.putInt(MOVIE_ID, movieID);
        reviewsDialog.setArguments(args);

        return reviewsDialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reviews, container, false);

        ButterKnife.bind(this, view);

        movieID = getArguments().getInt(MOVIE_ID);

        setAdapter();
        loadReviews();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    private void loadReviews() {
        try {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MovieReviewsResponse> call = apiService.getMovieReviews(movieID, API_KEY);
            call.enqueue(new Callback<MovieReviewsResponse>() {
                @Override
                public void onResponse(Call<MovieReviewsResponse> call, Response<MovieReviewsResponse> response) {
                    if (response.isSuccessful()) {
                        reviews.clear();
                        reviews.addAll(response.body().getReviews());
                        recyclerViewReviewsAdapter.notifyDataSetChanged();

                        if (reviews.size() > 0) {
                            movieDataRecyclerReviews.setVisibility(View.VISIBLE);
                            noReviewsTV.setVisibility(View.GONE);
                        } else {
                            movieDataRecyclerReviews.setVisibility(View.GONE);
                            noReviewsTV.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.v(TAG, response.errorBody().toString());
                        Toast.makeText(getActivity().getApplicationContext(), R.string.failed_to_get_reviews, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MovieReviewsResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            Toast.makeText(getActivity().getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void setAdapter() {
        recyclerViewReviewsAdapter = new RecyclerViewReviewsAdapter(reviews);
        movieDataRecyclerReviews.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        movieDataRecyclerReviews.setItemAnimator(new DefaultItemAnimator());
        movieDataRecyclerReviews.setAdapter(recyclerViewReviewsAdapter);
    }

    @OnClick(R.id.cancelB)
    public void onViewClicked() {
        this.dismiss();
    }
}
