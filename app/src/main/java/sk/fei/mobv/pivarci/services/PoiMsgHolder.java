package sk.fei.mobv.pivarci.services;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sk.fei.mobv.pivarci.R;

/**
 * Created by matwej on 12/2/15.
 */
public class PoiMsgHolder extends RecyclerView.ViewHolder {

    TextView username;
    TextView sent;
    TextView text;
    TextView poi;
    ImageView status;

    public PoiMsgHolder(View view) {
        super(view);
        username = (TextView)view.findViewById(R.id.username);
        sent = (TextView)view.findViewById(R.id.sent);
        text = (TextView)view.findViewById(R.id.text);
        poi = (TextView)view.findViewById(R.id.poiName);
        status = (ImageView)view.findViewById(R.id.status);
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public TextView getSent() {
        return sent;
    }

    public void setSent(TextView sent) {
        this.sent = sent;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

    public TextView getPoi() {
        return poi;
    }

    public void setPoi(TextView poi) {
        this.poi = poi;
    }

    public ImageView getStatus() {
        return status;
    }

    public void setStatus(ImageView status) {
        this.status = status;
    }
}
