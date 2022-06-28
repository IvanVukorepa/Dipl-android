package com.example.androidchatapp.chat_screen;

import android.content.Context;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class chatAdapter extends BaseAdapter {
    private Context myContext;
    private LayoutInflater mInflater;

    public chatAdapter(Context context){
        myContext = context;
        mInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return ChatDataStorage.messages.size();
    }

    @Override
    public Object getItem(int i) {
        return ChatDataStorage.messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (ChatDataStorage.messages.get(i).user.equals(AuthTokenService.getPayloadData("username"))){
            view = getviewMyMessage(i, view, viewGroup);
        } else{
            view = getViewOtherMessage(i, view, viewGroup);
        }

        return view;
    }

    private View getViewOtherMessage(int i, View view, ViewGroup viewGroup) {
        //if (view == null) {
            view = mInflater.inflate(R.layout.message, viewGroup, false);
        //}
        Log.e("getViewOtherMessage", "getViewOtherMessage");

        final TextView chatMessage = (TextView) view.findViewById(R.id.text_gchat_message_other);
        final TextView chatSender = (TextView) view.findViewById(R.id.text_gchat_user_other);
        final TextView time = view.findViewById(R.id.text_gchat_timestamp_other);
        final TextView dateTV = view.findViewById(R.id.text_gchat_date_other);
        final ImageView imageView = view.findViewById(R.id.message_image_view);
        final String message = ChatDataStorage.messages.get(i).message;
        final String sender = ChatDataStorage.messages.get(i).user;

        chatMessage.setText(message);
        chatSender.setText(sender);

        Calendar timeCalendar = Calendar.getInstance();
        try{
            String timeString = ChatDataStorage.messages.get(i).time;
            String finalstr = timeString.substring(0, timeString.length()-5);
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss.SSSSSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date =  dateFormat.parse(finalstr);
            timeCalendar.setTime(date);
            TimeZone tz = TimeZone.getTimeZone("GMT+2");
            timeCalendar.setTimeZone(tz);

            String timeText = String.format("%02d", timeCalendar.get(Calendar.HOUR)) + ":" + String.format("%02d", timeCalendar.get(Calendar.MINUTE));
            time.setText(timeText);

            int day = timeCalendar.get(Calendar.DAY_OF_MONTH);
            int month = timeCalendar.get(Calendar.MONTH);
            int year = timeCalendar.get(Calendar.YEAR);



            dateTV.setVisibility(View.GONE);
            if (i > 0){
                String timeStringPrev = ChatDataStorage.messages.get(i-1).time;
                String finalstrPrev = timeStringPrev.substring(0, timeStringPrev.length()-5);
                DateFormat dateFormatPrev = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss.SSSSSS");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date datePrev =  dateFormatPrev.parse(finalstrPrev);
                timeCalendar.setTime(datePrev);
                timeCalendar.setTimeZone(tz);
                int dayPrev = timeCalendar.get(Calendar.DAY_OF_MONTH);
                int monthPrev = timeCalendar.get(Calendar.MONTH) + 1;
                int yearPrev = timeCalendar.get(Calendar.YEAR);

                if(yearPrev != year || monthPrev != month || dayPrev != day){

                } else {
                    dateTV.setVisibility(View.GONE);

                }
            } else {
                dateTV.setText("Change of date");
            }

        } catch (Exception ex) {
            Log.e("chatAdapter", "error getting date from string. " + ex.toString());
        }

        if(!ChatDataStorage.messages.get(i).imageURI.equals("")){
            Log.e("chat view", "loading image from " + ChatDataStorage.messages.get(i).imageURI);
            Picasso.get().load(ChatDataStorage.messages.get(i).imageURI).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else{
            imageView.setVisibility(View.GONE);
        }

        return view;
    }

    private View getviewMyMessage(int i, View view, ViewGroup viewGroup) {
        //if (view == null) {
            view = mInflater.inflate(R.layout.message_me, viewGroup, false);
        //}
        Log.e("getviewMyMessage", "getviewMyMessage");
        final TextView chatMessage = (TextView) view.findViewById(R.id.text_gchat_message_me);
        final TextView time = view.findViewById(R.id.text_gchat_timestamp_me);
        final TextView dateTV = view.findViewById(R.id.text_gchat_date_me);
        final ImageView imageView = view.findViewById(R.id.message_image_view_me);
        final String message = ChatDataStorage.messages.get(i).message;

        chatMessage.setText(message);
        Calendar timeCalendar = Calendar.getInstance();

        // TODO chow dateTV when needed
        dateTV.setVisibility(View.GONE);

        try{
            String timeString = ChatDataStorage.messages.get(i).time;
            String finalstr = timeString.substring(0, timeString.length()-5);
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss.SSSSSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date =  dateFormat.parse(finalstr);
            timeCalendar.setTime(date);
            TimeZone tz = TimeZone.getTimeZone("GMT+2");
            timeCalendar.setTimeZone(tz);

            String timeText = String.format("%02d", timeCalendar.get(Calendar.HOUR)) + ":" + String.format("%02d", timeCalendar.get(Calendar.MINUTE));
            time.setText(timeText);

            int day = timeCalendar.get(Calendar.DAY_OF_MONTH);
            int month = timeCalendar.get(Calendar.MONTH);
            int year = timeCalendar.get(Calendar.YEAR);

            if (i > 0){
                String timeStringPrev = ChatDataStorage.messages.get(i-1).time;
                String finalstrPrev = timeStringPrev.substring(0, timeStringPrev.length()-5);
                DateFormat dateFormatPrev = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss.SSSSSS");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date datePrev =  dateFormatPrev.parse(finalstrPrev);
                timeCalendar.setTime(datePrev);
                timeCalendar.setTimeZone(tz);
                int dayPrev = timeCalendar.get(Calendar.DAY_OF_MONTH);
                int monthPrev = timeCalendar.get(Calendar.MONTH);
                int yearPrev = timeCalendar.get(Calendar.YEAR);

                if(yearPrev != year || monthPrev != month || dayPrev != day){
                    dateTV.setText("Change of date");
                } else {
                    dateTV.setVisibility(View.GONE);
                }
            } else {
                dateTV.setText("Change of date");
            }
        } catch (Exception ex) {
            Log.e("chatAdapter", "error getting date from string. " + ex.toString());
        }

        if(!ChatDataStorage.messages.get(i).imageURI.equals("")){
            Log.e("chat view", "loading image from " + ChatDataStorage.messages.get(i).imageURI);
            Picasso.get().load(ChatDataStorage.messages.get(i).imageURI).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else{
            imageView.setVisibility(View.GONE);
        }

        return view;    }
}
