package com.example.androidchatapp.main_screen;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChatsListAdapter extends BaseAdapter implements Filterable {
    @Override
    public int getCount() {
        return ChatListDataStorage.chats.size();
    }

    @Override
    public Object getItem(int position) {
        return ChatListDataStorage.chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Context myContext;
    private LayoutInflater mInflater;

    public ChatsListAdapter(Context context) {
        myContext = context;
        mInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("afaf", "asefgwagweagweargrsedagrsedgrthse");
        if (view == null) {
            view = mInflater.inflate(R.layout.chat_list_iten, viewGroup, false);
        }

        final TextView ChatName = (TextView) view.findViewById(R.id.ChatName);
        ImageView imageTmb = (ImageView) view.findViewById(R.id.image_tmb);
        final String chatInstance = ChatListDataStorage.chats.get(i).chatName;

        ChatName.setText(chatInstance);
        //Picasso.get().load(myContext.getString(R.string.baseURL) + sport.getImageUrl()).into(imageTmb);

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                ArrayList<UserGroup> filteredChats = new ArrayList<>();

                String stringToCheck = charSequence.toString().toLowerCase();


                for (UserGroup ug: ChatListDataStorage.allChats) {
                    if (ug.chatName.toLowerCase().startsWith(stringToCheck)){
                        filteredChats.add(ug);
                    }
                }

                results.count = filteredChats.size();
                results.values = filteredChats;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (charSequence.equals("")){
                    ChatListDataStorage.chats = ChatListDataStorage.allChats;
                } else {
                    ChatListDataStorage.chats = (ArrayList<UserGroup>) filterResults.values;
                }
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
