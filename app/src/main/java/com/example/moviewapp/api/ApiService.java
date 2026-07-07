package com.example.moviewapp.api;

import com.example.moviewapp.model.MovieResponse;
import com.example.moviewapp.model.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface ApiService {

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );
    @GET("discover/movie")
    Call<MovieResponse> discoverMovies(
            @Query("api_key") String apiKey,
            @Query("with_genres") String genreId,
            @Query("primary_release_year") String year,
            @Query("vote_average.gte") String rating
    );

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );
}