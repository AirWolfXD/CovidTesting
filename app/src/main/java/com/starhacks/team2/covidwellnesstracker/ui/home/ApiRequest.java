package com.starhacks.team2.covidwellnesstracker.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;;
import com.starhacks.team2.covidwellnesstracker.ui.home.objects.Countries;
import com.starhacks.team2.covidwellnesstracker.ui.home.objects.Global;
import com.starhacks.team2.covidwellnesstracker.ui.home.objects.Results;

public class ApiRequest {
    private static final Gson mGson = new Gson();

    public static void getGlobal(@NonNull final Context context,
                                 @NonNull final Results results,
                                 @NonNull final Response.Listener<Results> listener,
                                 @NonNull final Response.ErrorListener errorListener){
        final String url = "https://api.covid19api.com/summary";
        final HttpClient httpClient = HttpClient.getInstance(context);
        final GsonRequest request = new GsonRequest(
                Request.Method.GET, url, mGson, Results.class, null, listener, errorListener);
        httpClient.addRequest(context.getApplicationContext(), request);
    }
}
