package com.surf.saad.app.dbUtils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.surf.saad.app.data.MessageContent;
import com.surf.saad.app.data.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.surf.saad.app.appUtils.AppUtils;

import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {
    private static final String TAG = "DatabaseUtils";
    private DatabaseReference mDatabase;
    private DatabaseReference mMessagesRef;
    private DatabaseReference mChatroomRef;
    private DatabaseReference mUserRef;
    private static DatabaseUtils mDatabaseUtils;

    private DatabaseUtils(Context context) {
        FirebaseApp.initializeApp(context);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessagesRef = mDatabase.child(DbKeys.Messages.messages);
        mChatroomRef = mDatabase.child(DbKeys.ChatRoom.chatrooms);
        mUserRef = mDatabase.child(DbKeys.Users.users);

    }

    public static DatabaseUtils getInstance(Context context) {
        if (mDatabaseUtils == null) {
            mDatabaseUtils = new DatabaseUtils(context);

        }
        return mDatabaseUtils;
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }

    public DatabaseReference getMessageReference() {
        return mMessagesRef;
    }

    public DatabaseReference getChatroomReference() {
        return mChatroomRef;
    }

    public DatabaseReference getUserReference() {
        if (mUserRef == null) {
            mUserRef = mDatabase.child(DbKeys.Users.users);

        }
        return mUserRef;
    }

    public void readFromDatabase() {
        // Read from the database
        mMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MessageContent messageContent = dataSnapshot.getValue(MessageContent.class);
                Log.d(TAG, "Value is Chat Id: " + messageContent.getChatroomId());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void writeNewPost(MessageContent messageContent) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/messages/", messageContent);

        mDatabase.updateChildren(childUpdates);
    }

    public String createMessageKey() {
        return MessagesUtils.createNewMessageKey(mDatabase);
    }

    public String sendMessage(MessageContent messageContent, @Nullable String
            messageKey, @Nullable OnSuccessListener onSuccessListener) {
        if (AppUtils.hasValue(messageKey)) {
            MessagesUtils.updateMessageInKey(mDatabase, messageContent,
                    messageKey, onSuccessListener);
        } else {
            messageKey = MessagesUtils.sendMessage(mDatabase, messageContent,
                    onSuccessListener);
        }
        return messageKey;

    }

    public void updateChatRoom(String chatRoomId, String messageKey,
                               OnSuccessListener onSuccessListener) {
        MessagesUtils.addMessageIdToChatRoom(mDatabase, chatRoomId, messageKey,
                onSuccessListener);

    }

    public void updateMessageStatus(String messageKey, Map<String, String>
            status) {
        mDatabase.child(DbKeys.Messages.messages).child(messageKey).child
                (DbKeys.Messages.messageStatus).setValue(status);
    }

    public void updateMessageAttachmentUrl(String messageKey, Map<String, String> attachmentDetail) {
        mDatabase.child(DbKeys.Messages.messages).child(messageKey).child
                (DbKeys.Messages.attachmentDetail).setValue(attachmentDetail);
    }

    public void registerUser(User user, OnSuccessListener successListener) {

        UserUtils.registerUser(mUserRef, user, successListener);
    }

    public void getUserFromDb(String userId, ValueEventListener listener) {

        UserUtils.getUserFromDb(mUserRef, userId, listener);
    }

    public String createChatRoom() {
        return ChatListUtils.createChatRoom(mChatroomRef);
    }

    public void joinChatRoom(String chatRoomId, User user) {
        ChatListUtils.joinChatRoom(mChatroomRef, chatRoomId, user);
    }

    public void addChatRoomIdToUserInfo(String userId, String chatRoomId) {
        UserUtils.addChatRoomInfo(mUserRef, userId,
                chatRoomId);
    }

    public void getChatRoom(Map<String, String> users) {
        ChatListUtils.getChatRoom(mChatroomRef, users);
    }

    public void getChatroomListForThisUser(String userId, ChildEventListener
            listener) {
        mUserRef.child(userId).child(DbKeys.Users.chatRoomList)
                .addChildEventListener(listener);
    }

    //    private void writeToDatabase() {
//        // Write a message to the database
//
//        mMesageRef.child("messages").setValue(new MessageContent(index++, "10001",
//                "hi", "seen",
//                "12039120392129"));
//    }
}
