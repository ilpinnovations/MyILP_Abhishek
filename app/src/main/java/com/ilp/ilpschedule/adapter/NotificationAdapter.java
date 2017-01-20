package com.ilp.ilpschedule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.model.Notification;
import com.ilp.ilpschedule.model.NotificationViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private ArrayList<Notification> notifications;



    public NotificationAdapter(Context context, ArrayList<Notification> objects) {
        super(context, R.layout.notification_item, objects);
        this.context = context;
        notifications = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Notification notification = notifications.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notification_item, parent, false);
            viewHolder.content = (TextView) convertView.findViewById(R.id.textViewNotificationContent);
            viewHolder.date = (TextView) convertView.findViewById(R.id.textViewNotificationTimestamp);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.content.setText(notification.getMsg());
        viewHolder.date.setText(Notification.outputDateFormat.format(notification.getDate()));

        // Return the completed view to render on screen
        return convertView;


//        NotificationViewHolder nvh;
//        if (convertView == null) {
//            convertView = ((LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                    .inflate(R.layout.notification_item, parent, false);
//            nvh = new NotificationViewHolder();
//            nvh.setMsg((TextView) convertView
//                    .findViewById(R.id.textViewNotificationContent));
//            nvh.setDate((TextView) convertView
//                    .findViewById(R.id.textViewNotificationTimestamp));
//            convertView.setTag(nvh);
//        }
//        nvh = (NotificationViewHolder) convertView.getTag();
//
//        nvh.getMsg().setText(getItem(position).getMsg());
//        nvh.getDate().setText(
//                Notification.outputDateFormat.format(getItem(position)
//                        .getDate()));
//        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView content;
        TextView date;

    }

}
