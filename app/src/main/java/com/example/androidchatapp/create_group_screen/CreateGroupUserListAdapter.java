package com.example.androidchatapp.create_group_screen;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.main_screen.ChatListDataStorage;

import java.io.PushbackReader;
import java.util.ArrayList;

public class CreateGroupUserListAdapter extends BaseAdapter implements Filterable {
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
        if(CreateGroupUserDataStorage.selectedUsernames.contains(username)){
            view.setBackgroundColor(Color.BLUE);
        } else{
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                ArrayList<String> filteredUsernames = new ArrayList<>();

                String stringToCheck = charSequence.toString().toLowerCase();


                for (String username: CreateGroupUserDataStorage.allUsernames) {
                    if (username.toLowerCase().startsWith(stringToCheck)){
                        filteredUsernames.add(username);
                    }
                }

                results.count = filteredUsernames.size();
                results.values = filteredUsernames;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                CreateGroupUserDataStorage.usernames.clear();
                if (charSequence.equals("")){
                    CreateGroupUserDataStorage.usernames.addAll(CreateGroupUserDataStorage.allUsernames);
                    for (String username:CreateGroupUserDataStorage.selectedUsernames) {
                        if (!CreateGroupUserDataStorage.usernames.contains(username)){
                            CreateGroupUserDataStorage.usernames.add(username);
                        }
                    }
                } else {
                    CreateGroupUserDataStorage.usernames = (ArrayList<String>) filterResults.values;
                    for (String username:CreateGroupUserDataStorage.selectedUsernames) {
                        if (!CreateGroupUserDataStorage.usernames.contains(username)){
                            CreateGroupUserDataStorage.usernames.add(username);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        };
    }
}
