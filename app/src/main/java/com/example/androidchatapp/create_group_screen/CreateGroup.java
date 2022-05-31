package com.example.androidchatapp.create_group_screen;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.androidchatapp.DB.GroupsDataSource;
import com.example.androidchatapp.Models.Group;
import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class CreateGroup extends AppCompatActivity {

    CreateGroupUserListAdapter createGroupUserListAdapter;
    ListView listView;
    FloatingActionButton floatingActionButton;

    ArrayList<String> selectedUsernames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        listView = (ListView) findViewById(R.id.createGroupList);
        floatingActionButton = findViewById(R.id.fab);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = CreateGroupUserDataStorage.usernames.get(i);
                if (selectedUsernames.contains(username)){
                    selectedUsernames.remove(username);
                    view.setBackgroundColor(Color.WHITE);
                } else {
                    selectedUsernames.add(username);
                    view.setBackgroundColor(Color.BLUE);
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create group
            }
        });

        createGroupUserListAdapter = new CreateGroupUserListAdapter(getApplicationContext());
        listView.setAdapter(createGroupUserListAdapter);


        CreateGroupUserDataStorage.fillData(getApplicationContext(), createGroupUserListAdapter);

    }


}