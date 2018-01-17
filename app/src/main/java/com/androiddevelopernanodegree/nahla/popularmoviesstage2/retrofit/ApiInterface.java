package com.androiddevelopernanodegree.nahla.popularmoviesstage2.retrofit;

/**
 * Created by NahlaNabil on 3/18/2017.
 */

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieReviewsResponse;
import com.androiddevelopernanodegree.nahla.popularmoviesstage2.models.MovieTrailersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieResponse> getMostPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<MovieTrailersResponse> getMovieTrailers(@Path("id") String id, @Query("api_key") String apiKey) ;

    @GET("movie/{id}/reviews")
    Call<MovieReviewsResponse> getMovieReviews(@Path("id") String id, @Query("api_key") String apiKey) ;
}