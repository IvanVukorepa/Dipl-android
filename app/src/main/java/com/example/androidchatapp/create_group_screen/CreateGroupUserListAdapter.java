package com.example.androidchatapp.create_group_screen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidchatapp.R;
import com.example.androidchatapp.main_screen.ChatListDataStorage;

public class CreateGroupUserListAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return CreateGroupUserDataStorage.usernames.size();
    }

    @Override
    public Object getItem(int i) {
        return CreateGroupUserDataStorage.usernames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private Context myContext;
    private LayoutInflater mInflater;

    public CreateGroupUserListAdapter(Context context){
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
        final String username = CreateGroupUserDataStorage.usernames.get(i);

        ChatName.setText(username);
        //Picasso.get().load(myContext.getString(R.string.baseURL) + sport.getImageUrl()).into(imageTmb);

        return view;
    }
}
