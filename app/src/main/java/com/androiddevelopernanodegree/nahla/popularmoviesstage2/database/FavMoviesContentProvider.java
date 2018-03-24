package com.androiddevelopernanodegree.nahla.popularmoviesstage2.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FavMoviesContentProvider extends ContentProvider {
    public final static int MOVIE = 500;
    public final static int MOVIE_ID = 100;

    public final static String AUTHORITY_NAME = "com.androiddevelopernanodegree.nahla.popularmoviesstage2.database";

    public final static Uri URI_MOVIE = Uri.parse("content://" + AUTHORITY_NAME + "/" + Favourites.FavouriteEntry.TABLE_NAME);
    public final static Uri URI_MOVIE_ID = Uri.parse("content://" + AUTHORITY_NAME + "/" + Favourites.FavouriteEntry.TABLE_NAME + "/" + MOVIE_ID);

    private FavouriteDBHelper favouriteDBHelper;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY_NAME, Favourites.FavouriteEntry.TABLE_NAME, MOVIE);
        matcher.addURI(AUTHORITY_NAME, Favourites.FavouriteEntry.TABLE_NAME + "/*", MOVIE_ID);

        return matcher;
    }

    public FavMoviesContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri uri_ = null;

        if (favouriteDBHelper == null) {
            favouriteDBHelper = new FavouriteDBHelper(getContext());
        }

        final SQLiteDatabase database = favouriteDBHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case MOVIE:
                long ID_ = database.insert(Favourites.FavouriteEntry.TABLE_NAME, null, values);
                if (ID_ > 0) {
                    uri_ = ContentUris.withAppendedId(URI_MOVIE, ID_);
                    getContext().getContentResolver().notifyChange(uri_, null);
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }

        return uri_;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        favouriteDBHelper = new FavouriteDBHelper(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        SQLiteDatabase database = favouriteDBHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case MOVIE:
                cursor = database.query(Favourites.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
