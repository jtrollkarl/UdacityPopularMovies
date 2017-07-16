package com.example.jay.udacitypopularmovies.ui.fragment.detailsfragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.udacitypopularmovies.dbandmodels.Favourite;
import com.example.jay.udacitypopularmovies.dbandmodels.Movie;
import com.example.jay.udacitypopularmovies.misc.Urls;
import com.example.jay.udacitypopularmovies.retrofitservice.PopularMoviesService;
import com.example.jay.udacitypopularmovies.R;
import com.example.jay.udacitypopularmovies.dbandmodels.ResultReviews;
import com.example.jay.udacitypopularmovies.dbandmodels.ResultTrailer;
import com.example.jay.udacitypopularmovies.dbandmodels.Review;
import com.example.jay.udacitypopularmovies.dbandmodels.Trailer;
import com.example.jay.udacitypopularmovies.adapters.ReviewAdapter;
import com.example.jay.udacitypopularmovies.adapters.TrailerAdapter;
import com.example.jay.udacitypopularmovies.apikey.MovieApiKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {


    public static final String TAG = DetailFragment.class.getSimpleName();
    public static final String MOVIE_KEY = "MOVIE_KEY";
    @BindView(R.id.poster)
    ImageView poster;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.detailMovie)
    ImageView detailMovie;
    @BindView(R.id.movieTitle)
    TextView movieTitle;
    @BindView(R.id.movieRelease)
    TextView movieRelease;
    @BindView(R.id.movieRating)
    TextView movieRating;
    @BindView(R.id.movieSynopsis)
    TextView movieSynopsis;
    @BindView(R.id.fabFavourite)
    FloatingActionButton fabFavourite;
    @BindView(R.id.trailer_recycler)
    RecyclerView trailerRecycler;


    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    @BindView(R.id.review_recycler)
    RecyclerView reviewRecycler;
    private Retrofit retrofit;
    private Movie movieCurrent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "Attached");
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Urls.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LinearLayoutManager lmH = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailerAdapter = new TrailerAdapter(getContext());
        trailerRecycler.setLayoutManager(lmH);
        trailerRecycler.setAdapter(trailerAdapter);

        LinearLayoutManager lmV = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        reviewAdapter = new ReviewAdapter(getContext());
        reviewRecycler.setLayoutManager(lmV);
        reviewRecycler.setAdapter(reviewAdapter);


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, String.valueOf(movieCurrent == null));
        Log.d(TAG, String.valueOf(view == null));
        Log.d(TAG, "onViewCreated");
        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_KEY)){
            //do nothing
        }else{
            movieCurrent = savedInstanceState.getParcelable(MOVIE_KEY);
        }

        if (view != null && movieCurrent != null) {
            loadMovie(movieCurrent);
        }
    }

    public void loadMovie(Movie movie) {
        Log.d(TAG, String.valueOf(movie.getId()));
        this.movieCurrent = movie;

        Log.d(TAG, movie.getOriginalTitle());
        movieSynopsis.setText(movieCurrent.getOverview());
        movieRating.setText(String.valueOf(movie.getVoteAverage()));
        movieTitle.setText(movie.getTitle());
        movieRelease.setText(movie.getReleaseDate());

        Log.d(TAG, "loadMovie called");
        Picasso.with(getActivity())
                .load(Urls.TMDB_BACKDROP_IMG_URL + movie.getBackdropPath())
                .into(poster);

        Picasso.with(getActivity())
                .load(Urls.TMDB_POSTER_IMG_URL + movie.getPosterPath())
                .into(detailMovie);

        sendTrailerRequest(String.valueOf(movie.getId()));
        sendReviewRequest(String.valueOf(movie.getId()));


        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!new insertFavourite().execute(movieCurrent).get()){
                        Toast.makeText(getActivity(), movieCurrent.getTitle() + " removed from favourites", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), movieCurrent.getTitle() + " added to favourites", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movieCurrent != null){
            outState.putParcelable(MOVIE_KEY, movieCurrent);
        }
        super.onSaveInstanceState(outState);
    }

    public void update(Movie movie) {
        this.movieCurrent = movie;
    }


    private void sendTrailerRequest(String id) {
        PopularMoviesService service = retrofit.create(PopularMoviesService.class);
        Call<Trailer> trailerCall = service.listTrailers(id, MovieApiKey.ApiKey);
        Log.d(TAG, "Request is: " + trailerCall.request().url());
        trailerCall.enqueue(new Callback<Trailer>() {
            @Override
            public void onResponse(Call<Trailer> call, Response<Trailer> response) {
                if (response.isSuccessful()) {
                    ArrayList<ResultTrailer> trailers = (ArrayList<ResultTrailer>) response.body().getResults();
                    trailerAdapter.setTrailers(trailers);
                } else {
                    //something went wrong;
                }
            }

            @Override
            public void onFailure(Call<Trailer> call, Throwable t) {

            }
        });
    }

    private void sendReviewRequest(String id) {
        PopularMoviesService service = retrofit.create(PopularMoviesService.class);
        Call<Review> reviewCall = service.listReviews(id, MovieApiKey.ApiKey);
        Log.d(TAG, "Request is: " + reviewCall.request().url());
        reviewCall.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    ArrayList<ResultReviews> reviews = (ArrayList<ResultReviews>) response.body().getResults();
                    reviewAdapter.setReviews(reviews);
                } else {
                    //Something went wrong??!?
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {

            }
        });
    }

    public class insertFavourite extends AsyncTask<Movie, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Movie... params) {
            Favourite fav = new Favourite();

            fav.setId(movieCurrent.getId());
            fav.setBackdropPath(movieCurrent.getBackdropPath());
            fav.setPopularity(movieCurrent.getPopularity());
            fav.setVoteCount(movieCurrent.getVoteCount());
            fav.setVoteAverage(movieCurrent.getVoteAverage());
            fav.setAdult(movieCurrent.isAdult());
            fav.setOverview(movieCurrent.getOverview());
            fav.setOriginalLanguage(movieCurrent.getOriginalLanguage());
            fav.setPosterPath(movieCurrent.getPosterPath());
            fav.setOriginalTitle(movieCurrent.getOriginalTitle());
            fav.setReleaseDate(movieCurrent.getReleaseDate());
            fav.setTitle(movieCurrent.getTitle());
            if (fav.exists()) {
                fav.delete();
                return false;
            }
            fav.insert();
            fav.save();

            return true;

        }
    }

}