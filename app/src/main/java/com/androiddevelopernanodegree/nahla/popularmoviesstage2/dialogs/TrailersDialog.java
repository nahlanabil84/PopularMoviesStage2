package com.androiddevelopernanodegree.nahla.popularmoviesstage2.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.RecyclerViewTrailerAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieTrailersResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.TrailersResult;
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

import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.MovieDataActivity.MOVIE_ID;
import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.ShowMoviesActivity.API_KEY;
import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.ShowMoviesActivity.calculateNoOfColumns;

/**
 * Created by NAHLA on 3/24/2018.
 */

public class TrailersDialog extends DialogFragment {

    @BindView(R.id.movie_data_recycler_trailers)
    RecyclerView movieDataRecyclerTrailers;

    @BindView(R.id.sizeZero_tv)
    TextView noTrailersTV;

    private RecyclerViewTrailerAdapter recyclerViewTrailerAdapter;
    private GridLayoutManager layoutManager;
    private List<TrailersResult> trailersResults = new ArrayList<>();
    private int movieID;

    public static TrailersDialog newInstance(int movieID) {
        TrailersDialog trailersDialog = new TrailersDialog();

        Bundle args = new Bundle();
        args.putInt(MOVIE_ID, movieID);
        trailersDialog.setArguments(args);

        return trailersDialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_trailers, container, false);

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
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
    }

    private void loadReviews() {
        ApiClient apiClient = new ApiClient();
        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);
        Call<MovieTrailersResponse> call = apiInterface.getMovieTrailers(movieID, API_KEY);
        call.enqueue(new Callback<MovieTrailersResponse>() {
            @Override
            public void onResponse(Call<MovieTrailersResponse> call, Response<MovieTrailersResponse> response) {
                if (response.isSuccessful()) {
                    trailersResults.clear();
                    trailersResults.addAll(response.body().getTrailersResults());
                    movieDataRecyclerTrailers.setAdapter(new RecyclerViewTrailerAdapter(getActivity().getApplicationContext(), trailersResults));

                    if (trailersResults.size() > 0) {
                        movieDataRecyclerTrailers.setVisibility(View.VISIBLE);
                        noTrailersTV.setVisibility(View.GONE);
                    } else {
                        movieDataRecyclerTrailers.setVisibility(View.GONE);
                        noTrailersTV.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.failed_to_get_trailers, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MovieTrailersResponse> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.check_network_connection, Toast.LENGTH_SHORT).show();
                Log.e("Load trailer: ", t.getMessage().toString());
            }
        });


    }

    private void setAdapter() {
        layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), calculateNoOfColumns(getActivity().getApplicationContext()));
        movieDataRecyclerTrailers.setLayoutManager(layoutManager);
//        movieDataRecyclerTrailers.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerViewTrailerAdapter = new RecyclerViewTrailerAdapter(getActivity().getApplicationContext(), trailersResults);
        movieDataRecyclerTrailers.setItemAnimator(new DefaultItemAnimator());
        movieDataRecyclerTrailers.setAdapter(recyclerViewTrailerAdapter);
    }

    @OnClick(R.id.cancelB)
    public void onViewClicked() {
        this.dismiss();
    }

}
