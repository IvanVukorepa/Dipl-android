package com.example.androidchatapp.chat_screen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;
import com.example.androidchatapp.Services.Message;
import com.example.androidchatapp.Services.PubSubData;
import com.example.androidchatapp.Services.TestService;
import com.example.androidchatapp.main_screen.ChatListDataStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Button testBtn, joinBtn;
    EditText newMessageET;
    ListView chatMessagesLV;
    chatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        testBtn = (Button) findViewById(R.id.button);
        joinBtn = (Button) findViewById(R.id.button2);
        newMessageET = (EditText) findViewById(R.id.editTextNewMessage);
        chatMessagesLV = (ListView) findViewById(R.id.chatMessagesList);

        adapter = new chatAdapter(getApplicationContext());
        chatMessagesLV.setAdapter(adapter);

        Intent intent = getIntent();
        int userGroupPosition = intent.getIntExtra("userGroupPosition", -1);

        ChatService.chat = ChatListDataStorage.chats.get(userGroupPosition);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("info", "send message clicked");
                JSONObject test = new JSONObject();
                try {
                    test.put("type", "event");
                    test.put("event", "testevent");
                    //test.put("ackId", 1);
                    //change data to json and send group and message
                    test.put("dataType", "text");
                    test.put("data", "[" + ChatService.chat.group + "]" + newMessageET.getText().toString());
                } catch (JSONException e){
                    Log.e("info", "JSON exception");
                }

                Intent serviceIntent = new Intent(getApplicationContext(), TestService.class);
                serviceIntent.putExtra("message", test.toString());
                Log.e("service", "intent start service WebPubSubConService");
                startService(serviceIntent);

            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject test = new JSONObject();
                JSONObject data = new JSONObject();

                try {
                    test.put("type", "event");
                    test.put("event", "joinGroup");
                    //test.put("ackId", 1);
                    test.put("dataType", "json");
                    data.put("username",  AuthTokenService.getPayloadData("username"));
                    data.put("group", "testGroup");
                    test.put("data", data);
                } catch(JSONException e){

                }

            }
        });

        ChatService.createChatIfNotExists(getApplicationContext(), AuthTokenService.getPayloadData("username"), ChatService.chat.chatName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        ChatDataStorage.fillData(getApplicationContext(), adapter, ChatService.chat.group);
        chatMessagesLV.smoothScrollToPosition(ChatDataStorage.messages.size());
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ChatDataStorage.messages.clear();
        ChatService.chat = null;

        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void test(PubSubData data){
        //handle event connected
        /*Gson gson = new Gson();
        Type type = new TypeToken<PubSubData>(){}.getType();
        PubSubData data = gson.fromJson(input, type);*/
        Log.e("test", data.data.user + " " + data.data.message);

        ChatDataStorage.addMessage(getApplicationContext(), data, adapter);
        Toast.makeText(getApplicationContext(), data.data.message, Toast.LENGTH_SHORT).show();
    }
}