package com.example.androidchatapp.chat_screen;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.main_screen.ChatListDataStorage;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ConcurrentHashMap;

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
        if (view == null) {
            view = mInflater.inflate(R.layout.message, viewGroup, false);
        }

        DisplayMetrics displayMetrics = myContext.getResources().getDisplayMetrics();

        final TextView chatMessage = (TextView) view.findViewById(R.id.messageContentTV);
        final TextView chatSender = (TextView) view.findViewById(R.id.messageSenderTV);
        final String message = ChatDataStorage.messages.get(i).message;
        final String sender = ChatDataStorage.messages.get(i).user;

        chatMessage.setWidth(displayMetrics.widthPixels);
        chatSender.setWidth(displayMetrics.widthPixels);

        if (sender.equals(AuthTokenService.getPayloadData("username"))){
            chatMessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            chatSender.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else{
            chatMessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            chatSender.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

        chatMessage.setText(message);
        chatSender.setText(sender);

        return view;    }
}
