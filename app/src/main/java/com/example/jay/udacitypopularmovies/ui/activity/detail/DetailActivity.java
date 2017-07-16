package com.example.jay.udacitypopularmovies.ui.activity.detail;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.jay.udacitypopularmovies.dbandmodels.Movie;
import com.example.jay.udacitypopularmovies.R;
import com.example.jay.udacitypopularmovies.ui.fragment.detailsfragment.DetailFragment;


public class DetailActivity extends AppCompatActivity  {


    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    public static final String TAG = DetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie movie = getIntent().getParcelableExtra("movie");
        loadMovieDetailsFragment(movie);

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(movie.getTitle());
    }

    private void loadMovieDetailsFragment(Movie movie){
        Bundle detailsBundle = new Bundle();
        detailsBundle.putParcelable(DetailFragment.MOVIE_KEY, movie);

        Fragment detailsFragment = new DetailFragment();
        detailsFragment.setArguments(detailsBundle);

        FragmentTransaction detailsTransaction = getSupportFragmentManager().beginTransaction();
        detailsTransaction.replace(R.id.details_container, detailsFragment);
        detailsTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        detailsTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.menu_detail);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}