package com.example.androidchatapp.Services;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.Models.NewGroup;
import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.R;
import com.example.androidchatapp.chat_screen.ChatDataStorage;
import com.example.androidchatapp.main_screen.ChatListDataStorage;
import com.example.androidchatapp.main_screen.ChatsListAdapter;
import com.example.androidchatapp.main_screen.MainActivity;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChatService {

    public static UserGroup chat;
    public static boolean checkIfNewChat = false;
    public static int notificationId = 0;
    public static byte[] byteArr = null;
    public static String messageToSend = "";

    public static void rejoinGroups(final Context context, final String username, final String authToken){
        final String rejoinGroupsURL = context.getApplicationContext().getString(R.string.ChatServiceBaseURL) + context.getApplicationContext().getString(R.string.rejoin) + "?username=" + username;
        StringRequest request = new StringRequest(StringRequest.Method.POST, rejoinGroupsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("info", "rejoined groups");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "failed to rejoin groups due to " + error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", "Bearer " + authToken);
                return authHeader;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    public static void getAllGroupsForUser(final Context context, final String username, final ChatsListAdapter adapter, final String groupToRemove, final String authToken){
        final String url = context.getApplicationContext().getString(R.string.ChatServiceBaseURL) + context.getApplicationContext().getString(R.string.getAllGroupsUser) + "?username=" + username;
        JsonArrayRequest getGroups = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("blabla", response.toString());

                Gson gson = new Gson();
                Type type = new TypeToken<List<UserGroup>>(){}.getType();
                ChatListDataStorage.allChats = gson.fromJson(response.toString(), type);

                for (Iterator<UserGroup> iterator = ChatListDataStorage.allChats.iterator(); iterator.hasNext();){
                    if (iterator.next().chatName.equals(groupToRemove)){
                        iterator.remove();
                    }
                }
                ChatListDataStorage.chats = ChatListDataStorage.allChats;
                for (UserGroup ug:ChatListDataStorage.allChats) {
                    //Log.e("getAll", ug.chatName);
                    Log.e("getAll", ug.group);
                    Log.e("getAll", ug.username);
                }
                ChatListDataStorage.sortGroups(context, adapter);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error retrieving data from server", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> authHeader = new HashMap<>();
                authHeader.put("Authorization", "Bearer " + authToken);
                return authHeader;
            }
        };

        Volley.newRequestQueue(context).add(getGroups);
    }

    public static void createChatIfNotExists(final Context context, String username1, String username2){

        if (ChatService.checkIfNewChat && !ChatListDataStorage.allChats.contains(ChatService.chat)){
            JSONObject test = new JSONObject();
            JSONObject data = new JSONObject();

            try {
                test.put("type", "event");
                test.put("event", "createChat");
                //test.put("ackId", 1);
                test.put("dataType", "json");
                data.put("username1",  username1);
                data.put("username2", username2);
                test.put("data", data);
            } catch(JSONException e){
                Log.e("JSONException", e.toString());
            }

            Intent serviceIntent = new Intent(context, TestService.class);
            serviceIntent.putExtra("message", test.toString());
            Log.e("service", "intent start service WebPubSubConService");
            context.startService(serviceIntent);

            ChatListDataStorage.allChats.add(ChatService.chat);
        }
    }

    public static void createGroupChat(final Context context, final NewGroup group, final String authToken){

        String url = context.getString(R.string.ChatServiceBaseURL) + context.getString(R.string.createGroup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "created group " + group.getName(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Failed to crate group, error - " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> authHeader = new HashMap<>();
            authHeader.put("Authorization", "Bearer " + authToken);
            return authHeader;
            }

            @Override
            public byte[] getBody() {
                Gson gson = new Gson();
                String groupJson = gson.toJson(group);

                return groupJson.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static void sendImage(Context context, String group, String message){
        Log.e("info", "send image");
        JSONObject test = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            test.put("type", "event");
            test.put("event", "testevent");
            //test.put("ackId", 1);
            //change data to json and send group and message
            test.put("dataType", "json");
            if(byteArr != null){
                data.put("image",  Base64.encodeToString(byteArr, Base64.NO_WRAP));
            }
            data.put("message",  message);
            data.put("group", group);
            test.put("data", data);
        } catch (JSONException e){
            Log.e("info", "JSON exception");
        }

        byteArr = null;
        Intent serviceIntent = new Intent(context, TestService.class);
        //Bundle bundle = new Bundle();
        //bundle.putString("messageBundle", test.toString());
        //serviceIntent.putExtra("message", test.toString());
        //serviceIntent.putExtras(bundle);
        serviceIntent.putExtra("message", "");
        messageToSend = test.toString();
        Log.e("service", "intent start service WebPubSubConService");
        context.startService(serviceIntent);
    }

    public static void showNotification(Context context){
        Log.i("service", "show notification");
        String CHANNEL_ID = context.getString(R.string.channel_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification")
                .setContentText("Notification test")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId++, builder.build());
    }

    public static void leaveGroup(final Context context) {
        Log.e("info", "leave group");
        JSONObject test = new JSONObject();
        try {
            test.put("type", "event");
            test.put("event", "leaveGroup");
            //test.put("ackId", 1);
            //change data to json and send group and message
            test.put("dataType", "text");
            test.put("data", ChatService.chat.group);
        } catch (JSONException e){
            Log.e("info", "JSON exception");
        }

        byteArr = null;
        Intent serviceIntent = new Intent(context, TestService.class);
        //Bundle bundle = new Bundle();
        //bundle.putString("messageBundle", test.toString());
        //serviceIntent.putExtra("message", test.toString());
        //serviceIntent.putExtras(bundle);
        serviceIntent.putExtra("message", "");
        messageToSend = test.toString();
        Log.e("service", "intent start service WebPubSubConService");
        context.startService(serviceIntent);
    }
}
