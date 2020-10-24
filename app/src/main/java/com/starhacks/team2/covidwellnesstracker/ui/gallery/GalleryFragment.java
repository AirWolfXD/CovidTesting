package com.starhacks.team2.covidwellnesstracker.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.CustomMetadataValue;
import com.here.sdk.core.GeoBox;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PickMapItemsCallback;
import com.here.sdk.mapviewlite.PickMapItemsResult;
import com.here.sdk.search.Address;
import com.here.sdk.search.Details;
import com.here.sdk.search.Place;
import com.here.sdk.search.SearchCallback;
import com.here.sdk.search.SearchEngine;
import com.here.sdk.search.SearchError;
import com.here.sdk.search.SearchOptions;
import com.here.sdk.search.TextQuery;
import com.starhacks.team2.covidwellnesstracker.MainActivity;
import com.starhacks.team2.covidwellnesstracker.PlatformPositioningProvider;
import com.starhacks.team2.covidwellnesstracker.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    SearchEngine searchEngine;
    MapMarker topmostMapMarker;

    PlatformPositioningProvider platformPositioningProvider;
    Context context;

    String name;
    Address address;
    int distance;
    Details details;

    private Activity mActivity;

    private MapViewLite mapView;

    private final static String TAG = "TAG";

    Location currentPosition;

    private final List<MapMarker> mapMarkerList = new ArrayList<>();

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mActivity = (Activity) activity;
        } catch(ClassCastException e) {}
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        context = mActivity.getApplicationContext();

        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        loadMapScene();

        platformPositioningProvider = new PlatformPositioningProvider(context);
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                currentPosition = location;
                try {
                    GeoBox viewportGeoBox = new GeoBox(new GeoCoordinates(location.getLatitude() - 0.25, location.getLongitude() - 0.25), new GeoCoordinates(location.getLatitude() + 0.25, location.getLongitude() + 0.25));
                    searchEngine = new SearchEngine();
                    searchEngine.search(new TextQuery("Covid", viewportGeoBox), new SearchOptions(LanguageCode.EN_US, 25), new SearchCallback() {
                        @Override
                        public void onSearchCompleted(@Nullable SearchError searchError, @Nullable List<Place> list) {
                            if(searchError != null){
                                return;
                            }
                            for(Place searchResult : list){
                                Metadata metadata = new Metadata();
                                metadata.setCustomValue("key_search_result", new SearchResultMetadata(searchResult));
                                System.out.println("Here");
                                addPoiMapMarker(searchResult.getGeoCoordinates(), metadata);
                            }
                            return;
                        }
                    });
                } catch (InstantiationErrorException e) {
                    throw new RuntimeException("Initialization of SearchEngine failed: " + e.error.name());
                }
            }
        });


        return root;
    }



    private void clearMap() {
        for (MapMarker mapMarker : mapMarkerList) {
            mapView.getMapScene().removeMapMarker(mapMarker);
        }
        mapMarkerList.clear();
    }

    private void addPoiMapMarker(GeoCoordinates geoCoordinates, Metadata metadata) {
        MapMarker mapMarker = createPoiMapMarker(geoCoordinates);
        mapMarker.setMetadata(metadata);
        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private MapMarker createPoiMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.poi);
        MapMarker mapMarker = new MapMarker(geoCoordinates);
        MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
        mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5F, 1));
        mapMarker.addImage(mapImage, mapMarkerImageStyle);
        return mapMarker;
    }

    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {
                    mapView.getCamera().setTarget(new GeoCoordinates(currentPosition.getLatitude(), currentPosition.getLongitude()));
                    mapView.getCamera().setZoomLevel(10);
                } else {
                    Log.d(TAG, "onLoadScene failed: " + errorCode.toString());
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
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}