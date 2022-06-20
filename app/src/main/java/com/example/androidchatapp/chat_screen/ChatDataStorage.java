package com.example.androidchatapp.chat_screen;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

    public static void fillData(final Context context, final chatAdapter adapter, String chatName, String username){
        if (chatName.equals(""))
            return;
        MessagesDataSource msgDataSource = new MessagesDataSource(context);

        msgDataSource.open();
        ArrayList<MessageDatabse> messagesDB = msgDataSource.getAllMessagesForChat(username, chatName);
        msgDataSource.close();

        messages.clear();
        for (MessageDatabse m: messagesDB) {
            Log.e("all messages", m.getChatName() + " " + m.getUsername() + " " + m.getMessageContent());
            Message message = new Message(m.getUsername(), m.getMessageContent(), m.getImageURI(), m.getDatetime(), m.getGuid());
            messages.add(message);
        }
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void addMessage(Context context, final PubSubData data, chatAdapter adapter){
        if (ChatService.chat.group.equals(data.group)){
            messages.add(data.data);
            adapter.notifyDataSetChanged();
        } else{
            //show notification as we are in a different chat
            ChatService.showNotification(context, data.data.user, data.data.message.substring(0, Math.min(data.data.message.length(), 30)), data.group);
        }
    }
}
