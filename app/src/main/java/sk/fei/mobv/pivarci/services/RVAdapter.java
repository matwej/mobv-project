package sk.fei.mobv.pivarci.services;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.fragments.PoiFragment;
import sk.fei.mobv.pivarci.model.LocationItem;
import sk.fei.mobv.pivarci.settings.ComplexPreferences;
import sk.fei.mobv.pivarci.settings.General;

public class RVAdapter extends RecyclerView.Adapter<PoiViewHolder> {

    private List<LocationItem> pois;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private CardView selected;
    private LocationItem li;

    public RVAdapter(Activity activity) {
        layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return pois.size();
    }

    @Override
    public PoiViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = layoutInflater.inflate(R.layout.poi_card, viewGroup, false);
        return new PoiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PoiViewHolder poiViewHolder, int i) {
        String name = pois.get(i).getTags().get("name");
        String distance = String.valueOf(pois.get(i).getDistance());
        String id = String.valueOf(pois.get(i).getId());

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(activity.getApplicationContext(), General.PREFS, Context.MODE_PRIVATE);
        int maxd = complexPreferences.getObject(General.DISTANCE_KEY, Integer.class);
        String d2 = "0";
        if (selected != null){
            String dist = ((TextView) selected.findViewById(R.id.overpass_cv_distance)).getText().toString();
            d2 = dist.substring(0, dist.length() - 2);
        }

        if((pois.get(i).isClosest() && selected == null) || Integer.parseInt(d2) > maxd) {
            poiViewHolder.getCv().findViewById(R.id.overpass_cv_rv).setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
            selected = poiViewHolder.getCv();
            li = pois.get(i);
        }

        poiViewHolder.getId().setText(id);
        poiViewHolder.getName().setText(name);
        poiViewHolder.getDistance().setText(distance + " m");
        poiViewHolder.getIcon().setImageResource(R.mipmap.ic_poi_icon);

        poiViewHolder.getCv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected.findViewById(R.id.overpass_cv_rv).setBackgroundColor(ContextCompat.getColor(activity, R.color.light_green));
                selected = (CardView) v;
                v.findViewById(R.id.overpass_cv_rv).setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(activity.getApplicationContext(), General.PREFS, Context.MODE_PRIVATE);
                complexPreferences.putObject(General.CHOSEN_POI_ID_KEY, ((TextView) v.findViewById(R.id.overpass_cv_id)).getText());
                complexPreferences.putObject(General.CHOSEN_POI_NAME_KEY, ((TextView) v.findViewById(R.id.overpass_cv_name)).getText());
                complexPreferences.commit();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public List<LocationItem> getPois() {
        return pois;
    }

    public void setPois(List<LocationItem> pois) {
        this.pois = pois;
    }
}
