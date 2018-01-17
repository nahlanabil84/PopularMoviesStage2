package com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.adapters.ViewPagerAdapter;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.database.FavouriteDBHelper;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.fragments.ReviewsFragment;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.fragments.TrailersFragment;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result.IMAGE_BASE_URL;

public class MovieDataActivity extends AppCompatActivity {
    private Result chosenMovie;
    private ImageView moviePosterThumbnailIV;
    private TextView movieTitleTV, movieOverviewTV, movieVoteAverageTV, movieReleaseDateTV;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String title, backdropPath, overview, releaseDate;
    private double voteAverage;
    private FavouriteDBHelper favouriteDBHelper;
    private Result favourite;
    private MaterialFavoriteButton materialFavoriteButton;
    private ActionBar actionBar;
    private String posterPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data);

        initViews();
        setTabViewPager(viewPager);
        getData();
        actionBar.setTitle(title);
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
                                    getSharedPreferences("com.androiddevelopernanodegree.nahla" +
                                                    ".popularmoviesstage2.activities.MovieDataActivity"
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
                                    getSharedPreferences("com.androiddevelopernanodegree.nahla" +
                                                    ".popularmoviesstage2.activities.MovieDataActivity"
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
            url = IMAGE_BASE_URL + posterPath;
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
        materialFavoriteButton = (MaterialFavoriteButton) findViewById(R.id.movie_star_markAsFavourite);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        actionBar = getSupportActionBar();
    }

    private void getData() {
        chosenMovie = (Result) getIntent().getSerializableExtra("MovieData");
        title = chosenMovie.getOriginalTitle();
        backdropPath = chosenMovie.getBackdropPath();
        posterPath = chosenMovie.getPosterPath();
        overview = chosenMovie.getOverview();
        voteAverage = chosenMovie.getVoteAverage();
        releaseDate = chosenMovie.getReleaseDate();
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

    private void setTabViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TrailersFragment(),getResources().getString(R.string.trailers));
        adapter.addFrag(new ReviewsFragment(), getResources().getString(R.string.reviews));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
