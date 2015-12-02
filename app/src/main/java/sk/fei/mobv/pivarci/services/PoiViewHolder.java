package sk.fei.mobv.pivarci.services;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sk.fei.mobv.pivarci.R;

public class PoiViewHolder extends RecyclerView.ViewHolder {
    CardView cv;
    TextView id;
    TextView name;
    TextView distance;
    ImageView icon;

    public PoiViewHolder(View itemView) {
        super(itemView);
        cv = (CardView)itemView.findViewById(R.id.overpass_cv);
        id = (TextView)itemView.findViewById(R.id.overpass_cv_id);
        name = (TextView)itemView.findViewById(R.id.overpass_cv_name);
        distance = (TextView)itemView.findViewById(R.id.overpass_cv_distance);
        icon = (ImageView)itemView.findViewById(R.id.overpass_cv_icon);
    }

    public CardView getCv() {
        return cv;
    }

    public void setCv(CardView cv) {
        this.cv = cv;
    }

    public TextView getId() {
        return id;
    }

    public void setId(TextView id) {
        this.id = id;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getDistance() {
        return distance;
    }

    public void setDistance(TextView distance) {
        this.distance = distance;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }
}