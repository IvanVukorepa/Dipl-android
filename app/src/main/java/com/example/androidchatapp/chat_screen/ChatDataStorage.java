package com.example.androidchatapp.chat_screen;

import android.content.Context;
import android.util.Log;

import com.example.androidchatapp.DB.MessageDatabse;
import com.example.androidchatapp.DB.MessagesDataSource;
import com.example.androidchatapp.Services.AuthTokenService;
import com.example.androidchatapp.Services.ChatService;
import com.example.androidchatapp.Services.Message;
import com.example.androidchatapp.Services.PubSubData;

import java.util.ArrayList;

public class ChatDataStorage {

    public static ArrayList<Message> messages = new ArrayList<>();

    public static void fillData(final Context context, final chatAdapter adapter, String chatName){
        if (chatName.equals(""))
            return;
        MessagesDataSource msgDataSource = new MessagesDataSource(context);

        msgDataSource.open();
        ArrayList<MessageDatabse> messagesDB = msgDataSource.getAllMessagesForChat(AuthTokenService.getPayloadData("username"), chatName);
        msgDataSource.close();

        for (MessageDatabse m: messagesDB) {
            Log.e("all messages", m.getChatName() + " " + m.getUsername() + " " + m.getMessageContent());
            Message message = new Message(m.getUsername(), m.getMessageContent());
            messages.add(message);
        }
        adapter.notifyDataSetChanged();
    }

    public static void addMessage(Context context, final PubSubData data, chatAdapter adapter){

        Log.e("a", ChatService.chat.toString());
        Log.e("a", data.group);

        if (ChatService.chat.group.equals(data.group)){
            messages.add(data.data);
            adapter.notifyDataSetChanged();
        } else{
            //show notification as we are in a different chat
            ChatService.showNotification(context);
        }
    }
}
