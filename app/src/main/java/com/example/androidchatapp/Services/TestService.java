package com.example.androidchatapp.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.DB.GroupsDataSource;
import com.example.androidchatapp.DB.MessagesDataSource;
import com.example.androidchatapp.Models.Group;
import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.Models.UserGroupMessages;
import com.example.androidchatapp.R;
import com.example.androidchatapp.main_screen.ChatListDataStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestService extends Service {
    private final IBinder binder = new Binder();
    public static WebSocketClient client;
    private static URI uri;
    private static String pubSubConnectionURL = "";
    private static int notificationId = 0;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (intent != null) {
                            Log.e("service", "service send event" + this.toString());

                            if(client != null && client.isOpen()){
                                if(!ChatService.messageToSend.equals("")){
                                    client.send(ChatService.messageToSend);
                                    ChatService.messageToSend = "";
                                } else {
                                    try {
                                        Toast.makeText(getApplicationContext(), "message to send is empty", Toast.LENGTH_SHORT).show();
                                    } catch (Exception ex) {

                                    }
                                }

                            } else{
                                //TODO: notify message not sent/possibly save message and send later
                                Log.e("messageError", "client not open");
                                try {
                                    Toast.makeText(getApplicationContext(), "message not sent", Toast.LENGTH_SHORT).show();
                                } catch (Exception ex){

                                }
                            }
                        }
                    }
                }
        ).start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e("service", "service started");

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e("service", "service destroyed");
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createWebSocket(){
        final String authToken = getToken(getApplicationContext());
        final String username = getUsername(getApplicationContext());
        Log.e("test createWebSocket", authToken + " " + username);
        getConnection(getApplicationContext(), username, authToken, new ServerCalback() {
            @Override
            public void onSucess(String url) {
                Log.e("url", url);
                try{
                    uri = new URI(url);
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Sec-WebSocket-Protocol", "json.webpubsub.azure.v1");
                    client = new WebSocketClient(uri, headers) {
                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            Log.e("faaf", "connection opened");
                            ChatService.rejoinGroups(getApplicationContext(), username);
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.e("faaf", "message" + message + "received");
                            handleMessage(message, getApplicationContext());
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            Log.e("faaf", "client closed due to" + reason);
                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.e("faaf", "error" + ex.toString());

                        }
                    };
                    try{
                        client.connectBlocking();
                    } catch(InterruptedException ex){
                        Log.e("exception", ex.toString());
                    }

                } catch (URISyntaxException e){
                    Log.e("error", "uri syntax exception");
                }
            }
        });
    }

    public void getConnection(final Context context, final String username, final String authToken, final ServerCalback callback) {
        Log.e("getconnection", "get connection called for user " + username);
        if (TestService.pubSubConnectionURL.isEmpty()){
            final String negotiateURL = context.getApplicationContext().getString(R.string.ChatServiceBaseURL) + context.getApplicationContext().getString(R.string.negotiate) + "?userId=" + username;
            JsonObjectRequest request = new JsonObjectRequest(negotiateURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("error", response.toString());
                        String url = response.getString("url");
                        Log.e("error", "negotiate finished");
                        TestService.pubSubConnectionURL = url;
                        callback.onSucess(TestService.pubSubConnectionURL);

                    } catch (JSONException e) {
                        Log.e("error", "failed to get connection url");
                        Log.e("error", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", "failed to get connection url");
                    Log.e("error", negotiateURL);
                    Log.e("error", error.toString());
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

        } else{
            callback.onSucess(TestService.pubSubConnectionURL);
        }
    }

    public String getToken(final Context context){
        MyPreferences preferences = new MyPreferences(context);

        return preferences.getString("AuthToken");
    }

    public String getUsername(final Context context){
        MyPreferences preferences = new MyPreferences(context);

        return preferences.getString("Username");
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

    public void handleMessage(String message, Context context){
        Gson gson = new Gson();
        Type type = new TypeToken<PubSubData>(){}.getType();
        PubSubData data = gson.fromJson(message, type);
        if (data.event != null && data.event.equals("connected")){
            Log.e("event", "connected to pubsub");
            return;
        }
        processMessage(context, data);

    }

    private void processMessage(Context context, PubSubData data){
        if (EventBus.getDefault().hasSubscriberForEvent(PubSubData.class)){
            EventBus.getDefault().post(data);
            //showNotification(getApplicationContext());
        } else{
            showNotification(getApplicationContext());
        }

        MessagesDataSource msgDataSource = new MessagesDataSource(context);
        msgDataSource.open();
        msgDataSource.addMessageToDB(getUsername(context), data.data.user, data.group, data.data.message, data.data.imageURI, data.data.time, data.data.guid);
        msgDataSource.close();

        GroupsDataSource groupsDataSource = new GroupsDataSource(context);
        groupsDataSource.open();
        groupsDataSource.updateGroupData(getUsername(context), data.group, data.data.time, data.data.guid);
        groupsDataSource.close();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
                if (isNetworkAvailable(context)){
                    Log.e("network", "network available");
                    final String username = getUsername(getApplicationContext());
                    getUnreadMessages(context, username);
                    createWebSocket();
                } else {
                  Log.e("network", "network not available");
                }
            }
        }
    };


    private Boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    private void getUnreadMessages(final Context context, final String username){
        final String url = context.getApplicationContext().getString(R.string.ChatServiceBaseURL) + context.getApplicationContext().getString(R.string.getAllUnread) + "?username=" + username;
        JsonArrayRequest getGroups = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("unreadMessages", response.toString());

                Gson gson = new Gson();
                Type type = new TypeToken<List<UserGroupMessages>>(){}.getType();
                ArrayList<UserGroupMessages> userGroups = gson.fromJson(response.toString(), type);

                for (UserGroupMessages ug:userGroups) {
                    Log.e("getUnread", ug.group);
                    if (ug.message != null){
                        Log.e("getUnreadmess", ug.message + "");
                        type = new TypeToken<List<Message>>(){}.getType();
                        ArrayList<Message> messages = gson.fromJson(ug.message, type);
                        final String guid = getLastReadMessageGuid(ug.group, username);

                        boolean found = false;

                        for (Message m:messages) {
                            if(m.guid.equals(guid))
                                found = true;
                        }

                        for (Message m:messages) {
                            if(!found){
                                Log.e("getunreadtest", m.user + m.message + m.time);
                                PubSubData data = new PubSubData(ug.group, m);
                                processMessage(context, data);
                            } else {
                                if (m.guid.equals(guid))
                                    found = false;
                            }

                        }
                    }
                }
                //ChatListDataStorage.sortGroups(context, adapter);
                //adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error retrieving data from server", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(context).add(getGroups);
    }

    private String getLastReadMessageGuid(String groupName, String username){
        GroupsDataSource groupsDataSource = new GroupsDataSource(getApplicationContext());
        groupsDataSource.open();
        Group group = groupsDataSource.getGroupData(groupName, username);

        if (group == null)
            return "";
        return group.getGuid();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("testservice", "ontaskremoved");
        super.onTaskRemoved(rootIntent);
    }

    public static void closeClient(){
        client.close();
        pubSubConnectionURL = "";

    }
}
