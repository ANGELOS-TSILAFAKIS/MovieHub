package com.etiennelawlor.moviehub.data.source.movies;

import android.content.Context;

import com.etiennelawlor.moviehub.data.remote.AuthorizedNetworkInterceptor;
import com.etiennelawlor.moviehub.data.remote.MovieHubService;
import com.etiennelawlor.moviehub.data.remote.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by etiennelawlor on 2/13/17.
 */

public class MoviesRemoteDataSource implements MoviesDataSource {

    // region Member Variables
    private MovieHubService movieHubService;
    private List<Call> calls;
    // endregion

    // region Constructors
    public MoviesRemoteDataSource(Context context) {
        movieHubService = ServiceGenerator.createService(
                MovieHubService.class,
                MovieHubService.BASE_URL,
                new AuthorizedNetworkInterceptor(context));
//        MovieHubApplication.getInstance().getApplicationContext();
    }
    // endregion

    // region MoviesDataSource Methods
    @Override
    public void getMovies(int currentPage, final GetMoviesCallback<?> callback) {
        Call getPopularMoviesCall = movieHubService.getPopularMovies(currentPage);
        calls.add(getPopularMoviesCall);
        getPopularMoviesCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                callback.onSuccess(null,1);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onError(t,0);
            }
        });
    }

    @Override
    public void loadFirstPage(GetMoviesCallback<?> callback) {

    }

    @Override
    public void loadNextPage(GetMoviesCallback<?> callback) {

    }
    // endregion
}
