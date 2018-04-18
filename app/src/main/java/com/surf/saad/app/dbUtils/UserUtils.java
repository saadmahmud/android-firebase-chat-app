package com.surf.saad.app.dbUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.surf.saad.app.data.User;

public class UserUtils {

    public static void getUserFromDb(DatabaseReference mUserRef, String
            userId, ValueEventListener valueEventListener) {

        mUserRef.child(userId).addValueEventListener(valueEventListener);
    }

    public static void addChatRoomInfo(DatabaseReference mDatabase, String userId, String chatRoomId) {
        mDatabase.child(userId)
                .child(DbKeys.Users.chatRoomList)
                .child(chatRoomId)
                .setValue("true");
    }

    public static void registerUser(DatabaseReference mDatabase
            , User user
            , OnSuccessListener successListener) {
        mDatabase.child(user.getId())
                .setValue(user)
                .addOnSuccessListener(successListener);
    }

}
