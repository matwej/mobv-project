package sk.fei.mobv.pivarci.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class SecondTaskFragment extends Fragment implements OverpassInt, LocationListener {

    private RVAdapter rvAdapter;
    private RecyclerView recyclerView;
    private Double lat = 48.151923;
    private Double lon = 17.074021;
    private int maxDistance = 2000;
    private String poi_type = "pub";
    private BboxHolder bboxHolder;
    private LocationManager locationManager;

    public SecondTaskFragment() {
        // Empty constructor required for fragment subclasses
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity().getApplicationContext(), General.PREFS, Context.MODE_PRIVATE);
        if (complexPreferences.getObject(General.DISTANCE_KEY, Integer.class) != null)
            maxDistance = complexPreferences.getObject(General.DISTANCE_KEY, Integer.class);
        if (complexPreferences.getObject(General.POI_TYPE_KEY, String.class) != null)
            poi_type = complexPreferences.getObject(General.POI_TYPE_KEY, String.class);
        rvAdapter = new RVAdapter(getActivity());
        bboxHolder = new BboxHolder();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second_task, container, false);
        Bundle b = getArguments();
        if(!b.isEmpty()) {
            if (b.getInt(General.DISTANCE_KEY, 0) != 0)
                maxDistance = b.getInt(General.DISTANCE_KEY);
            if (b.getString(General.POI_TYPE_KEY, "empty") != "empty")
                poi_type = b.getString(General.POI_TYPE_KEY);
        }

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView)rootView.findViewById(R.id.poi_rv);
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
        new MyOverpassApi<SecondTaskFragment>
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
        new MyOverpassApi<SecondTaskFragment>
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

        int min=1000000, min_id=0, i=0;

        for (LocationItem item: items) {
            String distance = distance(lat, lon, item.getLat(), item.getLon());
            int intDistance = Integer.valueOf(distance);

            if(intDistance <= maxDistance ) {
                item.setDistance(Long.valueOf(distance));
                list.add(item);
                if(min > intDistance) {
                    min = intDistance;
                    min_id = i;
                }
                i++;
            }
        }
        if(list.size()!=0) list.get(min_id).setClosest(true);
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
        return strdist.substring(0, strdist.length()-2);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
