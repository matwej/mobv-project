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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.model.VolleyMessage;

public class VolleyAdapter extends RecyclerView.Adapter<VolleyAdapter.VolleyViewHolder> {

    private List<VolleyMessage> messageList;
    private LayoutInflater inflater;
    private Context context;

    public VolleyAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        messageList = new ArrayList<>();
        this.context = context;
    }

    public void addMessage(VolleyMessage message) {
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void addMessages(List<VolleyMessage> messages) {
        messageList.addAll(messages);
        notifyDataSetChanged();
    }

    public VolleyMessage getMessage(int position) {
        return messageList.get(position);
    }

    @Override
    public VolleyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_volley_message, parent, false);
        return new VolleyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VolleyViewHolder holder, int position) {
        VolleyMessage message = messageList.get(position);
        holder.textView.setText(message.getText());
        holder.timestampView.setText(message.getSent());
        Drawable mDrawable;
        switch (message.getStatus()) {
            case VolleyMessage.STATUS_SENT:
                holder.statusIcon.setVisibility(View.GONE);
                break;
            case VolleyMessage.STATUS_ERROR: {
                holder.statusIcon.setVisibility(View.VISIBLE);
                mDrawable = ContextCompat.getDrawable(context,android.R.drawable.ic_dialog_alert);
                assert mDrawable != null;
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
                holder.statusIcon.setImageDrawable(mDrawable);
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // znovuposlanie msg
                    }
                });
            }
            break;
            case VolleyMessage.STATUS_SENDING: {
                holder.statusIcon.setVisibility(View.VISIBLE);
                mDrawable = ContextCompat.getDrawable(context,android.R.drawable.stat_sys_upload);
                assert mDrawable != null;
                mDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context,R.color.color_primary), PorterDuff.Mode.MULTIPLY));
                holder.statusIcon.setImageDrawable(mDrawable);
            }
            break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class VolleyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView timestampView;
        ImageView statusIcon;

        public VolleyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            statusIcon = (ImageView) itemView.findViewById(R.id.status);
            timestampView = (TextView) itemView.findViewById(R.id.msg_timestamp);
        }
    }
}
