package com.starhacks.team2.covidwellnesstracker.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest<T> extends JsonRequest<T> {
    private final Gson gson;
    private final Class<T> responseClass;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;

    public GsonRequest(final int method, @NonNull final String url,
                       @NonNull final Gson gson, @NonNull final Class<T> responseClass,
                       @Nullable final Map<String, String> headers, @NonNull final String body,
                       @NonNull final Response.Listener<T> listener,
                       @NonNull final Response.ErrorListener errorListener) {
        super(method, url, body, listener, errorListener);

        if ((method != Method.POST) && (method != Method.PUT)) {
            throw new IllegalArgumentException("Parameter [method] must be POST or PUT!");
        }

        this.gson = gson;
        this.responseClass = responseClass;
        this.headers = headers;
        this.listener = listener;
    }

    public GsonRequest(final int method, @NonNull final String url,
                       @NonNull final Gson gson, @NonNull final Class<T> responseClass,
                       @Nullable final Map<String, String> headers,
                       @NonNull final Response.Listener<T> listener,
                       @NonNull final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);

        this.gson = gson;
        this.responseClass = responseClass;
        this.headers = headers;
        this.listener = listener;
    }

    public GsonRequest(@NonNull final String url,
                       @NonNull final Gson gson, @NonNull final Class<T> responseClass,
                       @Nullable final Map<String, String> headers,
                       @NonNull final Response.Listener<T> listener,
                       @NonNull final Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);

        this.gson = gson;
        this.responseClass = responseClass;
        this.headers = headers;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(gson.fromJson(json, responseClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}