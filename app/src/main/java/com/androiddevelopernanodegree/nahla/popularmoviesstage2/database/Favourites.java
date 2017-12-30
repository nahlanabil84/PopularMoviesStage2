package com.androiddevelopernanodegree.nahla.popularmoviesstage2.database;

import android.provider.BaseColumns;

/**
 * Created by Bolla on 12/29/2017.
 */

public class Favourites {
    public static final class FavouriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourite";
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_USERRATING = "userrating";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_PLOT_SYNOPSIS = "overview";
    }
}
