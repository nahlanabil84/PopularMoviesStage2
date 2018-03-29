package com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMoviesActivity extends AppCompatActivity {

    //TODO Insert your API_KEY
    public final static String API_KEY = "INSERT_YOUR_API_KEY";

    private final String TAG = "Movies";

    private static final String RV_POSITION = "SAVED_POSITION";

    private static final String SORT_CRITERION_KEY = "SORT_CRITERION_KEY";
    public static final String END_POINT_POPULAR = "popular";
    public static final String END_POINT_TOP_RATED = "top_rated";
    public static final String SORT_BY_FAV = "Fav";

    @BindView(R.id.movies_posters_recycler_view)
    RecyclerView moviesRecyclerView;
    @BindView(R.id.sizeZero_tv)
    TextView noMovies;

    ActionBar actionBar;

    private RecyclerViewMoviesAdapter recyclerViewMoviesAdapter;
    private List<Result> favouritesList;
    GridLayoutManager layoutManager;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String listing = "";

    private List<Result> moviesList;
    private ApiInterface apiService;
    private Parcelable mLayoutManagerSavedState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movies);

        ButterKnife.bind(this);

        init();

        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(RV_POSITION);
        }

        String sortBy = sharedPref.getString(SORT_CRITERION_KEY, END_POINT_POPULAR);

        if (!sortBy.contains(SORT_BY_FAV))
            getMovies(sortBy);
        else
            getFavouritesContentProvider();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RV_POSITION, layoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(RV_POSITION);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.most_popular:
                saveSortingSharedPref(END_POINT_POPULAR);
                getMovies(END_POINT_POPULAR);
                return true;

            case R.id.top_rated:
                saveSortingSharedPref(END_POINT_TOP_RATED);
                getMovies(END_POINT_TOP_RATED);
                return true;

            case R.id.favourites:
                saveSortingSharedPref(SORT_BY_FAV);
                getFavouritesContentProvider();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveSortingSharedPref(String sortBy) {
        editor.putString(SORT_CRITERION_KEY, sortBy);
        editor.commit();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void init() {
        actionBar = getSupportActionBar();
        moviesList = new ArrayList<>();
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    private void setAdapters(List<Result> movies) {
        recyclerViewMoviesAdapter = new RecyclerViewMoviesAdapter(movies);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        } else {
            layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        }
        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesRecyclerView.setAdapter(recyclerViewMoviesAdapter);
        recyclerViewMoviesAdapter.notifyDataSetChanged();

        if (mLayoutManagerSavedState != null) {
            layoutManager.onRestoreInstanceState(mLayoutManagerSavedState);
        }

    }

    private void getFavouritesContentProvider() {

        actionBar.setTitle(getResources().getString(R.string.favourites));

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
            moviesList.clear();
            moviesList.addAll(favouritesList);
            setAdapters(favouritesList);

            noMovies.setVisibility(View.GONE);
            moviesRecyclerView.setVisibility(View.VISIBLE);

        } else {

            moviesRecyclerView.setVisibility(View.GONE);
            noMovies.setVisibility(View.VISIBLE);

        }
    }

    private void getMovies(String sortType) {

        setTitle(sortType);

        if (isOnline()) {


            try {
                Call<MovieResponse> call = apiService.getMovies(sortType, API_KEY);
                call.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            moviesList = response.body().getResults();
                            setAdapters(moviesList);

                            noMovies.setVisibility(View.GONE);
                            moviesRecyclerView.setVisibility(View.VISIBLE);

                        } else {


                            Log.v(TAG, response.errorBody().toString());
                            Toast.makeText(getApplicationContext(), "Failed to get movies!! " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                    }
                });
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    private void setTitle(String sortType) {
        switch (sortType) {
            case END_POINT_POPULAR:
                actionBar.setTitle(getResources().getString(R.string.most_popular));
                break;
            case END_POINT_TOP_RATED:
                actionBar.setTitle(getResources().getString(R.string.top_rated));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listing.contains(SORT_BY_FAV))
            getFavouritesContentProvider();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

}
