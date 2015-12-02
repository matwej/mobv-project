package sk.fei.mobv.pivarci.services;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.model.PoiMessage;

public class PoiMessageAdapter extends RecyclerView.Adapter<PoiMsgHolder> {

    private List<PoiMessage> messageList;
    private LayoutInflater inflater;
    private Context context;

    public PoiMessageAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        messageList = new ArrayList<>();
        this.context = context;
    }

    public void addMessage(PoiMessage message) {
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void addMessages(List<PoiMessage> messages) {
        messageList.addAll(messages);
        notifyDataSetChanged();
    }

    public PoiMessage getMessage(int position) {
        return messageList.get(position);
    }

    @Override
    public PoiMsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_poi_message, parent, false);
        return new PoiMsgHolder(view);
    }

    @Override
    public void onBindViewHolder(PoiMsgHolder holder, int position) {
        PoiMessage message = messageList.get(position);
        holder.getText().setText(message.getText());
        holder.getSent().setText(message.getSent());
        holder.getUsername().setText(message.getUsername());
        holder.getPoi().setText(message.getPoi_name());
        Drawable mDrawable;
        switch (message.getStatus()) {
            case PoiMessage.STATUS_SENT:
                holder.getStatus().setVisibility(View.GONE);
                break;
            case PoiMessage.STATUS_ERROR: {
                holder.getStatus().setVisibility(View.VISIBLE);
                mDrawable = ContextCompat.getDrawable(context, android.R.drawable.ic_dialog_alert);
                assert mDrawable != null;
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
                holder.getStatus().setImageDrawable(mDrawable);
            }
            break;
            case PoiMessage.STATUS_SENDING: {
                holder.getStatus().setVisibility(View.VISIBLE);
                mDrawable = ContextCompat.getDrawable(context,android.R.drawable.stat_sys_upload);
                assert mDrawable != null;
                mDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context,R.color.color_primary), PorterDuff.Mode.MULTIPLY));
                holder.getStatus().setImageDrawable(mDrawable);
            }
            break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void cleanMessages() {
        List<PoiMessage> toRemove = new ArrayList<>();
        for (PoiMessage poiMessage : messageList) {
            if (poiMessage.getStatus() == PoiMessage.STATUS_SENT && poiMessage.getId() == null) {
                toRemove.add(poiMessage);
            }
        }
        messageList.removeAll(toRemove);
    }
}
