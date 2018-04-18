package com.surf.saad.app.dbUtils;

public final class DbKeys {
    public interface Messages {

        String messages = "messages";

        String chatroomId = "chatroomId";
        String messageId = "messageId";
        String messageStatus = "messageStatus";
        String messageText = "messageText";
        String messageTimestamp = "messageTimestamp";
        String userId = "userId";
        String hasAttachment = "hasAttachment";
        String attachmentDetail = "attachmentDetail";
        String attachmentName = "name";
        String attachmentUrl = "serverUrl";
    }

    public interface ChatRoom {

        String chatrooms = "chatrooms";

        String id = "chatroomId";
        String lastMessageId = "lastMessageId";
        String lastMessageStatus = "lastMessageStatus";
        String lastMessageText = "lastMessageText";
        String lastMessageTimestamp = "lastMessageTimestamp";
        String userList = "userList";
        String messageList = "messageList";
    }

    public interface Users {

        String users = "users";
        String id = "id";
        String name = "name";
        String chatRoomList = "chatRoomList";
    }
}
