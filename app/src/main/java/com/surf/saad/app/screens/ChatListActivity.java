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

import com.surf.saad.app.adapters.ChatListAdapter;
import com.surf.saad.app.data.ChatRoom;
import com.surf.saad.app.R;
import com.surf.saad.app.SurfApp;
import com.surf.saad.app.appUtils.AppUtils;
import com.surf.saad.app.data.Constants;
import com.surf.saad.app.dbUtils.DatabaseUtils;
import com.surf.saad.app.appUtils.OnChatsClick;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity implements OnChatsClick {

    RecyclerView recyclerViewChatList;
    ChatListAdapter chatListAdapter;
    LinearLayoutManager mLayoutManager;

    DatabaseUtils databaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        databaseUtils = DatabaseUtils.getInstance(this);
        setTitle(R.string.chat_list);

        recyclerViewChatList = (RecyclerView) findViewById(R.id.recyclerViewChatrooms);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewChatList.setLayoutManager(mLayoutManager);

        chatListAdapter = new ChatListAdapter(this, databaseUtils,
                getChatroomIds(), this);

        recyclerViewChatList.setAdapter(chatListAdapter);
    }

    @Override
    public void onChatClick(final ChatRoom selectedChat) {
        openChatRoom(selectedChat);
    }

    private void showLog(String s) {
        Log.d(this.getClass().getName(), s);
    }

    private void openChatRoom(ChatRoom chatRoom) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra(Constants.CHAT_ROOM_ID, chatRoom.getChatRoomId());
        intent.putExtra(Constants.USER_NAME
                , AppUtils.getChatRoomName(chatRoom.getUserList(), this));

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.c_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.userListActivity:
                openUserListPage();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        SurfApp.getInstance().logout(this);
        SurfApp.getInstance().launchRegActivity(this);
        finish();
    }

    private void openUserListPage() {
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

    public List<String> getChatroomIds() {
        return new ArrayList<>();
    }
}