package com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.RecyclerViewTrailerAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.database.FavouriteDBHelper;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieTrailersResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.TrailersResult;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiClient;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit.ApiInterface;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.ShowMoviesActivity.API_KEY;
import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result.IMAGE_BASE_URL;

public class MovieDataActivity extends AppCompatActivity {
    private Result chosenMovie;
    private ImageView moviePosterThumbnailIV;
    private TextView movieTitleTV, movieOverviewTV, movieVoteAverageTV, movieReleaseDateTV;
    private String title, backdropPath, overview, releaseDate;
    private RecyclerView trailersRecyclerView;
    private RecyclerViewTrailerAdapter recyclerViewTrailerAdapter;
    private double voteAverage;
    private List<TrailersResult> trailersResults;
    private FavouriteDBHelper favouriteDBHelper;
    private Result favourite;
    private MaterialFavoriteButton materialFavoriteButton;
    private ActionBar actionBar;
    private String postarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data);

        initViews();
        getData();
        actionBar.setTitle(title);
        setRecyclerAdapter();
        setActions();
        setData();
    }

    private void setActions() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        materialFavoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener(){
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite){
                        if (favorite){
                            SharedPreferences.Editor editor =
                                    getSharedPreferences("com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.MovieDataActivity"
                                    , MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Added", true);
                            editor.commit();
                            saveFavourite();
                            Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                                    + getResources().getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();

                        }else{
                            int movie_id = getIntent().getExtras().getInt("id");
                            removeFavourite(movie_id);
                            SharedPreferences.Editor editor =
                                    getSharedPreferences("com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities.MovieDataActivity"
                                    , MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Removed", true);
                            editor.commit();
                            Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                                    + getResources().getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

    }

    private void setRecyclerAdapter() {
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        trailersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trailersRecyclerView.setAdapter(recyclerViewTrailerAdapter);
        recyclerViewTrailerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setData() {
        String url;
        if(backdropPath != null){
            url = IMAGE_BASE_URL + backdropPath;
        }else {
            url = IMAGE_BASE_URL + postarPath;
        }
        Picasso.with(this).load(url).into(moviePosterThumbnailIV);
        movieTitleTV.setText(title);
        movieOverviewTV.setText(overview);
        movieVoteAverageTV.setText(String.valueOf(voteAverage));
        if(releaseDate==null)
            movieReleaseDateTV.setVisibility(View.GONE);
        movieReleaseDateTV.setText(releaseDate);

    }

    private void initViews() {
        moviePosterThumbnailIV = (ImageView) findViewById(R.id.movie_poster_image_view);
        movieTitleTV = (TextView) findViewById(R.id.movie_title_text_view);
        movieOverviewTV = (TextView) findViewById(R.id.movie_overview_text_view);
        movieVoteAverageTV = (TextView) findViewById(R.id.movie_vote_rate_text_view);
        movieReleaseDateTV = (TextView) findViewById(R.id.movie_release_date_text_view);
        trailersRecyclerView = (RecyclerView) findViewById(R.id.movie_data_recycler_trailers);
        materialFavoriteButton = (MaterialFavoriteButton) findViewById(R.id.movie_star_markAsFavourite);
        trailersResults = new ArrayList<>();
        recyclerViewTrailerAdapter = new RecyclerViewTrailerAdapter(getApplicationContext(), trailersResults);
        actionBar = getSupportActionBar();
        loadTrailerData();
    }

    private void getData() {
        Intent intent = getIntent();
        chosenMovie = (Result) intent.getSerializableExtra("MovieData");
        title = chosenMovie.getOriginalTitle();
        backdropPath = chosenMovie.getBackdropPath();
        postarPath = chosenMovie.getPosterPath();
        overview = chosenMovie.getOverview();
        voteAverage = chosenMovie.getVoteAverage();
        releaseDate = chosenMovie.getReleaseDate();
    }

    private void loadTrailerData() {
        getData();
        ApiClient apiClient = new ApiClient();
        ApiInterface apiInterface = (ApiInterface) apiClient.getClient().create(ApiInterface.class);
        Call<MovieTrailersResponse> call = apiInterface.getMovieTrailers(chosenMovie.getId().toString(), API_KEY);
        call.enqueue(new Callback<MovieTrailersResponse>() {
            @Override
            public void onResponse(Call<MovieTrailersResponse> call, Response<MovieTrailersResponse> response) {
                trailersResults = response.body().getTrailersResults();
                trailersRecyclerView.setAdapter(new RecyclerViewTrailerAdapter(getApplicationContext(), trailersResults));
                trailersRecyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(Call<MovieTrailersResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.check_network_connection, Toast.LENGTH_SHORT).show();
                Log.e("Load trailer: ", t.getMessage().toString());
            }
        });
    }

    private void removeFavourite(int id) {
        favouriteDBHelper = new FavouriteDBHelper(getApplicationContext());
        favouriteDBHelper.deleteFavourite(id);
    }

    private void saveFavourite() {
        favouriteDBHelper = new FavouriteDBHelper(getApplicationContext());
        favourite = new Result();
        favourite.setId(chosenMovie.getId());
        favourite.setOriginalTitle(chosenMovie.getOriginalTitle());
        favourite.setPosterPath(chosenMovie.getPosterPath());
        favourite.setVoteAverage(chosenMovie.getVoteAverage());
        favourite.setOverview(chosenMovie.getOverview());
        favourite.setReleaseDate(chosenMovie.getReleaseDate());

        favouriteDBHelper.addFavourite(favourite);

    }
}
