package tourguide.android.example.com.newsapplist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener

{
    private NewsListAdapter listAdapter;
    private SwipeRefreshLayout swipeContainer;
    private TextView errorMessage;
    private TextView connectingMessage;

    private static final String ORDER_BY_QUERY_PARAM = "orderby";
    public static final String PREFERENCE_INFO = "preference_info";
    private static final String ORDER_DATE_QUERY_PARAM = "orderdate";
    private static final String ORDER_BY_PREFERENCE_KEY = "orderby_preference";
    private static final String ORDER_DATE_PREFERENCE_KEY = "orderdate_preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //error messages will be show in this textview
        errorMessage = (TextView) findViewById(R.id.error_message);

        //initial message shown during loading of the app
        connectingMessage = (TextView) findViewById(R.id.connecting_message);
        ListView newsListView = (ListView) findViewById(R.id.news_list_view);
        listAdapter = new NewsListAdapter(this);
        newsListView.setAdapter(listAdapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.newsRefreshContainer);

        // swipe refresh control will invoke onrefresh when swipe action is performed by the user
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                errorMessage.setVisibility(View.INVISIBLE);
                initLoader();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLoader();
    }

    /**
     * This function creates the Loader object that performs the server requests
     * This function also sets up loaderCallBack events
     */
    private void initLoader() {
        SharedPreferences orderByPreference = this.getSharedPreferences(ORDER_BY_PREFERENCE_KEY, MODE_PRIVATE);
        String orderBy = orderByPreference.getString(ORDER_BY_PREFERENCE_KEY, this.getResources().getString(R.string.default_order_by));
        SharedPreferences orderDatePreference = this.getSharedPreferences(ORDER_DATE_PREFERENCE_KEY, MODE_PRIVATE);
        String orderDate = orderDatePreference.getString(ORDER_DATE_PREFERENCE_KEY, this.getResources().getString(R.string.default_order_date));
        // Create a bundle called queryBundle
        Bundle queryBundle = new Bundle();
        // Call getSupportLoaderManager and store it in a LoaderManager variable
        queryBundle.putString(ORDER_BY_QUERY_PARAM, orderBy);
        queryBundle.putString(ORDER_DATE_QUERY_PARAM, orderDate);
        LoaderManager loaderManager = getSupportLoaderManager();
        // Get our Loader by calling getLoader and passing the ID we specified
        int FETCH_NEWS_LOADER = 1313;
        Loader<String> loader = loaderManager.getLoader(FETCH_NEWS_LOADER);

        // If the Loader was null, initialize it. Else, restart it.
        LoaderManager.LoaderCallbacks<NewsItemsResponse<NewsItem>> loaderCallbacks = new LoaderManager.LoaderCallbacks<NewsItemsResponse<NewsItem>>() {
            @NonNull
            @Override
            public Loader<NewsItemsResponse<NewsItem>> onCreateLoader(int id, @Nullable Bundle args) {
                return new LoadAsyncTask(MainActivity.this, args);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<NewsItemsResponse<NewsItem>> loader, NewsItemsResponse<NewsItem> data) {
                connectingMessage.setVisibility(View.INVISIBLE);
                if (data.getError() != null) {
                    MainActivity.this.handleRequestFailure(data.getError());
                    //whoops
                    return;
                } else {
                    errorMessage.setVisibility(View.INVISIBLE);
                    MainActivity.this.listAdapter.clear(); //this won't work with pagination
                    MainActivity.this.listAdapter.addNextPageToView(data.getResult(), data.getResultsInfo(), data.getPreferenceData());
                    MainActivity.this.listAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<NewsItemsResponse<NewsItem>> loader) {
            }
        };
        if (loader == null) {
            loaderManager.initLoader(FETCH_NEWS_LOADER, queryBundle, loaderCallbacks);
        } else {
            loaderManager.restartLoader(FETCH_NEWS_LOADER, queryBundle, loaderCallbacks);
        }
    }

    /*
     * Error response handling function
     * Error message text box is shown
     */
    private void handleRequestFailure(Exception error) {
        if (error instanceof UnknownHostException) {
            errorMessage.setText(R.string.no_connection_error);
        } else if (error instanceof NetworkUtils.NewsItemFetchException) {
            errorMessage.setText(error.getMessage());
        } else {
            errorMessage.setText(error.getMessage());
        }
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }


    /**
     * @param <T> Type of the list of items that are generated as response
     */
    public static class NewsItemsResponse<T> {
        //Server response with newsItems
        private final List<T> result;

        //Field that carries the exception from NetworkUtils
        private final Exception error;

        private final ResultsInfo resultsInfo;
        private final PreferencesData preferenceData;

        NewsItemsResponse(List<T> result, ResultsInfo resultsInfo, PreferencesData preferencesData, Exception error) {
            this.result = result;
            this.resultsInfo = resultsInfo;
            this.preferenceData = preferencesData;
            this.error = error;
        }

        public List<T> getResult() {
            return result;
        }

        public Exception getError() {
            return error;
        }

        public ResultsInfo getResultsInfo() {
            return resultsInfo;
        }

        public PreferencesData getPreferenceData() {
            return preferenceData;
        }
    }

    /**
     * Asynchronous network events loader
     * Uses NetworkUtils to perform HTTP Requests
     */
    private static class LoadAsyncTask extends AsyncTaskLoader<NewsItemsResponse<NewsItem>> {
        private final Bundle args;
        private NetworkUtils datastore;

        LoadAsyncTask(@NonNull Context context, Bundle args) {
            super(context);
            this.args = args;
            this.datastore = new NetworkUtils(args.getString(ORDER_BY_QUERY_PARAM), args.getString(ORDER_DATE_QUERY_PARAM));
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public NewsItemsResponse<NewsItem> loadInBackground() {
            if (datastore.hasMorePages()) {
                return datastore.getNextPage();
            }
            return new MainActivity.NewsItemsResponse<>(new ArrayList<NewsItem>(), null, null, null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            ResultsInfo resultsInfo = MainActivity.this.listAdapter.getResultsInfo();
            PreferencesData preferencesData = MainActivity.this.listAdapter.getPreferencesData();
            if (resultsInfo != null) {
                settingsIntent.putExtra(ORDER_BY_QUERY_PARAM, resultsInfo.getOrderBy());
                settingsIntent.putExtra(PREFERENCE_INFO, preferencesData);
            }
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
