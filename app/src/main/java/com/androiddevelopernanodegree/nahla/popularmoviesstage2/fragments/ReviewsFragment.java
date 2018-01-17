package com.androiddevelopernanodegree.nahla.popularmoviesstage2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.RecyclerViewReviewsAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.RecyclerViewTrailerAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieReviewsResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieTrailersResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Review;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.TrailersResult;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiClient;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.ShowMoviesActivity.API_KEY;

/**
 * Created by Bolla on 12/30/2017.
 */

public class ReviewsFragment extends Fragment {
    private RecyclerView reviewsRecyclerView;
    private RecyclerViewReviewsAdapter recyclerViewReviewsAdapter;
    private List<Review> reviews = new ArrayList<>();

    public ReviewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        initViews(view);
        setRecyclerAdapter();
        loadReviews();

        return view;
    }

    private void initViews(View view) {
        reviewsRecyclerView = view.findViewById(R.id.movie_data_recycler_reviews);
        recyclerViewReviewsAdapter = new RecyclerViewReviewsAdapter(reviews);
    }

    private void loadReviews(){
        try {
            Result chosenMovie = (Result) getActivity().getIntent().getSerializableExtra("MovieData");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MovieReviewsResponse> call = apiService.getMovieReviews(chosenMovie.getId().toString(),API_KEY);
            Toast.makeText(getActivity().getApplicationContext(), chosenMovie.getId().toString(), Toast.LENGTH_LONG).show();
            call.enqueue(new Callback<MovieReviewsResponse>() {
                @Override
                public void onResponse(Call<MovieReviewsResponse> call, Response<MovieReviewsResponse> response) {
                    if (response.isSuccessful()) {
                        reviews = response.body().getReviews();
                        recyclerViewReviewsAdapter = new RecyclerViewReviewsAdapter(reviews);
                        reviewsRecyclerView.smoothScrollToPosition(0);
                        recyclerViewReviewsAdapter.notifyDataSetChanged();
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

    private void setRecyclerAdapter() {
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reviewsRecyclerView.setAdapter(recyclerViewReviewsAdapter);
        recyclerViewReviewsAdapter.notifyDataSetChanged();
    }



}
