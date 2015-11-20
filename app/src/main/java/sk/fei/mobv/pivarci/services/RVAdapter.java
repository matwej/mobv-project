package sk.fei.mobv.pivarci.services;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.model.LocationItem;
import sk.fei.mobv.pivarci.services.PoiViewHolder;

public class RVAdapter extends RecyclerView.Adapter<PoiViewHolder> {

    private List<LocationItem> pois;
    private LayoutInflater layoutInflater;
    private int maxDistance;

    public RVAdapter(Context context, int maxDistance) {
        layoutInflater = LayoutInflater.from(context);
        this.maxDistance = maxDistance;
    }

    @Override
    public int getItemCount() {
        return pois.size();
    }

    @Override
    public PoiViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = layoutInflater.inflate(R.layout.poi_card, viewGroup, false);
        PoiViewHolder rvh = new PoiViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(PoiViewHolder poiViewHolder, int i) {
        String name = pois.get(i).getTags().get("name");
        String distance = String.valueOf(pois.get(i).getDistance());
        String id = String.valueOf(pois.get(i).getId());

        if(pois.get(i).isClosest())
            poiViewHolder.getCv().setBackgroundColor(Color.GREEN);

        poiViewHolder.getId().setText(id);
        poiViewHolder.getName().setText(name);
        poiViewHolder.getDistance().setText(distance + " m");
        poiViewHolder.getIcon().setImageResource(R.mipmap.ic_poi_icon);
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
