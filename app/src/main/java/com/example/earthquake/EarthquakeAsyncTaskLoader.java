package com.example.earthquake;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

// this made for LOADER but loader are no more available for this version of android
// instead of LOADER new concepts VIEWMODELS and LIVEDATA are there which is more meomery efficient
public class EarthquakeAsyncTaskLoader extends AsyncTaskLoader<List<details>> {

    private String mUrls;

    public EarthquakeAsyncTaskLoader(@NonNull Context context, String url) {
        super(context);
        mUrls=url;
    }

    protected void onStartLoading(){
        Log.v("main", "onStartLoading\n");
        forceLoad();
    }

    @Nullable
    @Override
    public List<details> loadInBackground() {
        Log.v("main", "loadInBackground\n");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrls == null) {
            return null;
        }

        List<details> result = QueryUtils.fetchEarthquakeData(mUrls);
        return result;
//            return null;
    }
}
