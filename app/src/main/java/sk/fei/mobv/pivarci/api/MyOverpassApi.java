package sk.fei.mobv.pivarci.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sk.fei.mobv.pivarci.model.LocationEntity;
import sk.fei.mobv.pivarci.model.LocationItem;

public class MyOverpassApi <T extends OverpassInt> extends AsyncTask<String, Void, List<LocationItem>> {

    private String url = "http://overpass-api.de/api/interpreter?data=[out:json];";
    private T caller;
    private String amenity;
    private String minLat;
    private String maxLat;
    private String minLon;
    private String maxLon;

    public MyOverpassApi(T caller, String amenity, Double minLat, Double minLon, Double maxLat, Double maxLon) {
        this.caller = caller;
        this.minLat = String.format("%.5g", minLat);
        this.minLon = String.format("%.5g", minLon);
        this.maxLat = String.format("%.5g", maxLat);
        this.maxLon = String.format("%.5g", maxLon);
        if(amenity == null) this.amenity = "pub"; // default poi type
        else this.amenity = amenity;
    }

    @Override
    protected List<LocationItem> doInBackground(String... params) {

        url += "node[amenity="+amenity+"]";
        url += "("+minLat+","+minLon+","+maxLat+","+maxLon+");";
        url += "out;";

        Log.d("ourl", url);

        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(this.url);

            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LocationEntity oe = new Gson().fromJson(result.toString(), LocationEntity.class);
        List<LocationItem> items = new ArrayList<>();
        for(LocationItem item: oe.getElements()) {
            if(item.getTags().get("name") != null)
                items.add(item);
        }

        return items;
    }

    protected void onPostExecute(List<LocationItem> items) {
        caller.onBackgroundTaskCompleted(items);
    }


}
