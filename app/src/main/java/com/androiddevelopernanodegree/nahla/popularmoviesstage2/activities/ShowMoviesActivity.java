package com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.RecyclerViewMoviesAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.database.FavMoviesContentProvider;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.database.Favourites;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiClient;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMoviesActivity extends AppCompatActivity {
    private final String TAG = "Movies";
    //TODO Insert your API_KEY
    public final static String API_KEY = "INSERT_YOUR_API_KEY";
    private RecyclerView moviesRecyclerView;
    private TextView noMovies;
    private List<Result> moviesList;
    private ApiInterface apiService;
    private RecyclerViewMoviesAdapter recyclerViewMoviesAdapter;
    private ProgressDialog dialog;
    private List<Result> favouritesList;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movies);

        if (isOnline()) {
            initViews();
            getMostPopularMovies();
            setAdapters(moviesList);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setLoadingDialog();
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setLoadingDialog();
        switch (item.getItemId()) {
            case R.id.most_popular:
                dialog.show();
                getMostPopularMovies();
                return true;
            case R.id.top_rated:
                getTopRatedMovies();
                return true;
            case R.id.favourites:
                getFavouritesContentProvider();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void initViews() {
        moviesRecyclerView = findViewById(R.id.movies_posters_recycler_view);
        noMovies = findViewById(R.id.sizeZero_tv);
        actionBar = getSupportActionBar();
        moviesList = new ArrayList<>();
        apiService = ApiClient.getClient().create(ApiInterface.class);
        setLoadingDialog();
    }

    private void setAdapters(List<Result> movies) {
        recyclerViewMoviesAdapter = new RecyclerViewMoviesAdapter(movies);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        moviesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesRecyclerView.setAdapter(recyclerViewMoviesAdapter);
        recyclerViewMoviesAdapter.notifyDataSetChanged();

    }

    private void setLoadingDialog() {
        dialog = new ProgressDialog(ShowMoviesActivity.this);
        String pleaseWait = getResources().getString(R.string.Dialog_please_wait);
        dialog.setMessage(pleaseWait);
        dialog.setCancelable(false);
    }

    private void getFavouritesContentProvider() {
        dialog.show();
        favouritesList = new ArrayList<>();
        List<Result> results = new ArrayList<>();
        String sortingOrder = Favourites.FavouriteEntry.COLUMN_TITLE + " ASC";
        String selection = null;
        String[] selectionArgs = null;
        Cursor cursor = getContentResolver().query(FavMoviesContentProvider.URI_MOVIE, null, selection, selectionArgs, sortingOrder);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            Result favMovie = null;
            do {
                favMovie = new Result();
                favMovie.setId(cursor.getInt(1));
                favMovie.setOriginalTitle(cursor.getString(2));
                favMovie.setVoteAverage(cursor.getDouble(3));
                favMovie.setPosterPath(cursor.getString(4));
                favMovie.setOverview(cursor.getString(5));

                results.add(favMovie);
            } while (cursor.moveToNext());
        }

        favouritesList = results;
        if (favouritesList.size() > 0) {
            setAdapters(favouritesList);
            actionBar.setTitle(getResources().getString(R.string.favourites));

            noMovies.setVisibility(View.GONE);
            moviesRecyclerView.setVisibility(View.VISIBLE);

            dialog.dismiss();
        } else {
            actionBar.setTitle(getResources().getString(R.string.favourites));

            moviesRecyclerView.setVisibility(View.GONE);
            noMovies.setVisibility(View.VISIBLE);

            dialog.dismiss();
        }
    }

    private void getMostPopularMovies() {
        try {
            Call<MovieResponse> call = apiService.getMostPopularMovies(API_KEY);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful()) {
                        moviesList.clear();
                        moviesList.addAll(response.body().getResults());
                        setAdapters(moviesList);
                        actionBar.setTitle(getResources().getString(R.string.most_popular));

                        noMovies.setVisibility(View.GONE);
                        moviesRecyclerView.setVisibility(View.VISIBLE);

                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        Log.v(TAG, response.errorBody().toString());
                        Toast.makeText(getApplicationContext(), "Failed to get movies!! " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    // Log error here since request failed
                    dialog.dismiss();
                    Log.e(TAG, t.toString());
                }
            });
        } catch (Exception ex) {
            dialog.dismiss();
            Log.e(TAG, ex.toString());
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void getTopRatedMovies() {
        dialog.show();
        try {
            Call<MovieResponse> call = apiService.getTopRatedMovies(API_KEY);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful()) {
                        moviesList = response.body().getResults();
                        setAdapters(moviesList);
                        actionBar.setTitle(getResources().getString(R.string.top_rated));

                        noMovies.setVisibility(View.GONE);
                        moviesRecyclerView.setVisibility(View.VISIBLE);

                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        Log.v(TAG, response.errorBody().toString());
                        Toast.makeText(getApplicationContext(), "Failed to get movies!! " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    // Log error here since request failed
                    dialog.dismiss();
                    Log.e(TAG, t.toString());
                }
            });
        } catch (Exception ex) {
            dialog.dismiss();
            Log.e(TAG, ex.toString());
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
