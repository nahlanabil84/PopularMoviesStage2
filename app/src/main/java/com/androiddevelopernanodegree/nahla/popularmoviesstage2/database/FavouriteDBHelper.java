package com.androiddevelopernanodegree.nahla.popularmoviesstage2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bolla on 12/29/2017.
 */

public class FavouriteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoutrite.db";
    private static final int DATABASE_VERSION = 1;
    private static final String LOGTAG = "FAVOURITE";

    SQLiteDatabase db;
    SQLiteOpenHelper sqLiteOpenHelper;

    public FavouriteDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void close() {
        Log.i(LOGTAG,"Database closed");
        sqLiteOpenHelper.close();
    }

    public void open() {
        Log.i(LOGTAG,"Database opened");
        db = sqLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + Favourites.FavouriteEntry.TABLE_NAME + " (" +
                Favourites.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Favourites.FavouriteEntry.COLUMN_MOVIEID + " INTEGER, " +
                Favourites.FavouriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                Favourites.FavouriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                Favourites.FavouriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                Favourites.FavouriteEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                "); ";


        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Favourites.FavouriteEntry.TABLE_NAME);
        onCreate(db);
    }

    public void addFavourite(Result result){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Favourites.FavouriteEntry.COLUMN_MOVIEID, result.getId());
        values.put(Favourites.FavouriteEntry.COLUMN_TITLE, result.getOriginalTitle());
        values.put(Favourites.FavouriteEntry.COLUMN_USERRATING, result.getVoteAverage());
        values.put(Favourites.FavouriteEntry.COLUMN_POSTER_PATH, result.getPosterPath());
        values.put(Favourites.FavouriteEntry.COLUMN_PLOT_SYNOPSIS, result.getOverview());

        db.insert(Favourites.FavouriteEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteFavourite(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Favourites.FavouriteEntry.TABLE_NAME, Favourites.FavouriteEntry.COLUMN_MOVIEID+ "=" + id, null);
    }

    public List<Result> getAllFavourite(){
        String[] columns = {
                Favourites.FavouriteEntry._ID,
                Favourites.FavouriteEntry.COLUMN_MOVIEID,
                Favourites.FavouriteEntry.COLUMN_TITLE,
                Favourites.FavouriteEntry.COLUMN_USERRATING,
                Favourites.FavouriteEntry.COLUMN_POSTER_PATH,
                Favourites.FavouriteEntry.COLUMN_PLOT_SYNOPSIS

        };
        String sortOrder =
                Favourites.FavouriteEntry._ID + " ASC";
        List<Result> favoriteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Favourites.FavouriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                Result result = new Result();
                result.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Favourites.FavouriteEntry.COLUMN_MOVIEID))));
                result.setOriginalTitle(cursor.getString(cursor.getColumnIndex(Favourites.FavouriteEntry.COLUMN_TITLE)));
                result.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Favourites.FavouriteEntry.COLUMN_USERRATING))));
                result.setPosterPath(cursor.getString(cursor.getColumnIndex(Favourites.FavouriteEntry.COLUMN_POSTER_PATH)));
                result.setOverview(cursor.getString(cursor.getColumnIndex(Favourites.FavouriteEntry.COLUMN_PLOT_SYNOPSIS)));

                favoriteList.add(result);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favoriteList;
    }


}
