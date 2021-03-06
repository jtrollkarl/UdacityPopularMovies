package com.example.jay.udacitypopularmovies.service;

import com.example.jay.udacitypopularmovies.data.model.Movie;
import com.example.jay.udacitypopularmovies.data.model.ResultReviews;
import com.example.jay.udacitypopularmovies.data.model.ResultTrailer;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Jay on 2017-07-16.
 */

public interface MoviesService {

    public static final String TYPE_POPULAR = "popular";
    public static final String TYPE_TOP_RATED = "top_rated";

    Single<List<Movie>> fetchMovies(String type);

    Single<List<Movie>> fetchMoviesPage(String type, int pageNumber);

    Single<List<ResultTrailer>> fetchTrailers(String movieId);

    Single<List<ResultReviews>> fetchReviews(String movieId);

}
