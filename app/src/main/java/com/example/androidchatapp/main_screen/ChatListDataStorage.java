package com.example.androidchatapp.main_screen;

import android.content.Context;
import android.util.Log;

import com.example.androidchatapp.DB.GroupsDataSource;
import com.example.androidchatapp.Models.Group;
import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class ChatListDataStorage {

    public static ArrayList<UserGroup> chats = new ArrayList<>();
    public static ArrayList<UserGroup> allChats = new ArrayList<>();

    public static void fillData(final Context context, final ChatsListAdapter adapter){
        ChatService.getAllGroupsForUser(context, AuthTokenService.getPayloadData("username"), adapter);
    }

    public static void sortGroups(Context context, final ChatsListAdapter adapter){
        GroupsDataSource groupsDataSource = new GroupsDataSource(context);
        groupsDataSource.open();
        ArrayList<Group> groups = groupsDataSource.getAllGroupsForUser(AuthTokenService.getPayloadData("username"));
        groupsDataSource.close();

        Collections.sort(groups, new Comparator<Group>() {
            @Override
            public int compare(Group group, Group t1) {
                return t1.getDate().compareTo(group.getDate());
            }
        });

        for (Group g:groups) {
            Log.e("after sort", g.getGroupName() + " " + g.getDate());
        }

        ArrayList<UserGroup> sorted = new ArrayList<>();
        for (Group g:groups) {
            for (UserGroup ug:allChats) {
                Log.e("test", g.getGroupName() + " " + ug.group);
                if (g.getGroupName().equals(ug.group)){
                    sorted.add(ug);
                }
            }
        }

        for (UserGroup ug:allChats) {
            if (!sorted.contains(ug)){
                sorted.add(ug);
            }
        }

        allChats = sorted;
        chats = allChats;
        adapter.notifyDataSetChanged();
    }
}
