package com.example.androidchatapp.create_group_screen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.androidchatapp.DB.GroupsDataSource;
import com.example.androidchatapp.Models.Group;
import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;
import com.example.androidchatapp.Services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class CreateGroup extends AppCompatActivity {

    CreateGroupUserListAdapter createGroupUserListAdapter;
    ListView listView;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        listView = findViewById(R.id.createGroupList);
        floatingActionButton = findViewById(R.id.fabCreateGroup);
        CreateGroupUserDataStorage.selectedUsernames.clear();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = CreateGroupUserDataStorage.usernames.get(i);
                if (CreateGroupUserDataStorage.selectedUsernames.contains(username)){
                    CreateGroupUserDataStorage.selectedUsernames.remove(username);
                    view.setBackgroundColor(Color.WHITE);
                } else {
                    CreateGroupUserDataStorage.selectedUsernames.add(username);
                    view.setBackgroundColor(Color.BLUE);
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GroupDetails.class);
                startActivity(intent);
            }
        });

        createGroupUserListAdapter = new CreateGroupUserListAdapter(getApplicationContext());
        listView.setAdapter(createGroupUserListAdapter);


        CreateGroupUserDataStorage.fillData(getApplicationContext(), createGroupUserListAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                //request all possible chats from DB
                UserService.getAll(getApplicationContext(), createGroupUserListAdapter, s, true);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                // filter existing chats

                createGroupUserListAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


}