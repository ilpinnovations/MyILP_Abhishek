package com.ilp.ilpschedule.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.model.ChatMessage;
import com.ilp.ilpschedule.model.ChatType;
import com.ilp.ilpschedule.model.CseBean;
import com.ilp.ilpschedule.model.Status;
import com.ilp.ilpschedule.model.WeatherApiBean;
import com.ilp.ilpschedule.util.AndroidUtilities;
import com.ilp.ilpschedule.util.Util;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public ChatListAdapter(ArrayList<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;

    }


    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        ChatMessage message = chatMessages.get(position);
        ViewHolderBotDefault holder1;
        ViewHolderUser holder2;
        ViewHolderWeather holder3;
        ViewHolderSearch holder4;

        if (message.getUserType() == ChatType.BOT_DEFAULT) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_item_bot_default, null, false);
                holder1 = new ViewHolderBotDefault();

                holder1.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder1.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolderBotDefault) v.getTag();
            }

//            holder1.messageTextView.setText(Emoji.replaceEmoji(message.getMessageText(), holder1.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
            holder1.messageTextView.setText(message.getMessageText());
            holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

        } else if (message.getUserType() == ChatType.USER) {

            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_item_user, null, false);

                holder2 = new ViewHolderUser();


                holder2.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder2.timeTextView = (TextView) v.findViewById(R.id.time_text);
                holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);
                v.setTag(holder2);

            } else {
                v = convertView;
                holder2 = (ViewHolderUser) v.getTag();

            }

//            holder2.messageTextView.setText(Emoji.replaceEmoji(message.getMessageText(), holder2.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16) ));
            holder2.messageTextView.setText(message.getMessageText());
            holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

            if (message.getMessageStatus() == Status.DELIVERED) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double_tick));
            } else if (message.getMessageStatus() == Status.SENT) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_single_tick));

            }


        }
        else if (message.getUserType() == ChatType.BOT_WEATHER){
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_item_bot_weather, null, false);
                holder3 = new ViewHolderWeather();

                holder3.geoLocation = (TextView) v.findViewById(R.id.weather_geo_location);
                holder3.weatherTypeImage = (ImageView) v.findViewById(R.id.weather_type_image);
                holder3.weatherType = (TextView) v.findViewById(R.id.weather_type);
                holder3.temperature = (TextView) v.findViewById(R.id.weather_temperature);
                holder3.tempMax = (TextView) v.findViewById(R.id.weather_max_temp);
                holder3.tempMin = (TextView) v.findViewById(R.id.weather_min_temp);
                holder3.humidity = (TextView) v.findViewById(R.id.weather_humidity);
                holder3.wind = (TextView) v.findViewById(R.id.weather_wind);
                holder3.sunrise = (TextView) v.findViewById(R.id.weather_sunrise);
                holder3.sunset = (TextView) v.findViewById(R.id.weather_sunset);
                holder3.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder3);
            } else {
                v = convertView;
                holder3 = (ViewHolderWeather) v.getTag();
            }
            WeatherApiBean bean = message.getBean();

            holder3.weatherTypeImage.setImageResource(Util.getArtResourceForWeatherCondition(Integer.parseInt(bean.getWeatherId())));
            holder3.geoLocation.setText(bean.getLocation());
            holder3.weatherType.setText(bean.getWeatherDescription());
            holder3.temperature.setText(Util.kelvinToCelsius(Double.parseDouble(bean.getTemp())) + " °C");
            holder3.tempMax.setText(Util.kelvinToCelsius(Double.parseDouble(bean.getTempMax())) + " °C");
            holder3.tempMin.setText(Util.kelvinToCelsius(Double.parseDouble(bean.getTempMin())) + " °C");
            holder3.humidity.setText(bean.getHumidity() + " %");
            holder3.wind.setText(bean.getWindSpeed() + " m/sec");
            holder3.sunrise.setText(bean.getSunrise());
            holder3.sunset.setText(bean.getSunset());
            holder3.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

        }
        else if (message.getUserType() == ChatType.BOT_SEARCH){
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_item_bot_search, null, false);
                holder4 = new ViewHolderSearch();

                holder4.image = (ImageView) v.findViewById(R.id.search_image);
                holder4.heading = (TextView) v.findViewById(R.id.search_heading);
                holder4.link = (TextView) v.findViewById(R.id.search_link);
                holder4.summary = (TextView) v.findViewById(R.id.search_summary);
                holder4.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder4);
            } else {
                v = convertView;
                holder4 = (ViewHolderSearch) v.getTag();
            }

            CseBean cseBean = message.getCseBean();

            Picasso.with(context)
                    .load(cseBean.getImagePath())
                    .resize(50,50)
                    .centerCrop()
//                    .error(R.drawable.image_not_found_icon)
                    .into(holder4.image);

            holder4.heading.setText(cseBean.getHeading());
            holder4.link.setText(cseBean.getLink());
            holder4.summary.setText(cseBean.getSummary());
            holder4.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));
        }

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        return message.getUserType().ordinal();
    }

    private class ViewHolderBotDefault {
        public TextView messageTextView;
        public TextView timeTextView;

    }

    private class ViewHolderUser {
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;

    }

    private class ViewHolderWeather {
        public TextView geoLocation;
        public ImageView weatherTypeImage;
        public TextView temperature;
        public TextView tempMax;
        public TextView tempMin;
        public TextView weatherType;
        public TextView humidity;
        public TextView wind;
        public TextView sunrise;
        public TextView sunset;
        public TextView timeTextView;
    }

    private class ViewHolderSearch {
        public ImageView image;
        public TextView heading;
        public TextView link;
        public TextView summary;
        public TextView timeTextView;
    }
}
