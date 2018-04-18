package com.surf.saad.app.dbUtils;
import com.google.firebase.database.DatabaseReference;
import com.surf.saad.app.data.User;

import java.util.Map;

public class ChatListUtils {

    public static String createChatRoom(DatabaseReference chatRoomRef) {
        DatabaseReference ref = chatRoomRef.push();

        return ref.getKey();
    }

    public static void getChatRoom(DatabaseReference chatRoomRef, Map<String, String> users) {
        chatRoomRef.orderByValue().equalTo("");
    }

    public static void joinChatRoom(DatabaseReference chatRoomRef, String
            chatRoomId, User user) {

        chatRoomRef.child(chatRoomId)
                .child(DbKeys.ChatRoom.userList)
                .child(user.getId())
                .setValue(user);
    }

}
