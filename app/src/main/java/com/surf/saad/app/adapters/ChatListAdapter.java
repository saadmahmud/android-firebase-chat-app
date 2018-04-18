package com.surf.saad.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.surf.saad.app.data.ChatRoom;
import com.surf.saad.app.data.User;
import com.surf.saad.app.R;
import com.surf.saad.app.appUtils.UserPref;
import com.surf.saad.app.dbUtils.DatabaseUtils;
import com.surf.saad.app.appUtils.OnChatsClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private static final String TAG = "ChatListAdapter";
    DatabaseUtils mDatabaseRef;
    Context mContext;
    OnChatsClick mCallBack;
    private List<String> mChatRoomIds;
    private List<ChatRoom> mChats = new ArrayList<>();
    private User thisUser;

    public ChatListAdapter(Context context, DatabaseUtils databaseRef
            , List<String> chatRoomIds, OnChatsClick callBack) {
        this.mDatabaseRef = databaseRef;
        this.mContext = context;
        this.mChatRoomIds = chatRoomIds;
        this.mCallBack = callBack;
        thisUser = UserPref.getUser(context);

        addChatRoomDetailsListener();
    }

    private void addChatRoomDetailsListener() {
        ChildEventListener chatListDetailsListner = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null)
                    return;

                ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                chatRoom.setChatRoomId(dataSnapshot.getKey());

                if (chatRoom.getUserList().containsKey(thisUser.getId())) {
                    mChatRoomIds.add(dataSnapshot.getKey());
                    mChats.add(chatRoom);

                    notifyItemInserted(mChats.size() - 1);
                }
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
                Log.w(TAG, "chat:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load chat rooms.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        DatabaseReference mChatRoomListRef = mDatabaseRef.getChatroomReference();

        mChatRoomListRef.addChildEventListener(chatListDetailsListner);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatroom_list_row, parent, false);

        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ChatRoom chatRoom = mChats.get(position);
        Log.d(TAG, "chat list size " + mChats.size());

        String userName = "";
        Map<String, User> userMap = chatRoom.getUserList();

        if (userMap == null)
            return;

        for (String userId : userMap.keySet()) {
            if (!UserPref.isThisUser(mContext, userId)) {
                if (userName.length() > 0) {
                    userName += ", ";
                }
                userName += userMap.get(userId).getName();
            }
        }
        holder.textViewUser.setText(userName);
        holder.textViewText.setText("");
        holder.textViewTime.setText("");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.onChatClick(chatRoom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser;
        TextView textViewText;
        TextView textViewTime;
        ImageView imageViewStatus;

        public ViewHolder(View view) {
            super(view);
            textViewUser = (TextView) view.findViewById(R.id.textViewUser);
            textViewText = (TextView) view.findViewById(R.id.textViewText);
            textViewTime = (TextView) view.findViewById(R.id.textViewTime);
            imageViewStatus = (ImageView) view.findViewById(R.id.imageViewStatus);

        }
    }
}
