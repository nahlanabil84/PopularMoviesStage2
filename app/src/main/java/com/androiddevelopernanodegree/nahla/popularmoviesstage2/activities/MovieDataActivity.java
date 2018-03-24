package com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.database.FavouriteDBHelper;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.dialogs.ReviewsDialog;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.dialogs.TrailersDialog;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result.IMAGE_BASE_URL;

public class MovieDataActivity extends AppCompatActivity{
    public static final String MOVIE_ID = "movie_id";
    private Result chosenMovie;
    private ImageView moviePosterThumbnailIV;
    private TextView movieTitleTV, movieOverviewTV, movieVoteAverageTV, movieReleaseDateTV;
    private String title, backdropPath, overview, releaseDate;
    private double voteAverage;
    private FavouriteDBHelper favouriteDBHelper;
    private Result favourite;
    private MaterialFavoriteButton materialFavoriteButton;
    private ActionBar actionBar;
    private String posterPath;
    private boolean isFavorited;
    private List<Result> favouritesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data);

        ButterKnife.bind(this);

        chosenMovie = (Result) getIntent().getSerializableExtra("MovieData");

        initViews();
        getData();
        actionBar.setTitle(title);
        setActions();
        setData();
    }

    private void setActions() {
        materialFavoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            if (!isFavorited) {
                                SharedPreferences.Editor editor =
                                        getSharedPreferences("com.androiddevelopernanodegree.nahla" +
                                                        ".popularmoviesstage2.activities.MovieDataActivity"
                                                , MODE_PRIVATE).edit();
                                editor.putBoolean("Favorite Added", true);
                                editor.commit();
                                saveFavourite();
                                Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                                        + getResources().getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                                        + getResources().getString(R.string.already_favorited), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            removeFavourite(chosenMovie.getId());
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

    private boolean searchInFavorites(int id) {
        boolean isFav = false;

        favouriteDBHelper = new FavouriteDBHelper(getApplicationContext());
        favouritesList = favouriteDBHelper.getAllFavourite();
        if (favouritesList.size() == 0) {
            isFav = false;
        } else if (favouritesList.size() > 0) {
            for (int i = 0; i < favouritesList.size(); i++) {
                if (favouritesList.get(i).getId() == id) {
                    isFav = true;
                    break;
                } else
                    isFav = false;
            }
        }

        return isFav;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setData() {
        String url;
        if (backdropPath != null) {
            url = IMAGE_BASE_URL + backdropPath;
        } else {
            url = IMAGE_BASE_URL + posterPath;
        }
        Picasso.with(this).load(url).into(moviePosterThumbnailIV);
        movieTitleTV.setText(title);
        movieOverviewTV.setText(overview);
        movieVoteAverageTV.setText(String.valueOf(voteAverage));
        if (releaseDate == null)
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
        actionBar = getSupportActionBar();
        isFavorited = searchInFavorites(chosenMovie.getId());
    }

    private void getData() {
        title = chosenMovie.getOriginalTitle();
        backdropPath = chosenMovie.getBackdropPath();
        posterPath = chosenMovie.getPosterPath();
        overview = chosenMovie.getOverview();
        voteAverage = chosenMovie.getVoteAverage();
        releaseDate = chosenMovie.getReleaseDate();

        if (isFavorited) {
            materialFavoriteButton.setFavorite(true);
        } else
            materialFavoriteButton.setFavorite(false);

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

    @OnClick({R.id.trailersB, R.id.reviewsB})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.trailersB:
                showTrailersDialog();
                break;
            case R.id.reviewsB:
                showReviewsDialog();
                break;
        }
    }

    private void showReviewsDialog() {
        ReviewsDialog dialog = ReviewsDialog.newInstance(chosenMovie.getId());
        dialog.show(getFragmentManager(), "reviews");
    }

    private void showTrailersDialog() {
        TrailersDialog dialog = TrailersDialog.newInstance(chosenMovie.getId());
        dialog.show(getFragmentManager(), "reviews");
    }
}
