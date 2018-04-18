package com.surf.saad.app.data;

import java.util.Map;

public class User {
    private String id;
    private String name;
    private String password;
    private Map<String, String> chatRoomList;

    public User() {
    }

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getChatRoomList() {
        return chatRoomList;
    }

    public void setChatRoomList(Map<String, String> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", chatRoomList=" + chatRoomList +
                '}';
    }
}
