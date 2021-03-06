package com.example.jay.udacitypopularmovies.service;


import com.example.jay.udacitypopularmovies.data.model.Page;
import com.example.jay.udacitypopularmovies.data.model.Review;
import com.example.jay.udacitypopularmovies.data.model.Trailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jay on 2016-10-08.
 */

public interface PopularMoviesService {
    @GET("/3/movie/{type}")
    Call<Page> listMovies(@Path("type") String type, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Call<Review> listReviews(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/videos")
    Call<Trailer> listTrailers(@Path("id") String id, @Query("api_key") String apiKey);

}
