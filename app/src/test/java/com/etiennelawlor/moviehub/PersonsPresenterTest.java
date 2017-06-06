package com.etiennelawlor.moviehub;

import com.etiennelawlor.moviehub.data.model.PersonsPage;
import com.etiennelawlor.moviehub.data.remote.response.Person;
import com.etiennelawlor.moviehub.data.source.person.PersonDataSourceContract;
import com.etiennelawlor.moviehub.ui.persons.PersonsPresenter;
import com.etiennelawlor.moviehub.ui.persons.PersonsUiContract;
import com.etiennelawlor.moviehub.util.rxjava.TestSchedulerTransformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by etiennelawlor on 2/9/17.
 */

public class PersonsPresenterTest {

    // region Test Doubles

    // Mocks
    @Mock
    private PersonsUiContract.View mockPersonsView;
    @Mock
    private PersonDataSourceContract.Repository mockPersonRepository;

    // Stubs
    private Observable stub;

    // endregion

    // region Member Variables
    private PersonsPage personsPage;
    private PersonsPresenter personsPresenter;
    // endregion

    @Before
    public void setUp() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        personsPresenter = new PersonsPresenter(mockPersonsView, mockPersonRepository, new TestSchedulerTransformer<PersonsPage>());
    }

    // region Test Methods
//    @Test(expected = IOException.class)
    @Test
    public void onLoadPopularPersons_shouldShowError_whenFirstPageRequestFailed() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(0), 1, true, Calendar.getInstance().getTime());
        stub = Observable.<PersonsPage>error(new IOException());
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).hideEmptyView();
        verify(mockPersonsView).hideErrorView();
        verify(mockPersonsView).showLoadingView();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).hideLoadingView();
        verify(mockPersonsView).setErrorText(anyString());
        verify(mockPersonsView).showErrorView();
    }

    @Test
    public void onLoadPopularPersons_shouldShowError_whenNextPageRequestFailed() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(0), 2, true, Calendar.getInstance().getTime());
        stub = Observable.<PersonsPage>error(new IOException());
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).showLoadingFooter();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).showErrorFooter();
    }

    @Test
    public void onLoadPopularPersons_shouldShowEmpty_whenFirstPageHasNoPersons() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(0), 1, true, Calendar.getInstance().getTime());
        stub = Observable.just(personsPage);
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).hideEmptyView();
        verify(mockPersonsView).hideErrorView();
        verify(mockPersonsView).showLoadingView();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).hideLoadingView();
        verify(mockPersonsView).showEmptyView();
        verify(mockPersonsView).setPersonsPage(personsPage);
    }

    @Test
    public void onLoadPopularPersons_shouldNotAddPersons_whenNextPageHasNoPersons() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(0), 2, true, Calendar.getInstance().getTime());
        stub = Observable.just(personsPage);
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).showLoadingFooter();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).removeFooter();
        verify(mockPersonsView).setPersonsPage(personsPage);
    }

    @Test
    public void onLoadPopularPersons_shouldAddPersons_whenFirstPageHasPersonsAndIsLastPage() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(5), 1, true, Calendar.getInstance().getTime());
        stub = Observable.just(personsPage);
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).hideEmptyView();
        verify(mockPersonsView).hideErrorView();
        verify(mockPersonsView).showLoadingView();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).hideLoadingView();
        verify(mockPersonsView).addHeader();
        verify(mockPersonsView).addPersonsToAdapter(personsPage.getPersons());
        verify(mockPersonsView).setPersonsPage(personsPage);
    }

    @Test
    public void onLoadPopularPersons_shouldAddPersons_whenFirstPageHasPersonsAndIsNotLastPage() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(5), 1, false, Calendar.getInstance().getTime());
        stub = Observable.just(personsPage);
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).hideEmptyView();
        verify(mockPersonsView).hideErrorView();
        verify(mockPersonsView).showLoadingView();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).hideLoadingView();
        verify(mockPersonsView).addHeader();
        verify(mockPersonsView).addPersonsToAdapter(personsPage.getPersons());
        verify(mockPersonsView).addFooter();
        verify(mockPersonsView).setPersonsPage(personsPage);
    }

    @Test
    public void onLoadPopularPersons_shouldAddPersons_whenNextPageHasPersonsAndIsLastPage() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(5), 2, true, Calendar.getInstance().getTime());
        stub = Observable.just(personsPage);
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).showLoadingFooter();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).removeFooter();
        verify(mockPersonsView).addPersonsToAdapter(personsPage.getPersons());
        verify(mockPersonsView).setPersonsPage(personsPage);
    }

    @Test
    public void onLoadPopularPersons_shouldAddPersons_whenNextPageHasPersonsAndIsNotLastPage() {
        // 1. (Given) Set up conditions required for the test
        personsPage = new PersonsPage(getListOfPersons(5), 2, false, Calendar.getInstance().getTime());
        stub = Observable.just(personsPage);
        when(mockPersonRepository.getPopularPersons(anyInt())).thenReturn(stub);

        // 2. (When) Then perform one or more actions
        personsPresenter.onLoadPopularPersons(personsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).showLoadingFooter();

        verify(mockPersonRepository).getPopularPersons(anyInt());

        verify(mockPersonsView).removeFooter();
        verify(mockPersonsView).addPersonsToAdapter(personsPage.getPersons());
        verify(mockPersonsView).addFooter();
        verify(mockPersonsView).setPersonsPage(personsPage);
//        verify(mockPersonsView, times(1)).setModel(any(PersonsWrapper.class)); // Alternative verify check
    }

    @Test
    public void onPersonClick_shouldOpenPersonDetails() {
        // 1. (Given) Set up conditions required for the test
        Person person = new Person();

        // 2. (When) Then perform one or more actions
        personsPresenter.onPersonClick(person);

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).openPersonDetails(person);

        verifyZeroInteractions(mockPersonRepository);
    }

    @Test
    public void onScrollToEndOfList_shouldLoadMoreItems() {
        // 1. (Given) Set up conditions required for the test

        // 2. (When) Then perform one or more actions
        personsPresenter.onScrollToEndOfList();

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockPersonsView).loadMoreItems();

        verifyZeroInteractions(mockPersonRepository);
    }

    @Test
    public void onDestroyView_shouldNotHaveInteractions() {
        // 1. (Given) Set up conditions required for the test

        // 2. (When) Then perform one or more actions
        personsPresenter.onDestroyView();
        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verifyZeroInteractions(mockPersonsView);
        verifyZeroInteractions(mockPersonRepository);
    }

    // endregion

    // region Helper Methods
    private List<Person> getListOfPersons(int numOfPersons){
        List<Person> persons = new ArrayList<>();
        for(int i=0; i<numOfPersons; i++){
            Person person = new Person();
            persons.add(person);
        }
        return persons;
    }
    // endregion
}
