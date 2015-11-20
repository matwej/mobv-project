package sk.fei.mobv.pivarci.api;

import android.app.Fragment;
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

public class OverpassApi extends AsyncTask<String, Void, List<LocationItem>> {

    private String api_url = "http://overpass-api.de/api/interpreter?data=[out:json];";
    Fragment caller;

    public OverpassApi(Fragment caller) {
        this.caller = caller;
    }

    @Override
    protected List<LocationItem> doInBackground(String... params) {

        api_url += "node[amenity=restaurant]";
        api_url += "(48.14456,16.99362,48.19367,17.10932);";
        api_url += "out;";

        Log.d("qqq", "overpass api url: " + api_url);

        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(api_url);

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
        for(LocationItem item: oe.getItems()) {
            if(item.getTags().get("name") != null)
                items.add(item);
        }

        return items;
    }

    protected void onPostExecute(List<LocationItem> items) {
        //caller.onBackgroundTaskCompleted(items); TODO
    }


}
