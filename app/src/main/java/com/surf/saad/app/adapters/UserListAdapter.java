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
import com.surf.saad.app.data.User;
import com.surf.saad.app.R;
import com.surf.saad.app.appUtils.AppUtils;
import com.surf.saad.app.appUtils.OnUserClick;
import com.surf.saad.app.dbUtils.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private static final String TAG = "UserListAdapter";
    DatabaseUtils mDatabaseRef;
    Context mContext;
    OnUserClick mCallBack;
    private List<String> mChatRoomIds = new ArrayList<>();
    private List<User> mUserList = new ArrayList<>();

    public UserListAdapter(Context context, DatabaseUtils databaseRef,
                           OnUserClick callBack) {
        this.mDatabaseRef = databaseRef;
        this.mContext = context;
        this.mCallBack = callBack;

        DatabaseReference mUserRef = mDatabaseRef.getUserReference();

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null)
                    return;

                showLog("dataSnapshot user : " + dataSnapshot);
                showLog("dataSnapshot user : " + user.getChatRoomList());
                if (AppUtils.thisUser(mContext, user))
                    return;

                mChatRoomIds.add(dataSnapshot.getKey());
                mUserList.add(user);

                notifyItemInserted(mUserList.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("adapter onChildChanged", dataSnapshot.getValue().toString());
//
//                if (user.getId().equals(Preference.get(mContext, Preference
//                        .USER_NUMBER, "")) == false) {
//                mChatRoomIds.add(dataSnapshot.getKey());
//                mUserList.add(user);
//
//                notifyItemInserted(mUserList.size() - 1);
//                }
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

        mUserRef.addChildEventListener(listener);
    }

    private void showLog(String s) {
        Log.d(TAG, "BJIT " + s);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatroom_list_row, parent, false);

        return new UserListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUserList.get(position);
        holder.textViewUser.setText(user.getName());
        holder.textViewText.setText(""); // last message
        holder.textViewTime.setText(""); // last interaction time

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
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
