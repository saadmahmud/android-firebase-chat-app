package com.surf.saad.app.data;

import java.util.Map;

public class ChatRoom {
    String chatRoomId;
    Map<String, User> userList;

    Map<String, String> messageList;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomId, Map<String, User> userList) {
        this.chatRoomId = chatRoomId;
        this.userList = userList;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Map<String, User> getUserList() {
        return userList;
    }

    public void setUserList(Map<String, User> userList) {
        this.userList = userList;
    }

    public Map<String, String> getMessageList() {
        return messageList;
    }

    public void setMessageList(Map<String, String> messageList) {
        this.messageList = messageList;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "chatRoomId=" + chatRoomId +
                ", userList=" + userList +
                ", messageList=" + messageList +
                '}';
    }
}
