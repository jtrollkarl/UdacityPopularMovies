package com.example.jay.udacitypopularmovies;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.jay.udacitypopularmovies.apikey.MovieApiKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 */
public class RefreshMovies extends IntentService {

    private static final String TAG = RefreshMovies.class.getSimpleName();
    public static final String ACTION_APP_START = "com.example.jay.udacitypopularmovies.action.APPLICATION_START";
    public static final String ACTION_SWITCH_POPULAR = "com.example.jay.udacitypopularmovies.action.SWITCH_POPULAR";
    public static final String ACTION_SWTICH_TOP_RATED = "com.example.jay.udacitypopularmovies.action.SWITCH_TOP_RATED";


    private Retrofit retrofit;

    public RefreshMovies() {
        super("RefreshMovies");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SWITCH_POPULAR.equals(action)) {

                UILoader.PROPERTY = Movie_Table.popularity;
                handleRefresh(Page.POPULAR);
            } else if (ACTION_APP_START.equals(action)) {

                UILoader.PROPERTY = Movie_Table.popularity;
                handleRefresh(Page.POPULAR);
            } else if (ACTION_SWTICH_TOP_RATED.equals(action)){
                UILoader.PROPERTY = Movie_Table.voteAverage;
                handleRefresh(Page.TOP_RATED);
            }
        }
    }


    private void handleRefresh(String action) {
        Log.d(TAG, "Starting app");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PopularMoviesService service = retrofit.create(PopularMoviesService.class);
        Call<Page> movies = service.listMovies(action, MovieApiKey.ApiKey);
        Log.d(TAG, "Request is: " + movies.request().url());
        //ArrayList<Movie> resultsList;



        try {
            Response<Page> response = movies.execute();
            if(response.errorBody() != null){
                Log.d(TAG, response.errorBody().string());
            }
            ArrayList<Movie> listofmovies = (ArrayList<Movie>) response.body().getResults();

            DatabaseStorageRetrieval.insert(this, listofmovies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}