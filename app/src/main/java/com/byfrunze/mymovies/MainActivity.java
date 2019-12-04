package com.byfrunze.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.byfrunze.mymovies.adapters.MovieAdapter;
import com.byfrunze.mymovies.data.MainViewModel;
import com.byfrunze.mymovies.data.Movie;
import com.byfrunze.mymovies.utils.JSONUtils;
import com.byfrunze.mymovies.utils.NetWorkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<JSONObject> {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewTop;
    private TextView textViewPop;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 133;
    private LoaderManager loaderManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderManager = LoaderManager.getInstance(this);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        switchSort = findViewById(R.id.switchSort);
        recyclerView = findViewById(R.id.recyclerView);
        textViewPop = findViewById(R.id.textViewPopularity);
        textViewTop = findViewById(R.id.textViewTopRatred);


        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();

        recyclerView.setAdapter(movieAdapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMethodpfSort(isChecked);
            }
        });
        switchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DeteailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "END", Toast.LENGTH_SHORT);
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });
    }

    public void onClickSetPopullarity(View view) {
        setMethodpfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTop(View view) {
        setMethodpfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodpfSort(boolean isTopRated) {
        int methodOfSort;
        if (isTopRated) {
            textViewTop.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPop.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetWorkUtils.TOP_RATED;
        } else {
            textViewTop.setTextColor(getResources().getColor(R.color.white_color));
            textViewPop.setTextColor(getResources().getColor(R.color.colorAccent));
            methodOfSort = NetWorkUtils.POPULARITY;
        }
        downloadData(methodOfSort, 1);

    }

    private void downloadData(int methodOfSort, int page) {
        URL url = NetWorkUtils.buldURL(methodOfSort, page);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);


    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetWorkUtils.JSONLoader jsonLoader = new NetWorkUtils.JSONLoader(this, args);
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) {
            viewModel.deleteAllMovies();
            for (Movie movie : movies) {
                Log.i("MOV", movie.getTitle());
                viewModel.insertMovie(movie);
            }
        }
        loaderManager.destroyLoader(LOADER_ID);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}
