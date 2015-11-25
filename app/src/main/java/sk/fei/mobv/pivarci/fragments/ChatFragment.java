package sk.fei.mobv.pivarci.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.model.VolleyMessage;
import sk.fei.mobv.pivarci.services.VolleyAdapter;

public class ChatFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {

    public ChatFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static final String API_KEY = "3C7e56ZRFQcMXXr";
    private static final String URL_SEND = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=message/send";
    private static final String URL_FETCH = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=message/get";
    private SwipyRefreshLayout swipyRefreshLayout;
    private Date lastUpdate;
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private VolleyAdapter adapter;
    private TextView newMessage;
    private long messageIDs;
    private DateFormat dateFormat;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        messageIDs = -1;
        View view = inflater.inflate(R.layout.fragment_volley, container, false);
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queue = Volley.newRequestQueue(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new VolleyAdapter(inflater, getActivity());
        recyclerView.setAdapter(adapter);

        newMessage = (TextView) view.findViewById(R.id.messageText);
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.sendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        lastUpdate = calendar.getTime();

        return view;
    }

    private void sendMessage() {
        String newMessageText = newMessage.getText().toString();
        if (!newMessageText.isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("api_key", API_KEY);
            params.put("msg", newMessageText);
            newMessage.setText("");
            VolleyMessage message = new VolleyMessage();
            message.setId(messageIDs--);
            message.setSent(dateFormat.format(new Date()));
            message.setText(newMessageText);
            message.setStatus(VolleyMessage.STATUS_SENDING);
            adapter.addMessage(message);
            SendResponseListener sendResponseListener = new SendResponseListener(message);
            JsonObjectRequest req = new JsonObjectRequest(URL_SEND, new JSONObject(params), sendResponseListener, sendResponseListener);
            queue.add(req);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        HashMap<String, String> params = new HashMap<>();
        params.put("api_key", API_KEY);
        params.put("from", Long.toString(lastUpdate.getTime() / 1000));
        params.put("limit", Integer.toString(50));
        final Date temp = lastUpdate;
        lastUpdate = new Date();
        JsonArrayRequest req = new JsonArrayRequest(URL_FETCH, new JSONObject(params),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        swipyRefreshLayout.setRefreshing(false);
                        Type messageListType = new TypeToken<List<VolleyMessage>>() {
                        }.getType();
                        List<VolleyMessage> messages = new Gson().fromJson(response.toString(), messageListType);
                        adapter.addMessages(messages);
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lastUpdate = temp;
                VolleyLog.e("Error: ", error.getMessage());
                swipyRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(req);
    }

    private class SendResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener {

        private VolleyMessage message;

        public SendResponseListener(VolleyMessage message) {
            this.message = message;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            message.setStatus(VolleyMessage.STATUS_ERROR);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onResponse(JSONObject response) {
            message.setStatus(VolleyMessage.STATUS_SENT);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }
}
