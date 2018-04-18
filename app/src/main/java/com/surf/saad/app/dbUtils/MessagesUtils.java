package com.surf.saad.app.dbUtils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.surf.saad.app.data.MessageContent;

public class MessagesUtils {


    public static String createNewMessageKey(DatabaseReference mDatabase) {
        // Getting the key first before submitting a message to that key
        return mDatabase.child(DbKeys.Messages.messages).push().getKey();
    }

    public static String sendMessage(DatabaseReference mDatabase,
                                     MessageContent messageContent,
                                     @Nullable OnSuccessListener onSuccessListener) {
        // submit a message
        DatabaseReference msgRef = mDatabase.child(DbKeys.Messages.messages)
                .push();

        msgRef.setValue(messageContent).addOnSuccessListener(onSuccessListener);

        return msgRef.getKey();

    }

    public static void updateMessageInKey(DatabaseReference mDatabase
            , MessageContent messageContent, String messageKey, @Nullable OnSuccessListener onSuccessListener) {
        // submit a message
        mDatabase.child(DbKeys.Messages.messages).child(messageKey).setValue
                (messageContent).addOnSuccessListener(onSuccessListener);

    }

    public static void addMessageIdToChatRoom(DatabaseReference mDatabase, String chatRoomId, String messageKey, OnSuccessListener onSuccessListener) {

        Log.d("addMessageIdToChatRoom", "chatRoomId = " + chatRoomId + ", messageKey = " +
                messageKey);
        mDatabase.child(DbKeys.ChatRoom.chatrooms)
                .child(chatRoomId)
                .child(DbKeys.ChatRoom.messageList)
                .child(messageKey)
                .setValue("true");
    }
}
