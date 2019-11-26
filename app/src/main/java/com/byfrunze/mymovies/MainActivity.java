package com.byfrunze.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.byfrunze.mymovies.data.MainViewModel;
import com.byfrunze.mymovies.data.Movie;
import com.byfrunze.mymovies.utils.JSONUtils;
import com.byfrunze.mymovies.utils.NetWorkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewTop;
    private TextView textViewPop;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchSort = findViewById(R.id.switchSort);
        recyclerView = findViewById(R.id.recyclerView);
        textViewPop = findViewById(R.id.textViewPopularity);
        textViewTop = findViewById(R.id.textViewTopRatred);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

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
                Toast.makeText(MainActivity.this, "CLICK" + position, Toast.LENGTH_SHORT).show();
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "END",Toast.LENGTH_SHORT);
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

    private void setMethodpfSort(boolean isTopRated){
        int methodOfSort;
        if(isTopRated){
            textViewTop.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPop.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetWorkUtils.TOP_RATED;
        }
        else{
            textViewTop.setTextColor(getResources().getColor(R.color.white_color));
            textViewPop.setTextColor(getResources().getColor(R.color.colorAccent));
            methodOfSort = NetWorkUtils.POPULARITY;
        }
        downloadData(methodOfSort, 1);

    }
    private void downloadData(int methodOfSort, int page){
        JSONObject jsonObject = NetWorkUtils.getJSONFromNetWork(methodOfSort,page);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if(movies != null && !movies.isEmpty()){
            viewModel.deleteAllMovies();
            for(Movie movie : movies){
                viewModel.insertMovie(movie);
            }
        }

    }
}
