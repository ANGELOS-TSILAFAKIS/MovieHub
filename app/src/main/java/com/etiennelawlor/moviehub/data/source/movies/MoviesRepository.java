package com.etiennelawlor.moviehub.data.source.movies;

import com.etiennelawlor.moviehub.data.model.MoviesModel;
import com.etiennelawlor.moviehub.data.remote.response.Movie;
import com.etiennelawlor.moviehub.data.remote.response.MoviesEnvelope;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by etiennelawlor on 2/13/17.
 */

public class MoviesRepository implements MoviesDataSourceContract.Repository {

    // http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/
//    https://github.com/millionsun93/HackerNews/blob/bd94c62ac658eb3281879c8018540f6dc2c2ec3d/app/src/main/java/com/innovatube/boilerplate/data/HackerNewsRepositoryImpl.java

    // Uses mapper to go from POJO to RealmObject
    // https://github.com/ihorvitruk/buddysearch/blob/master/library/src/main/java/com/buddysearch/android/library/data/mapper/BaseMapper.java
    // https://github.com/dcampogiani/Qwertee/blob/f71dbc318264bcc05a7f51c8cb8c40e54b53b57e/data/src/main/java/com/danielecampogiani/qwertee/data/local/model/MapperImpl.java

    // region Constants
    private static final int PAGE_SIZE = 20;
    // endregion

    // region Member Variables
    private MoviesDataSourceContract.LocalDateSource moviesLocalDataSource;
    private MoviesDataSourceContract.RemoteDateSource moviesRemoteDataSource;
    // endregion

    // region Constructors
    // Additionally i need to pass in configRemoteDataSource as
    public MoviesRepository(MoviesDataSourceContract.LocalDateSource moviesLocalDataSource, MoviesDataSourceContract.RemoteDateSource moviesRemoteDataSource) {
        this.moviesLocalDataSource = moviesLocalDataSource;
        this.moviesRemoteDataSource = moviesRemoteDataSource;
    }
    // endregion

    // region MoviesDataSourceContract.Repository Methods
    @Override
    public Observable<MoviesModel> getPopularMovies(int currentPage) {
        return moviesRemoteDataSource.getPopularMovies(currentPage)
            .map(new Func1<MoviesEnvelope, MoviesModel>() {
                @Override
                public MoviesModel call(MoviesEnvelope moviesEnvelope) {
                    List<Movie> movies = moviesEnvelope.getMovies();
                    int currentPage = moviesEnvelope.getPage();
                    boolean isLastPage = moviesEnvelope.getMovies().size() < PAGE_SIZE ? true : false;

                    return new MoviesModel(movies, currentPage, isLastPage);
                }
            }).doOnNext(new Action1<MoviesModel>() {
                    @Override
                    public void call(MoviesModel moviesViewModel) {
                        // todo: update realm
                    }
                });
    }

//  Create an Observable that emits a particular item
//  Observable.just(List<Movie> movies)
//  Observable.just(MoviesModel movies)

//  Create an Observable that emits no items but terminates normally
//  Observable.empty();

    // endregion
}
