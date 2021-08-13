package com.example.earthquake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=1&limit=15";

    /** Adapter for the list of earthquakes */
    private detailsadapter mAdapter;

    /**text view for empty list view*/
    private TextView mEmptyStateTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        /*      ____ BEFORE __ USING __ JSON __ FORMAT __ USED __ THIS ____
        ArrayList <details> earthquakes = new ArrayList<>();

        earthquakes.add(new details("7.2", "San Francisco", "Feb 2, 2016"));
        earthquakes.add(new details("6.1", "London", "July 20, 2015"));
        earthquakes.add(new details("3.9", "Tokyo", "Nov 10, 2014"));
        earthquakes.add(new details("5.4", "Mexico City", "May 3, 2014"));
         */

//        final detailsadapter adapter = new detailsadapter(this, QueryUtils.extract());
        ListView listview = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.emptylist);
        listview.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new detailsadapter(this, new ArrayList<details>());

//        getLoaderManager().initLoader(0, null, this);

//        LoaderManager loaderManager = getLoaderManager();
//        loaderManager.initLoader(1, null, this);

        // Start the AsyncTask to fetch the earthquake data
        //    THIS IS FOR ASYNC TASK

        // checking internet connection
        if(isConnected) {
            /**if connected send request to server*/
            EarthquakeAsyncTask task = new EarthquakeAsyncTask();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String minMagnitude = sharedPrefs.getString(
                    getString(R.string.settings_min_magnitude_key),
                    getString(R.string.settings_min_magnitude_default));

            String orderBy = sharedPrefs.getString(
                    getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_default)
            );

            Uri baseUri = Uri.parse(USGS_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            uriBuilder.appendQueryParameter("format", "geojson");
            uriBuilder.appendQueryParameter("limit", "10");
            uriBuilder.appendQueryParameter("minmag", minMagnitude);
            uriBuilder.appendQueryParameter("orderby", orderBy);

            task.execute(uriBuilder.toString());
//            task.execute(USGS_REQUEST_URL);
        } else {
            /** if not connected to network*/

            // making the loading spinner invisible after the result of response from server
            ProgressBar spinner = (ProgressBar) findViewById(R.id.loading_spinner);
            spinner.setVisibility(View.GONE);

            // showing "no internet connection"
            mEmptyStateTextView.setText("NO INTERNET CONNECTION !!!");
        }

        listview.setAdapter(mAdapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                // after making earthquake arraylist static, we can also use this
//                // this method will directly extract url from arraylist
//                searchWeb(QueryUtils.earthquake.getItem(position).getUrl());
                details cureq = (details) mAdapter.getItem(position);
                searchWeb(cureq.getUrl());

            }
        });

    }

    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

//    @Override
//    public Loader<List<details>> onCreateLoader(int id, Bundle bundle) {
//        Log.v("main", "oncreateloader");
//        new EarthquakeAsyncTaskLoader(this, USGS_REQUEST_URL);
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<List<details>> loader, List<details> data) {
//        mAdapter.clear();
//        Log.v("main", "onloadfinished");
//        if(data != null && !data.isEmpty()){
//            mAdapter.addAll(data);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<List<details>> loader) {
//        Log.v("main", "onloaderreset");
//        mAdapter.clear();
//    }


    //    ASYNCTASK IS NOT SO MEOMERY EFFICIENT, THATS WHY WE AVOID USING IT
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<details>> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link details}s as the result.
         */

        @Override
        protected List<details> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<details> result = QueryUtils.fetchEarthquakeData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of earthquake data from a previous
         * query to USGS. Then we update the adapter with the new list of earthquakes,
         * which will trigger the ListView to re-populate its list items.
         */

        @Override
        protected void onPostExecute(List<details> data) {

            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {

                mAdapter.addAll(data);

                // making the loading spinner invisible after the result of response from server
                ProgressBar spinner = (ProgressBar) findViewById(R.id.loading_spinner);
                spinner.setVisibility(View.GONE);

                // If no data loaded then text "NO EARTHQUAKE FOUND" will be displayed
                mEmptyStateTextView.setText("NO EARTHQUAKE FOUND");

            }
        }
    }

//    private static class EarthquakeAsyncTaskLoader extends AsyncTaskLoader<List<details>> {
//        private String mUrls=null;
//
//        public EarthquakeAsyncTaskLoader(@NonNull Context context, String url) {
//            super(context);
//            mUrls=url;
//        }
//
//        protected void onStartLoading(){
//            Log.v("main", "onStartLoading\n");
//            forceLoad();
//        }
//
//        @Nullable
//        @Override
//        public List<details> loadInBackground() {
//            Log.v("main", "loadInBackground\n");
//            // Don't perform the request if there are no URLs, or the first URL is null.
//            if (mUrls == null) {
//                return null;
//            }
//
//            List<details> result = QueryUtils.fetchEarthquakeData(mUrls);
//            return result;
////            return null;
//        }
//    }

}