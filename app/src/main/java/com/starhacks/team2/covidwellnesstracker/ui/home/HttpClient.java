package com.starhacks.team2.covidwellnesstracker.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpClient {

    /**
     * default initial timeout (in milliseconds)
     */
    private final int DEFAULT_INITIAL_TIMEOUT = 15000;  // 15 seconds
    private final int DEFAULT_MAX_RETRIES = 3;          // up to 3 retries
    private final float DEFAULT_BACKOFF_MULT = 1.0f;

    private static HttpClient mInstance;
    private RequestQueue mRequestQueue;

    private HttpClient(@NonNull final Context context) {
        mRequestQueue = getRequestQueue(context);
    }

    static synchronized HttpClient getInstance(@NonNull final Context context) {
        if (mInstance == null) {
            mInstance = new HttpClient(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue(@NonNull final Context context) {
        if (mRequestQueue == null) {
            // NOTE: must be application context
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addRequest(@NonNull final Context context, @NonNull final Request<T> request, @NonNull final String tag) {
        request.setTag(tag);
        addRequest(context, request);
    }

    public <T> void addRequest(@NonNull final Context context, @NonNull final Request<T> request) {
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DEFAULT_INITIAL_TIMEOUT,
                        DEFAULT_MAX_RETRIES,
                        DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(request);
    }
}