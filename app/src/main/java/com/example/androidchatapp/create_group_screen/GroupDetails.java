package com.example.androidchatapp.create_group_screen;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.Models.NewGroup;
import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;
import com.example.androidchatapp.main_screen.ChatListDataStorage;
import com.example.androidchatapp.main_screen.ChatsListAdapter;
import com.example.androidchatapp.main_screen.MainActivity;

import java.util.ArrayList;

public class GroupDetails extends AppCompatActivity {

    EditText groupName;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        groupName = findViewById(R.id.editTextGroupName);
        floatingActionButton = findViewById(R.id.fabGroupDetails);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateGroupUserDataStorage.selectedUsernames.add(AuthTokenService.getPayloadData("username"));
                NewGroup newGroup = new NewGroup(CreateGroupUserDataStorage.selectedUsernames, groupName.getText().toString());
                ChatService.createGroupChat(getApplicationContext(), newGroup, AuthTokenService.getToken());

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}