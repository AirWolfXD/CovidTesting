package com.starhacks.team2.covidwellnesstracker;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.here.sdk.core.CustomMetadataValue;
import com.here.sdk.core.GeoBox;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.search.Address;
import com.here.sdk.search.Place;
import com.here.sdk.search.SearchCallback;
import com.here.sdk.search.SearchEngine;
import com.here.sdk.search.SearchError;
import com.here.sdk.search.SearchOptions;
import com.here.sdk.search.TextQuery;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    SearchEngine searchEngine;
    MapMarker topmostMapMarker;

    PlatformPositioningProvider platformPositioningProvider;
    Context context;

    String name;
    Address address;
    int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        context = getApplicationContext();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                1);

        platformPositioningProvider = new PlatformPositioningProvider(context);
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                try {
                    GeoBox viewportGeoBox = new GeoBox(new GeoCoordinates(location.getLatitude() - 0.25, location.getLongitude() - 0.25), new GeoCoordinates(location.getLatitude() + 0.25, location.getLongitude() + 0.25));
                    searchEngine = new SearchEngine();
                    searchEngine.search(new TextQuery("Covid", viewportGeoBox), new SearchOptions(LanguageCode.EN_US, 10), new SearchCallback() {
                        @Override
                        public void onSearchCompleted(@Nullable SearchError searchError, @Nullable List<Place> list) {
                            if(searchError != null){
                                return;
                            }
                            for(Place searchResult : list){
                                Metadata metadata = new Metadata();
                                metadata.setCustomValue("key_search_result", new SearchResultMetadata(searchResult));
                                name = searchResult.getTitle();
                                address = searchResult.getAddress();
                                distance = searchResult.getDistanceInMeters();
                                System.out.println("Here");
                            }
                            return;
                        }
                    });
                } catch (InstantiationErrorException e) {
                    throw new RuntimeException("Initialization of SearchEngine failed: " + e.error.name());
                }
            }
        });
    }

    private static class SearchResultMetadata implements CustomMetadataValue {

        public final Place searchResult;

        public SearchResultMetadata(Place searchResult) {
            this.searchResult = searchResult;
        }

        @NonNull
        @Override
        public String getTag() {
            return "SearchResult Metadata";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
