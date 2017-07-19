package com.example.jay.udacitypopularmovies.ui.fragment.moviesfragment;

import com.example.jay.udacitypopularmovies.R;
import com.example.jay.udacitypopularmovies.dbandmodels.Movie;
import com.example.jay.udacitypopularmovies.schedulers.BaseSchedulerProvider;
import com.example.jay.udacitypopularmovies.service.DatabaseService;
import com.example.jay.udacitypopularmovies.service.MoviesService;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jay on 2017-07-16.
 */

public class MoviesFragmentPresenter extends MvpBasePresenter<MovieFragmentContract.View> implements MovieFragmentContract.Actions {

    private final MoviesService moviesService;
    private final DatabaseService databaseService;
    private final BaseSchedulerProvider schedulerProvider;
    private final CompositeDisposable disposables;

    public MoviesFragmentPresenter(MoviesService moviesService, DatabaseService databaseService, BaseSchedulerProvider schedulerProvider) {
        this.moviesService = moviesService;
        this.databaseService = databaseService;
        this.schedulerProvider = schedulerProvider;
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void fetchMovies() {
        disposables.add(moviesService.fetchMovies()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(new DisposableSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(@NonNull List<Movie> movies) {
                        insertMovies(movies);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (isViewAttached()) {
                            getView().showMessage(R.string.error_fetch_movies);
                        }
                    }
                }));
    }

    @Override
    public void fetchMovies(int pageNumber) {
        disposables.add(moviesService.fetchMoviesPage(pageNumber)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(new DisposableSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(@NonNull List<Movie> movies) {
                        insertMovies(movies);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (isViewAttached()){
                            getView().showMessage(R.string.error_fetch_movies);
                        }
                    }
                }));
    }

    @Override
    public void insertMovies(List<Movie> movies) {
        disposables.add(databaseService.insertMovies(movies)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        if (isViewAttached()) {
                            getView().showMovies();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (isViewAttached()) {
                            getView().showMessage(R.string.error_insert_movies);
                        }
                    }
                }));
    }

    @Override
    public void onPause() {
        disposables.dispose();
    }

    @Override
    public void onClickMovie(Movie movie) {
        if (isViewAttached()) {
            getView().showMovieDetails(movie);
        }
    }

    @Override
    public void onClickSortPopular() {

    }

    @Override
    public void onClickSortTopRated() {

    }

    @Override
    public void onClickSortFavourites() {

    }

}
