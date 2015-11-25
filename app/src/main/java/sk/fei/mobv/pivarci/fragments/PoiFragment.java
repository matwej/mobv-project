package sk.fei.mobv.pivarci.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.api.MyOverpassApi;
import sk.fei.mobv.pivarci.api.OverpassInt;
import sk.fei.mobv.pivarci.model.LocationItem;
import sk.fei.mobv.pivarci.services.BboxHolder;
import sk.fei.mobv.pivarci.services.RVAdapter;
import sk.fei.mobv.pivarci.settings.ComplexPreferences;
import sk.fei.mobv.pivarci.settings.General;

public class PoiFragment extends Fragment implements OverpassInt, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private RVAdapter rvAdapter;
    private RecyclerView recyclerView;
    private Double lat = 17.0;
    private Double lon = 48.0;
    private int maxDistance = 2000;
    private String poi_type = "pub";
    private BboxHolder bboxHolder;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public PoiFragment() {
        // Empty constructor required for fragment subclasses
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity().getApplicationContext(), General.PREFS, Context.MODE_PRIVATE);
        if (complexPreferences.getObject(General.DISTANCE_KEY, Integer.class) != null)
            maxDistance = complexPreferences.getObject(General.DISTANCE_KEY, Integer.class);
        if (complexPreferences.getObject(General.POI_TYPE_KEY, String.class) != null)
            poi_type = complexPreferences.getObject(General.POI_TYPE_KEY, String.class);
        rvAdapter = new RVAdapter(getActivity());
        bboxHolder = new BboxHolder();

        buildRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void buildRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(General.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(General.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second_task, container, false);
        Bundle b = getArguments();
        if (!b.isEmpty()) {
            if (b.getInt(General.DISTANCE_KEY, 0) != 0)
                maxDistance = b.getInt(General.DISTANCE_KEY);
            if (b.getString(General.POI_TYPE_KEY, "empty") != "empty")
                poi_type = b.getString(General.POI_TYPE_KEY);
        }

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.poi_rv);
        recyclerView.setLayoutManager(llm);

        ((TextView) rootView.findViewById(R.id.max_distance_value)).setText(String.valueOf(maxDistance) + "m");
        ((TextView) rootView.findViewById(R.id.poi_type_value)).setText(poi_type);
        Button button = (Button) rootView.findViewById(R.id.refresh_poi);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                submitOnClick();
            }
        });

        bboxHolder.calculate(lat, lon, maxDistance);
        new MyOverpassApi<PoiFragment>
                (
                        this,
                        poi_type,
                        bboxHolder.getMinLat(),
                        bboxHolder.getMinLon(),
                        bboxHolder.getMaxLat(),
                        bboxHolder.getMaxLon()
                ).execute();

        return rootView;
    }


    public void onBackgroundTaskCompleted(List<LocationItem> items) {
        rvAdapter.setPois(getItemsWithinDistance(items));
        recyclerView.setAdapter(rvAdapter);
    }

    private void submitOnClick() {
        bboxHolder.calculate(lat, lon, maxDistance);
        new MyOverpassApi<PoiFragment>
                (
                        this,
                        poi_type,
                        bboxHolder.getMinLat(),
                        bboxHolder.getMinLon(),
                        bboxHolder.getMaxLat(),
                        bboxHolder.getMaxLon()
                ).execute();
    }

    private List<LocationItem> getItemsWithinDistance(List<LocationItem> items) {
        List<LocationItem> list = new ArrayList<>();

        int min = 1000000, min_id = 0, i = 0;

        for (LocationItem item : items) {
            String distance = distance(lat, lon, item.getLat(), item.getLon());
            int intDistance = Integer.valueOf(distance);

            if (intDistance <= maxDistance) {
                item.setDistance(Long.valueOf(distance));
                list.add(item);
                if (min > intDistance) {
                    min = intDistance;
                    min_id = i;
                }
                i++;
            }
        }
        if (list.size() != 0) list.get(min_id).setClosest(true);
        return list;
    }

    private static String distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(BboxHolder.deg2rad(lat1))
                * Math.sin(BboxHolder.deg2rad(lat2))
                + Math.cos(BboxHolder.deg2rad(lat1))
                * Math.cos(BboxHolder.deg2rad(lat2))
                * Math.cos(BboxHolder.deg2rad(theta));
        dist = Math.acos(dist);
        dist = BboxHolder.rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = Math.round(dist * 1000);
        String strdist = String.valueOf(dist);
        return strdist.substring(0, strdist.length() - 2);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        Log.d("LAT", String.format("%.5g", lat));
        lon = location.getLongitude();
        Log.d("LON", String.format("%.5g", lon));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        } else {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location l = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        lat = l.getLatitude();
        lon = l.getLongitude();
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
