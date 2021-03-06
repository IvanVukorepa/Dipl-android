package com.example.androidchatapp.main_screen;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.androidchatapp.DB.GroupsDataSource;
import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;
import com.example.androidchatapp.Services.MyPreferences;
import com.example.androidchatapp.Services.PubSubData;
import com.example.androidchatapp.Services.TestService;
import com.example.androidchatapp.Services.UserService;
import com.example.androidchatapp.chat_screen.ChatActivity;
import com.example.androidchatapp.chat_screen.ChatDataStorage;
import com.example.androidchatapp.create_group_screen.CreateGroup;
import com.example.androidchatapp.login_screen.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ChatsListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        listView = (ListView) findViewById(R.id.mainScreenList);
        adapter = new ChatsListAdapter(getApplicationContext());

        startLoginIfNotLoggedIn();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String chatName = ChatListDataStorage.chats.get(i);
                //ChatService.chatName = chatName;
                //add chatname to intent and set it in next activity, on destroy remove name and clear datastorage
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("userGroupPosition", i);
                startActivity(intent);
            }
        });

        final String username = AuthTokenService.getPayloadData("username");
        Log.i("error", username);

        // should probably be in Test/WebPubSub Service code
        //ChatService.rejoinGroups(getApplicationContext(), username);

        if (!username.isEmpty()) {
            Intent serviceIntent = new Intent(this, TestService.class);
            Log.e("service", "intent start service WebPubSubConService");
            startService(serviceIntent);
        }
    }

    private void startLoginIfNotLoggedIn() {
        MyPreferences preferences = new MyPreferences(getApplicationContext());

        String username = preferences.getString("Username");
        String token = preferences.getString("AuthToken");

        if (username.isEmpty() || token.isEmpty()){
            Intent intent;
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        } else {
            AuthTokenService.decodeToken(token, getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        listView.setAdapter(adapter);
        String groupToRemove = getIntent().getStringExtra("removeGroup");
        ChatListDataStorage.fillData(getApplicationContext(), adapter, groupToRemove);
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
                UserService.getAll(getApplicationContext(), adapter, s);
                ChatService.checkIfNewChat = true;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                // filter existing chats

                adapter.getFilter().filter(s);
                ChatService.checkIfNewChat = false;

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                UserService.logout(getApplicationContext());
                return true;
            case R.id.createGroup:
                Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void test(PubSubData data){
        Log.e("test", data.data.user + " " + data.data.message);

        if (!groupExists(data.group)){
            UserGroup ug = new UserGroup(AuthTokenService.getPayloadData("username"), data.group, data.data.user);
            ChatListDataStorage.allChats.add(ug);
            adapter.notifyDataSetChanged();

            GroupsDataSource groupsDataSource = new GroupsDataSource(getApplicationContext());
            groupsDataSource.open();
            groupsDataSource.addGroupToDB(data.group, AuthTokenService.getPayloadData("username"), data.data.time);
            groupsDataSource.close();
        }
        ChatService.showNotification(getApplicationContext(), data.data.user, data.data.message.substring(0, Math.min(data.data.message.length(), 30)), data.group);
    }

    private boolean groupExists(String group) {
        for (UserGroup ug: ChatListDataStorage.allChats) {
            if (ug.group.equals(group))
                return true;
        }
        return false;
    }
}