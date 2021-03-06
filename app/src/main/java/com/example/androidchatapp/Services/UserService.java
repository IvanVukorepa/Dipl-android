package com.example.androidchatapp.Services;

import android.content.Context;
import android.content.Intent;
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
import com.example.androidchatapp.Models.UserGroup;
import com.example.androidchatapp.R;
import com.example.androidchatapp.create_group_screen.CreateGroup;
import com.example.androidchatapp.create_group_screen.CreateGroupUserDataStorage;
import com.example.androidchatapp.login_screen.LoginActivity;
import com.example.androidchatapp.chat_screen.ChatActivity;
import com.example.androidchatapp.main_screen.ChatListDataStorage;
import com.example.androidchatapp.main_screen.ChatsListAdapter;
import com.example.androidchatapp.main_screen.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.sql.StatementEvent;

public class UserService {

    public static void login(final Context context, final String username, final String password){
        final String loginURL = context.getString(R.string.UsersServiceBaseURL) + context.getString(R.string.Login);
        StringRequest loginRequest = new StringRequest(StringRequest.Method.POST, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String token = response;
                Log.i("token", token);

                if (AuthTokenService.decodeToken(token, context)){
                    Intent intent;
                    intent = new Intent(context.getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context.getApplicationContext(), "Failed to decode authentication token", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context.getApplicationContext(),"username or password is wrong", Toast.LENGTH_SHORT).show();
                Log.e("login error", error.toString());
            }
        }){
            @Override
            public byte[] getBody() {
                JSONObject user = new JSONObject();
                try{
                    user.put("Id", username);
                    user.put("username", username);
                    user.put("Password", password);
                } catch(JSONException e){ e.printStackTrace(); }

                return user.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(context).add(loginRequest);
    }

    public static void createUser(final Context context, final JSONObject user){
        String url = context.getString(R.string.UsersServiceBaseURL) + context.getString(R.string.addUser);

        StringRequest registerUser = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        Toast.makeText(context, "User " + user.optString("Username", "") + " successfully registered", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {

                return user.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(context).add(registerUser);
    }

    public static void getAll(final Context context, final BaseAdapter adapter, final String filter){
        getAll(context, adapter, filter, false);
    }

    public static void getAll(final Context context, final BaseAdapter adapter, final String filter, final Boolean second){
        if (filter.equals("")){
            Toast.makeText(context, "Filter can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final String url = context.getApplicationContext().getString(R.string.UsersServiceBaseURL) + context.getApplicationContext().getString(R.string.getAllUsers) + "?filter=" + filter;
        JsonArrayRequest getGroups = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("blabla", response.toString());

                Gson gson = new Gson();
                Type type = new TypeToken<List<String>>(){}.getType();
                ArrayList<String> users = gson.fromJson(response.toString(), type);

                if (second){
                    Log.e("usrs", "test");
                    for (String s:users) {
                        Log.e("usrs", s);
                    }
                    CreateGroupUserDataStorage.usernames.clear();
                    CreateGroupUserDataStorage.usernames.addAll(users);
                    CreateGroupUserDataStorage.usernames.addAll(CreateGroupUserDataStorage.selectedUsernames);
                }else{
                    ArrayList<UserGroup> userGroups = new ArrayList<>();
                    for (String u:users) {
                        if (u != null){
                            String group = getGroupName(AuthTokenService.getPayloadData("username"), u);
                            UserGroup ug = new UserGroup(AuthTokenService.getPayloadData("username"), group, u);
                            userGroups.add(ug);
                        }
                    }
                    ChatListDataStorage.chats = userGroups;
                }

                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error retrieving data from server", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(context).add(getGroups);
    }

    public static void logout (Context context){
        MyPreferences preferences = new MyPreferences(context);

        preferences.setString("AuthToken", "");
        preferences.setString("Username", "");


        TestService.closeClient();
        Intent intentService = new Intent(context, TestService.class);
        context.stopService(intentService);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static String getGroupName(String username1, String username2){
        String name = "";
        if (username1.compareTo(username2) < 0){
            name = username1 + "_" + username2;
        } else{
            name = username2 + "_" + username1;
        }
        return name;
    }
}
