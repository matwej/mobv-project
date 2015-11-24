package sk.fei.mobv.pivarci.fragments;

import android.content.Context;
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
import sk.fei.mobv.pivarci.settings.AccountGeneral;
import sk.fei.mobv.pivarci.settings.ComplexPreferences;

public class SecondTaskFragment extends Fragment implements OverpassInt {

    public static final String DISTANCE_KEY = "distance";
    public static final String POI_TYPE_KEY = "poitype";

    private RVAdapter rvAdapter;
    private RecyclerView recyclerView;
    private Double lat = 48.151923;
    private Double lon = 17.074021;
    private int maxDistance = 2000;
    private String poi_type = "pub";
    private BboxHolder bboxHolder;
    private ComplexPreferences complexPreferences;

    public SecondTaskFragment() {
        // Empty constructor required for fragment subclasses
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity().getApplicationContext(), AccountGeneral.PREFS, Context.MODE_PRIVATE);
        if(complexPreferences.getObject(DISTANCE_KEY, Integer.class) != null)
            maxDistance = complexPreferences.getObject(DISTANCE_KEY, Integer.class);
        rvAdapter = new RVAdapter(getActivity(), maxDistance);
        // setCurrentLocation();
        bboxHolder = new BboxHolder();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second_task, container, false);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView)rootView.findViewById(R.id.poi_rv);
        recyclerView.setLayoutManager(llm);

        ((TextView) rootView.findViewById(R.id.max_distance_value)).setText(String.valueOf(maxDistance) + "m");
        Button button = (Button) rootView.findViewById(R.id.refresh_poi);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                submitOnClick();
            }
        });

        Bundle b = getArguments();
        if(!b.isEmpty()) {
            if (b.getInt(DISTANCE_KEY, 0) != 0)
                maxDistance = b.getInt(DISTANCE_KEY);
            if (b.getString(POI_TYPE_KEY, "empty") != "empty")
                poi_type = b.getString(POI_TYPE_KEY);
        }

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
        // setCurrentLocation();
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
}
