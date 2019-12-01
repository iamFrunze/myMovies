package com.byfrunze.mymovies.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie extends Movie {
    public FavouriteMovie(int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String backDropPath, double voteAverage, String releaseDate, String bigPosterPath) {
        super(id, voteCount, title, originalTitle, overview, posterPath, backDropPath, voteAverage, releaseDate, bigPosterPath);
    }

    @Ignore
    public FavouriteMovie(Movie movie) {
        super(movie.getId(), movie.getVoteCount(), movie.getTitle(), movie.getOriginalTitle(),
                movie.getOverview(), movie.getPosterPath(), movie.getBackDropPath(), movie.getVoteAverage(),
                movie.getReleaseDate(), movie.getBigPosterPath());
    }
}
