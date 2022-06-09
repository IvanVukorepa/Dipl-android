package com.example.androidchatapp.chat_screen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.R;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;
import com.example.androidchatapp.Services.ImageHandler;
import com.example.androidchatapp.Services.Message;
import com.example.androidchatapp.Services.PubSubData;
import com.example.androidchatapp.Services.TestService;
import com.example.androidchatapp.create_group_screen.CreateGroup;
import com.example.androidchatapp.main_screen.ChatListDataStorage;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Button testBtn, joinBtn, imageBtn;
    EditText newMessageET;
    ListView chatMessagesLV;
    chatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        testBtn = (Button) findViewById(R.id.button);
        joinBtn = (Button) findViewById(R.id.button2);
        imageBtn = findViewById(R.id.imagebtn);
        newMessageET = (EditText) findViewById(R.id.editTextNewMessage);
        chatMessagesLV = (ListView) findViewById(R.id.chatMessagesList);

        adapter = new chatAdapter(getApplicationContext());
        chatMessagesLV.setAdapter(adapter);

        Intent intent = getIntent();
        int userGroupPosition = intent.getIntExtra("userGroupPosition", -1);

        ChatService.chat = ChatListDataStorage.chats.get(userGroupPosition);
        setTitle(ChatService.chat.chatName);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("info", "send message clicked");

                ChatService.sendImage(getApplicationContext(), ChatService.chat.group, newMessageET.getText().toString());
                /*JSONObject test = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    test.put("type", "event");
                    test.put("event", "testevent");
                    //test.put("ackId", 1);
                    //change data to json and send group and message
                    test.put("dataType", "json");
                    data.put("message",  newMessageET.getText().toString());
                    data.put("group", ChatService.chat.group);
                    //test.put("data", "[" + ChatService.chat.group + "]" + newMessageET.getText().toString());
                    test.put("data", data);
                } catch (JSONException e){
                    Log.e("info", "JSON exception");
                }

                Intent serviceIntent = new Intent(getApplicationContext(), TestService.class);
                serviceIntent.putExtra("message", test.toString());
                Log.e("service", "intent start service WebPubSubConService");
                startService(serviceIntent);*/

            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentImageSelect = new Intent(Intent.ACTION_GET_CONTENT);
                intentImageSelect.setType("image/*");
                // replace request code
                startActivityForResult(intentImageSelect, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            if (data == null){
                Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            } else {
                try{
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ByteStreams.copy(inputStream, baos);

                    Bitmap bmp;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int quality = 100;
                    do
                    {
                        byteArrayOutputStream.reset();
                        bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
                        bmp.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                        Log.e("imagesize", "size is " + byteArrayOutputStream.size() + ", quality " + quality);
                        if (quality > 20) {
                            quality -= 10;
                        } else if (quality > 5) {
                            quality -= 5;
                        } else {
                            quality -= 1;
                        }
                    }while (byteArrayOutputStream.size() > 600000 && quality > 0);

                    ChatService.byteArr = byteArrayOutputStream.toByteArray();
                    inputStream.read(ChatService.byteArr);

                    /*InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    Log.e("imagesize", "size is " + bmp.getByteCount());

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    //check what the max size should be
                    bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                    Bitmap bmpReduced = ImageHandler.reduceSize(bmp, 500000);

                    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                    bmpReduced.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2);

                    ChatService.byteArr = byteArrayOutputStream2.toByteArray();
                    inputStream.read(ChatService.byteArr);
                    ChatService.sendImage(getApplicationContext());*/
                } catch (Exception ex){
                    Log.e("image exception", ex.toString());
                    Toast.makeText(getApplicationContext(), "exception occured + " + ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                return true;
            case R.id.createGroup:
                Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(intent);
                return true;
            case R.id.leaveGroup:
                ChatService.leaveGroup(getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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