package com.etiennelawlor.moviehub.presentation.moviedetails;

import com.etiennelawlor.moviehub.data.repositories.movie.models.MovieDetailsWrapper;
import com.etiennelawlor.moviehub.data.network.response.Movie;
import com.etiennelawlor.moviehub.data.network.response.Person;
import com.etiennelawlor.moviehub.presentation.base.BasePresenter;

/**
 * Created by etiennelawlor on 2/9/17.
 */

public interface MovieDetailsUiContract {

    interface View {
        void showMovieDetails(MovieDetailsWrapper movieDetailsWrapper);
        void showToolbarTitle();
        void hideToolbarTitle();
        void showErrorView();

        // Navigation methods
        void openPersonDetails(Person person);
        void openMovieDetails(Movie movie);
    }

    interface Presenter extends BasePresenter {
        void onLoadMovieDetails(int movieId);
        void onPersonClick(Person person);
        void onMovieClick(Movie movie);
        void onScrollChange(boolean isScrolledPastThreshold);
    }
}
