package com.androiddevelopernanodegree.nahla.popularmoviesstage2.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.RecyclerViewTrailerAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieTrailersResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.TrailersResult;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiClient;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.ShowMoviesActivity.API_KEY;

/**
 * Created by Bolla on 12/30/2017.
 */

public class TrailersFragment extends Fragment {

    private RecyclerView trailersRecyclerView;
    private RecyclerViewTrailerAdapter recyclerViewTrailerAdapter;
    private List<TrailersResult> trailersResults = new ArrayList<>();

    public TrailersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trailers, container, false);

        initViews(view);
        setRecyclerAdapter();
        loadTrailerData();

        return view;
    }

    private void initViews(View view) {
        trailersRecyclerView = (RecyclerView) view.findViewById(R.id.movie_data_recycler_trailers);
        trailersResults = new ArrayList<>();
        recyclerViewTrailerAdapter = new RecyclerViewTrailerAdapter(getActivity().getApplicationContext(), trailersResults);
    }

    private void loadTrailerData() {
        Result chosenMovie = (Result) getActivity().getIntent().getSerializableExtra("MovieData");
        ApiClient apiClient = new ApiClient();
        ApiInterface apiInterface = (ApiInterface) apiClient.getClient().create(ApiInterface.class);
        Call<MovieTrailersResponse> call = apiInterface.getMovieTrailers(chosenMovie.getId().toString(), API_KEY);
        call.enqueue(new Callback<MovieTrailersResponse>() {
            @Override
            public void onResponse(Call<MovieTrailersResponse> call, Response<MovieTrailersResponse> response) {
                trailersResults = response.body().getTrailersResults();
                trailersRecyclerView.setAdapter(new RecyclerViewTrailerAdapter(getActivity().getApplicationContext(), trailersResults));
                trailersRecyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(Call<MovieTrailersResponse> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.check_network_connection, Toast.LENGTH_SHORT).show();
                Log.e("Load trailer: ", t.getMessage().toString());
            }
        });
    }

    private void setRecyclerAdapter() {
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        trailersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trailersRecyclerView.setAdapter(recyclerViewTrailerAdapter);
        recyclerViewTrailerAdapter.notifyDataSetChanged();
    }

}
