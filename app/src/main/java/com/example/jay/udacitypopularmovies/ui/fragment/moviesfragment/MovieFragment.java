package com.example.jay.udacitypopularmovies.ui.fragment.moviesfragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jay.udacitypopularmovies.Injector;
import com.example.jay.udacitypopularmovies.adapters.MovieAdapter;
import com.example.jay.udacitypopularmovies.R;
import com.example.jay.udacitypopularmovies.dbandmodels.Movie;
import com.example.jay.udacitypopularmovies.misc.RecyclerViewItemDecorator;
import com.example.jay.udacitypopularmovies.schedulers.SchedulerProvider;
import com.example.jay.udacitypopularmovies.loader.MovieLoader;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MovieFragment extends MvpFragment<MovieFragmentContract.View,
        MovieFragmentContract.Actions>
        implements MovieFragmentContract.View,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = MovieFragment.class.getSimpleName();
    @BindView(R.id.movie_recycler) RecyclerView recyclerView;
    private MovieAdapter adapter;
    private static int CURRENT_LOADER;
    private static final String LOADER_KEY = "LOADER_KEY";
    private Unbinder unbinder;

    @Override
    public MovieFragmentContract.Actions createPresenter() {
        return new MoviesFragmentPresenter(Injector.provideMoviesService(), Injector.provideDatabaseService(), new SchedulerProvider());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSetCurrentLoader(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(CURRENT_LOADER, null, this);
    }

    private void checkSetCurrentLoader(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState == null || !savedInstanceState.containsKey(LOADER_KEY)){
            Log.d(TAG, "No saved instance state. Loading default loader");
            CURRENT_LOADER = 1;
        }else{
            Log.d(TAG, "Loading saved loader id: " + CURRENT_LOADER);
            CURRENT_LOADER = savedInstanceState.getInt(LOADER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MovieAdapter(getActivity(), null);
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(10));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(LOADER_KEY, CURRENT_LOADER);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==  R.id.menuSortPopularity) {
            presenter.onClickSortPopular();
            return true;
        }
        else if(item.getItemId() == R.id.menuSortRating){
            presenter.onClickSortTopRated();
            return true;
        }
        else if(item.getItemId() == R.id.menuSortFavourites){
            presenter.onClickSortFavourites();
            return true;

        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(getActivity().getApplicationContext());

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Load finished");
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
        adapter.swapCursor(null);
    }


    @Override
    public void showMovies() {
        Log.d(TAG, "Showing movies with id: " + String.valueOf(CURRENT_LOADER));
        //getActivity().getLoaderManager().getLoader(CURRENT_LOADER).forceLoad();
    }

    @Override
    public void showMovieDetails(Movie movie) {

    }

    @Override
    public void showMessage(@StringRes int resId) {
        Toast.makeText(this.getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showNotLoading() {

    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.fetchMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
