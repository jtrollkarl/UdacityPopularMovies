package com.example.jay.udacitypopularmovies.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.jay.udacitypopularmovies.dbandmodels.Favourite;
import com.example.jay.udacitypopularmovies.dbandmodels.Movie;
import com.example.jay.udacitypopularmovies.dbandmodels.Movie_Table;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;


/**
 * Created by Jay on 2016-10-09.
 */

public class UILoader extends AsyncTaskLoader<Cursor> {

    public static final String ACTION_FORCE = UILoader.class.getSimpleName() + ":FORCE_LOAD";
    public static final int LOADER_ID_POPULAR = 1;
    public static final int LOADER_ID_TOP_RATED = 2;
    public static final int LOADER_ID_FAVOURITES = 3;
    private static final String TAG = UILoader.class.getSimpleName();

    public UILoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(TAG, String.valueOf(getId()));

        switch (getId()){
            case LOADER_ID_POPULAR:
                FlowCursorList<Movie> listPopular = SQLite.select()
                        .from(Movie.class)
                        .where()
                        .orderBy(Movie_Table.popularity, false)
                        .cursorList();
                return listPopular.cursor();
            case LOADER_ID_TOP_RATED:
                FlowCursorList<Movie> listTopRated = SQLite.select()
                        .from(Movie.class)
                        .where()
                        .orderBy(Movie_Table.voteAverage, false)
                        .cursorList();
                return listTopRated.cursor();
            case LOADER_ID_FAVOURITES:
                FlowCursorList<Favourite> listFav = SQLite.select()
                        .from(Favourite.class)
                        .where()
                        .cursorList();
                return listFav.cursor();
        }
        return null;
    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);
    }

}
