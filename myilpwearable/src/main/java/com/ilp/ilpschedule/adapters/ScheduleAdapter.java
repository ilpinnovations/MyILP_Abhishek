package com.ilp.ilpschedule.adapters;

/**
 * Created by 1007546 on 18-10-2016.
 */

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.beans.SlotBean;

import java.util.ArrayList;

public class ScheduleAdapter extends WearableListView.Adapter {
    private ArrayList<SlotBean> schedules = new ArrayList<>();
    private final LayoutInflater mInflater;

    public ScheduleAdapter(Context context, ArrayList<SlotBean> schedules) {
        mInflater = LayoutInflater.from(context);
        this.schedules = schedules;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.schedule_row, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.titleTextView.setText(schedules.get(position).getTitle());
        itemViewHolder.locationTextView.setText(schedules.get(position).getLocation());
        itemViewHolder.slotTextView.setText(schedules.get(position).getSlot());
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {
        public TextView titleTextView, locationTextView, slotTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            locationTextView = (TextView) itemView.findViewById(R.id.location);
            slotTextView = (TextView) itemView.findViewById(R.id.slot);
        }
    }
}