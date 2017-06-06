package com.etiennelawlor.moviehub.ui.persons;

import com.etiennelawlor.moviehub.data.model.PersonsPage;
import com.etiennelawlor.moviehub.data.remote.response.Person;
import com.etiennelawlor.moviehub.ui.base.BasePresenter;

import java.util.List;

/**
 * Created by etiennelawlor on 2/9/17.
 */

public interface PersonsUiContract {

    interface View {
        void showEmptyView();
        void hideEmptyView();
        void showErrorView();
        void hideErrorView();
        void setErrorText(String errorText);
        void showLoadingView();
        void hideLoadingView();
        void addHeader();
        void addFooter();
        void removeFooter();
        void showErrorFooter();
        void showLoadingFooter();
        void addPersonsToAdapter(List<Person> persons);
        void loadMoreItems();
        void setPersonsPage(PersonsPage personsPage);

        // Navigation methods
        void openPersonDetails(Person person);
    }

    interface Presenter extends BasePresenter {
        void onLoadPopularPersons(int currentPage);
        void onPersonClick(Person person);
        void onScrollToEndOfList();
    }
}
