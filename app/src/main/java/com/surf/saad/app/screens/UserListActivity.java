package com.surf.saad.app.screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.surf.saad.app.adapters.UserListAdapter;
import com.surf.saad.app.data.ChatRoom;
import com.surf.saad.app.data.Constants;
import com.surf.saad.app.data.User;
import com.surf.saad.app.R;
import com.surf.saad.app.SurfApp;
import com.surf.saad.app.appUtils.OnUserClick;
import com.surf.saad.app.appUtils.UserPref;
import com.surf.saad.app.dbUtils.DatabaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListActivity extends AppCompatActivity implements OnUserClick {
    RecyclerView recyclerViewChatList;
    UserListAdapter chatListAdapter;
    LinearLayoutManager mLayoutManager;

    DatabaseUtils databaseUtils;

    List<String> chatRoomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle(R.string.userList);
        databaseUtils = DatabaseUtils.getInstance(this);

        chatRoomList = new ArrayList<>();

        addChatRoomIdListener();

        initView();
    }

    private void addChatRoomIdListener() {
        databaseUtils.getChatroomListForThisUser(UserPref.getUser(this)
                .getId(), userChatListListener);
    }

    private void initView() {
        recyclerViewChatList = findViewById(R.id.recyclerViewUsers);
        chatListAdapter = new UserListAdapter(this, databaseUtils, this);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewChatList.setLayoutManager(mLayoutManager);

        recyclerViewChatList.setAdapter(chatListAdapter);
    }

    @Override
    public void onUserClick(User selectedUser) {
        String chatRoomId = getChatRoomId(selectedUser);

        if (chatRoomId == null) {
            chatRoomId = databaseUtils.createChatRoom();
            databaseUtils.joinChatRoom(chatRoomId, selectedUser);
            databaseUtils.addChatRoomIdToUserInfo(selectedUser.getId(), chatRoomId);

            databaseUtils.joinChatRoom(chatRoomId, UserPref.getUser(this));
            databaseUtils.addChatRoomIdToUserInfo(UserPref.getUser(this).getId(), chatRoomId);
        }

        showLog(chatRoomId);
        openChatRoom(chatRoomId, selectedUser);
    }

    ChildEventListener userChatListListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            chatRoomList.add(dataSnapshot.getKey());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private String getChatRoomId(User selectedUser) {
        Object[] selectedUserChatList = selectedUser.getChatRoomList().keySet().toArray();
        for (Object chatRoomId : selectedUserChatList) {
            // if users have previously shared chatroom then return chatroom id
            if (chatRoomList.contains(chatRoomId.toString())) {
                return chatRoomId.toString();
            }
        }
        return null;
    }

    private void openChatRoom(String chatRoomId, User selectedUser) {
        Intent intent = new Intent(UserListActivity.this, ChatRoomActivity.class);
        intent.putExtra(Constants.CHAT_ROOM_ID, chatRoomId);
        intent.putExtra(Constants.USER_NAME, selectedUser.getName());
        startActivity(intent);
    }

    private void showLog(String s) {
        Log.d("BJIT " + this.getClass().getName(), s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.u_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatListActivity:
                openChatListPage();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openChatListPage() {
        Intent intent = new Intent(this, ChatListActivity.class);
        startActivity(intent);
    }

    private void logout() {
        SurfApp.getInstance().logout(this);
        SurfApp.getInstance().launchRegActivity(this);
        finish();
    }
}