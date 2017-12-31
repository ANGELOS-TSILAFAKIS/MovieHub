package com.etiennelawlor.moviehub.presentation.persons;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etiennelawlor.moviehub.MovieHubApplication;
import com.etiennelawlor.moviehub.R;
import com.etiennelawlor.moviehub.data.network.response.Person;
import com.etiennelawlor.moviehub.data.repositories.person.models.PersonsDataModel;
import com.etiennelawlor.moviehub.di.component.PersonsComponent;
import com.etiennelawlor.moviehub.di.module.PersonsModule;
import com.etiennelawlor.moviehub.presentation.base.BaseAdapter;
import com.etiennelawlor.moviehub.presentation.base.BaseFragment;
import com.etiennelawlor.moviehub.presentation.persondetails.PersonDetailsActivity;
import com.etiennelawlor.moviehub.util.FontCache;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by etiennelawlor on 12/16/16.
 */

public class PersonsFragment extends BaseFragment implements PersonsAdapter.OnItemClickListener, PersonsAdapter.OnReloadClickListener, PersonsUiContract.View {

    // region Constants
    public static final String KEY_PERSON = "KEY_PERSON";
    // endregion

    // region Views
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.error_ll)
    LinearLayout errorLinearLayout;
    @BindView(R.id.error_tv)
    TextView errorTextView;
    @BindView(R.id.pb)
    ProgressBar progressBar;
    @BindView(android.R.id.empty)
    LinearLayout emptyLinearLayout;

    private View selectedPersonView;
    // endregion

    // region Member Variables
    private PersonsAdapter personsAdapter;
    private Typeface font;
    private Unbinder unbinder;
    private StaggeredGridLayoutManager layoutManager;
    private PersonsDataModel personsDataModel;
    private PersonsComponent personsComponent;
    private boolean isLoading = false;
    // endregion

    // region Injected Variables
    @Inject
    PersonsUiContract.Presenter personsPresenter;
    // endregion

    // region Listeners
    @OnClick(R.id.reload_btn)
    public void onReloadButtonClicked() {
        personsPresenter.onLoadPopularPersons(personsDataModel == null ? 1 : personsDataModel.getPageNumber());
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            int[] positions = layoutManager.findFirstVisibleItemPositions(null);
            int firstVisibleItem = positions[1];

            if ((visibleItemCount + firstVisibleItem) >= totalItemCount
                    && totalItemCount > 0
                    && !isLoading
                    && !personsDataModel.isLastPage()) {
                personsPresenter.onScrollToEndOfList();
            }
        }
    };

    // endregion

    // region Constructors
    public PersonsFragment() {
    }
    // endregion

    // region Factory Methods
    public static PersonsFragment newInstance() {
        return new PersonsFragment();
    }

    public static PersonsFragment newInstance(Bundle extras) {
        PersonsFragment fragment = new PersonsFragment();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion

    // region Lifecycle Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createPersonsComponent().inject(this);

        font = FontCache.getTypeface("Lato-Medium.ttf", getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        personsAdapter = new PersonsAdapter(getContext());
        personsAdapter.setOnItemClickListener(this);
        personsAdapter.setOnReloadClickListener(this);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(personsAdapter);

        // Pagination
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        personsPresenter.onLoadPopularPersons(personsDataModel == null ? 1 : personsDataModel.getPageNumber());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeListeners();
        unbinder.unbind();
        personsPresenter.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releasePersonsComponent();
    }
    // endregion

    // region PersonsAdapter.OnItemClickListener Methods
    @Override
    public void onItemClick(int position, View view) {
        selectedPersonView = view;
        Person person = personsAdapter.getItem(position);
        if(person != null){
            personsPresenter.onPersonClick(person);
        }
    }
    // endregion

    // region PersonsAdapter.OnReloadClickListener Methods
    @Override
    public void onReloadClick() {
        personsPresenter.onLoadPopularPersons(personsDataModel.getPageNumber());
    }
    // endregion

    // region PersonsUiContract.View Methods

    @Override
    public void showEmptyView() {
        emptyLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        emptyLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showErrorView() {
        errorLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideErrorView() {
        errorLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void setErrorText(String errorText) {
        errorTextView.setText(errorText);
    }

    @Override
    public void showLoadingView() {
        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;
    }

    @Override
    public void hideLoadingView() {
        progressBar.setVisibility(View.GONE);
        isLoading = false;
    }

    @Override
    public void addHeader() {
        personsAdapter.addHeader();
    }

    @Override
    public void addFooter() {
        personsAdapter.addFooter();
    }

    @Override
    public void removeFooter() {
        personsAdapter.removeFooter();
        isLoading = false;
    }

    @Override
    public void showErrorFooter() {
        personsAdapter.updateFooter(BaseAdapter.FooterType.ERROR);
    }

    @Override
    public void showLoadingFooter() {
        personsAdapter.updateFooter(BaseAdapter.FooterType.LOAD_MORE);
        isLoading = true;
    }

    @Override
    public void addPersonsToAdapter(List<Person> persons) {
        personsAdapter.addAll(persons);
    }

    @Override
    public void loadMoreItems() {
        personsDataModel.incrementPageNumber();
        personsPresenter.onLoadPopularPersons(personsDataModel.getPageNumber());
    }

    @Override
    public void setPersonsDataModel(PersonsDataModel personsDataModel) {
        this.personsDataModel = personsDataModel;
    }

    @Override
    public void openPersonDetails(Person person) {
        Intent intent = new Intent(getActivity(), PersonDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PERSON, person);
        intent.putExtras(bundle);

        Window window = getActivity().getWindow();
//            window.setStatusBarColor(primaryDark);

        Resources resources = selectedPersonView.getResources();
        Pair<View, String> personPair  = getPair(selectedPersonView, resources.getString(R.string.transition_person_thumbnail));

        ActivityOptionsCompat options = getActivityOptionsCompat(personPair);

        window.setExitTransition(null);
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    // endregion

    // region Helper Methods
    private void removeListeners() {
        personsAdapter.setOnItemClickListener(null);
    }

    private ActivityOptionsCompat getActivityOptionsCompat(Pair pair){
        ActivityOptionsCompat options = null;

        Pair<View, String> bottomNavigationViewPair = getBottomNavigationViewPair();
        Pair<View, String> statusBarPair = getStatusBarPair();
        Pair<View, String> navigationBarPair  = getNavigationBarPair();
        Pair<View, String> appBarPair  = getAppBarPair();

        if(pair!=null
                && bottomNavigationViewPair != null
                && statusBarPair!= null
                && navigationBarPair!= null
                && appBarPair!= null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, bottomNavigationViewPair, statusBarPair, navigationBarPair, appBarPair);
        } else if(pair != null
                && bottomNavigationViewPair != null
                && statusBarPair != null
                && appBarPair!= null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, bottomNavigationViewPair, statusBarPair, appBarPair);
        } else if(pair != null
                && bottomNavigationViewPair != null
                && navigationBarPair != null
                && appBarPair!= null){
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    pair, bottomNavigationViewPair, navigationBarPair, appBarPair);
        }

        return options;
    }

    private Pair<View, String> getPair(View view, String transition){
        Pair<View, String> posterImagePair = null;
        View posterImageView = ButterKnife.findById(view, R.id.thumbnail_iv);
        if(posterImageView != null){
            posterImagePair = Pair.create(posterImageView, transition);
        }

        return posterImagePair;
    }

    private Pair<View, String> getBottomNavigationViewPair(){
        Pair<View, String> pair = null;
        View bottomNavigationView = ButterKnife.findById(getActivity(), R.id.bottom_navigation);
        if(bottomNavigationView != null) {
            Resources resources = bottomNavigationView.getResources();
            pair = Pair.create(bottomNavigationView, resources.getString(R.string.transition_bottom_navigation));
        }
        return pair;
    }

    private Pair<View, String> getStatusBarPair(){
        Pair<View, String> pair = null;
        View statusBar = ButterKnife.findById(getActivity(), android.R.id.statusBarBackground);
        if(statusBar != null)
            pair = Pair.create(statusBar, statusBar.getTransitionName());
        return pair;
    }

    private Pair<View, String> getNavigationBarPair(){
        Pair<View, String> pair = null;
        View navigationBar = ButterKnife.findById(getActivity(), android.R.id.navigationBarBackground);
        if(navigationBar != null)
            pair = Pair.create(navigationBar, navigationBar.getTransitionName());
        return pair;
    }

    private Pair<View, String> getAppBarPair(){
        Pair<View, String> pair = null;
        View appBar = ButterKnife.findById(getActivity(), R.id.appbar);
        if(appBar != null) {
            Resources resources = appBar.getResources();
            pair = Pair.create(appBar, resources.getString(R.string.transition_app_bar));
        }
        return pair;
    }

    public void scrollToTop(){
        recyclerView.scrollToPosition(0);
    }

    private PersonsComponent createPersonsComponent(){
        personsComponent = ((MovieHubApplication)getActivity().getApplication())
                .getApplicationComponent()
                .createSubcomponent(new PersonsModule(this));
        return personsComponent;
    }

    public void releasePersonsComponent(){
        personsComponent = null;
    }
    // endregion
}
