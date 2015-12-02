package sk.fei.mobv.pivarci.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sk.fei.mobv.pivarci.MainActivity;
import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.model.LocationItem;
import sk.fei.mobv.pivarci.model.PoiMessage;
import sk.fei.mobv.pivarci.model.User;
import sk.fei.mobv.pivarci.services.PoiMessageAdapter;
import sk.fei.mobv.pivarci.settings.ComplexPreferences;
import sk.fei.mobv.pivarci.settings.General;

import com.android.volley.Response;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

/**
 * Created by matwej on 12/2/15.
 */
public class ChatPoiFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {

    private static final String URL_SEND = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=poiMessage/send";
    private static final String URL_GET = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=poiMessage/get";
    private static final String FETCHED_MSGS_LIMIT = "50";

    private SwipyRefreshLayout swipyRefreshLayout;
    private Date lastUpdate;
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private PoiMessageAdapter poiMessageAdapter;
    private TextView msg;
    private long ids;
    private DateFormat dateFormat;
    private String fav_id;
    private String fav_name;
    private ComplexPreferences complexPreferences;
    HashMap<String, String> sendParams;
    HashMap<String, String> recParams;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), General.PREFS, Context.MODE_PRIVATE);

        sendParams = new HashMap<>();
        sendParams.put("api_key", General.API_KEY);
        sendParams.put("msg", "");
        sendParams.put("token", ((MainActivity) getActivity()).getUser().getSession_token());
        recParams = new HashMap<>();
        recParams.put("api_key", General.API_KEY);
        recParams.put("limit", FETCHED_MSGS_LIMIT );
        recParams.put("token", ((MainActivity) getActivity()).getUser().getSession_token());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30); // za poslednu polhodinu
        lastUpdate = calendar.getTime();

        fav_id = complexPreferences.getObject(General.CHOSEN_POI_ID_KEY, String.class);
        fav_name = complexPreferences.getObject(General.CHOSEN_POI_NAME_KEY, String.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ids = -1;
        View view = inflater.inflate(R.layout.fragment_volley, container, false);
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_green_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_green_dark);

        queue = Volley.newRequestQueue(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        poiMessageAdapter = new PoiMessageAdapter(inflater, getActivity());
        recyclerView.setAdapter(poiMessageAdapter);
        msg = (TextView) view.findViewById(R.id.messageText);
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.sendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage((MainActivity) getActivity());
            }
        });

        return view;
    }

    private void sendMessage(MainActivity m) {
        if(!msg.getText().toString().isEmpty()) {
            sendParams.put("poi_id", fav_id);
            sendParams.put("poi_type", "node");
            sendParams.put("poi_name", fav_name);
            sendParams.put("msg", msg.getText().toString());
            PoiMessage message = new PoiMessage();
            message.setUsername(m.getUsername());
            message.setSent(dateFormat.format(new Date()));
            message.setText(msg.getText().toString());
            msg.setText("");
            message.setStatus(PoiMessage.STATUS_SENDING);
            message.setPoi_name(fav_name);
            poiMessageAdapter.addMessage(message);
            SendResponseListener sendResponseListener = new SendResponseListener(message);
            JsonObjectRequest req = new JsonObjectRequest(URL_SEND, new JSONObject(sendParams), sendResponseListener, sendResponseListener);
            queue.add(req);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        recParams.put("from", Long.toString(lastUpdate.getTime() / 1000));

        final Date before = lastUpdate;
        lastUpdate = new Date();

        JsonArrayRequest req = new JsonArrayRequest(
                URL_GET,
                new JSONObject(recParams),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        swipyRefreshLayout.setRefreshing(false);
                        List<PoiMessage> messages = new Gson().fromJson(response.toString(), new TypeToken<List<PoiMessage>>() {}.getType());
                        poiMessageAdapter.cleanMessages();
                        poiMessageAdapter.addMessages(messages);
                        recyclerView.scrollToPosition(poiMessageAdapter.getItemCount() - 1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lastUpdate = before;
                        VolleyLog.e("Error: ", error.getMessage());
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
        queue.add(req);
    }

    private class SendResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener {

        private PoiMessage message;

        public SendResponseListener(PoiMessage message) {
            this.message = message;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            message.setStatus(PoiMessage.STATUS_ERROR);
            poiMessageAdapter.notifyDataSetChanged();
        }

        @Override
        public void onResponse(JSONObject response) {
            if (response.has("id")) {
                message.setStatus(PoiMessage.STATUS_SENT);
                poiMessageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(poiMessageAdapter.getItemCount() - 1);
            }
        }
    }

}
