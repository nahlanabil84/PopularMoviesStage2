package com.androiddevelopernanodegree.nahla.popularmoviesstage2.activities;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.database.FavMoviesContentProvider;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.database.Favourites;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.dialogs.ReviewsDialog;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.dialogs.TrailersDialog;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result.IMAGE_BASE_URL;

public class MovieDataActivity extends AppCompatActivity {

    @BindView(R.id.movie_poster_image_view)
    ImageView moviePosterThumbnailIV;
    @BindView(R.id.movie_title_text_view)
    TextView movieTitleTV;
    @BindView(R.id.movie_overview_text_view)
    TextView movieOverviewTV;
    @BindView(R.id.movie_vote_rate_text_view)
    TextView movieVoteAverageTV;
    @BindView(R.id.movie_release_date_text_view)
    TextView movieReleaseDateTV;
    @BindView(R.id.movie_star_markAsFavourite)
    MaterialFavoriteButton materialFavoriteButton;

    public static final String MOVIE_ID = "movie_id";
    private Result chosenMovie;
    private String title, backdropPath, overview, releaseDate;
    private double voteAverage;
    private ActionBar actionBar;
    private String posterPath;
    private boolean isFavorited;
    private List<Result> favouritesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data);

        ButterKnife.bind(this);

        chosenMovie = getIntent().getParcelableExtra("MovieData");
        actionBar = getSupportActionBar();
        isFavorited = searchInFavoritesContentProvider(chosenMovie.getId());

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
                                saveFavouriteContentProvider();
                            } else {
                                Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                                        + getResources().getString(R.string.already_favorited), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            removeFavouriteContentProvider(chosenMovie.getId());
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
        if (backdropPath != null) {
            url = IMAGE_BASE_URL + backdropPath;
        } else {
            url = IMAGE_BASE_URL + posterPath;
        }
        Picasso.with(this).load(url).centerCrop().fit().into(moviePosterThumbnailIV);
        movieTitleTV.setText(title);
        movieOverviewTV.setText(overview);
        movieVoteAverageTV.setText(String.valueOf(voteAverage));
        if (releaseDate == null)
            movieReleaseDateTV.setVisibility(View.GONE);
        movieReleaseDateTV.setText(releaseDate);

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

    private boolean searchInFavoritesContentProvider(int id) {
        boolean isFav = false;

        favouritesList = getFavoritesList();

        if (favouritesList.size() <= 0) {
            isFav = false;
        } else {
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

    private int getFavSize() {
        int count = 0;

        favouritesList = getFavoritesList();

        if (favouritesList.size() <= 0) {
            count = 0;
        } else {
            count = favouritesList.size();
        }

        return count;
    }

    @NonNull
    private List<Result> getFavoritesList() {
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
        return results;
    }

    private void removeFavouriteContentProvider(int id) {
        int del = getContentResolver().delete(FavMoviesContentProvider.URI_MOVIE, Favourites.FavouriteEntry.COLUMN_MOVIEID + "=?", new String[]{String.valueOf(id)});

        if (del > 0) {
            Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                    + getResources().getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
            if (getFavSize() == 0) {

            }
        } else {
            Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                    + getResources().getString(R.string.not_removed_from_fav), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFavouriteContentProvider() {

        ContentValues values = new ContentValues();
        values.put(Favourites.FavouriteEntry.COLUMN_MOVIEID, chosenMovie.getId());
        values.put(Favourites.FavouriteEntry.COLUMN_TITLE, chosenMovie.getOriginalTitle());
        values.put(Favourites.FavouriteEntry.COLUMN_USERRATING, chosenMovie.getVoteAverage());
        values.put(Favourites.FavouriteEntry.COLUMN_POSTER_PATH, chosenMovie.getPosterPath());
        values.put(Favourites.FavouriteEntry.COLUMN_PLOT_SYNOPSIS, chosenMovie.getOverview());

        Uri uri = getContentResolver().insert(FavMoviesContentProvider.URI_MOVIE, values);

        long rid = Long.valueOf(uri.getLastPathSegment());

        if (rid > 0) {
            Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                    + getResources().getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), chosenMovie.getOriginalTitle() + " "
                    + getResources().getString(R.string.not_added), Toast.LENGTH_SHORT).show();
        }

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
