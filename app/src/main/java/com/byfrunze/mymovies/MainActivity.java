package com.byfrunze.mymovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.byfrunze.mymovies.data.Movie;
import com.byfrunze.mymovies.utils.JSONUtils;
import com.byfrunze.mymovies.utils.NetWorkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONObject jsonObject = NetWorkUtils.getJSONFromNetWork(NetWorkUtils.TOP_RATED, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        Log.i("OBJ", jsonObject.toString());
        Log.i("Arr", movies.toString() + " size " + movies.size());
        StringBuilder builder = new StringBuilder();
        for(Movie movie : movies){
            builder.append(movie.getTitle()).append("\n");
        }
        Log.i("RES", builder.toString());

    }
}
