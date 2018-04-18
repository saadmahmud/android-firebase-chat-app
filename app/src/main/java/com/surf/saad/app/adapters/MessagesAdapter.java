package com.surf.saad.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.surf.saad.app.data.Constants;
import com.surf.saad.app.data.MessageContent;
import com.surf.saad.app.R;
import com.surf.saad.app.appUtils.AppUtils;
import com.surf.saad.app.appUtils.SurfRecyclerViewListener;
import com.surf.saad.app.appUtils.UserPref;
import com.surf.saad.app.dbUtils.DatabaseUtils;
import com.surf.saad.app.dbUtils.DbKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final String TAG = "MessagesAdapter";
    DatabaseUtils mDatabaseRef;
    FirebaseStorage mFirebaseStorage;
    Context mContext;
    String mChatRoomId;
    SurfRecyclerViewListener mRecyclerViewListener;
    private List<String> mChatIds = new ArrayList<>();
    private List<MessageContent> mChats = new ArrayList<>();

    private final int OWN_VIEW = 0;
    private final int OTHER_VIEW = 1;

    public MessagesAdapter(Context context, String chatRoomId, DatabaseUtils databaseUtils,
                           FirebaseStorage firebaseStorage) {

        mContext = context;
        mDatabaseRef = databaseUtils;
        mFirebaseStorage = firebaseStorage;
        mChatRoomId = chatRoomId;
        mRecyclerViewListener = (SurfRecyclerViewListener) context;

        DatabaseReference mMesageRef = mDatabaseRef.getMessageReference();

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                MessageContent content = dataSnapshot.getValue(MessageContent.class);

                // Update RecyclerView
                mChatIds.add(dataSnapshot.getKey());
                mChats.add(content);
                notifyItemInserted(mChats.size() - 1);
                mRecyclerViewListener.scrollTo(mChats.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A message has changed, use the key to determine if we are
                // displaying this message and if so displayed the changed message.
                MessageContent content = dataSnapshot.getValue(MessageContent.class);
                String contentKey = dataSnapshot.getKey();

                int contentIndex = mChatIds.indexOf(contentKey);
                if (contentIndex > -1) {
                    // Replace with the new data
                    mChats.set(contentIndex, content);

                    // Update the RecyclerView
                    notifyItemChanged(contentIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + contentKey);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "messages:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load messages.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mMesageRef.orderByChild(DbKeys.Messages.chatroomId)
                .equalTo(mChatRoomId).addChildEventListener(listener);
    }

    @Override
    public int getItemViewType(int position) {
        return ownMessage(mChats.get(position)) ? OWN_VIEW : OTHER_VIEW;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view;
        if (viewType == OWN_VIEW) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_row_owner, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_row_others, parent, false);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MessageContent messageContent = mChats.get(position);
        holder.textViewUser.setText(messageContent.getUserId().substring(0, 4));
        holder.textViewText.setText(messageContent.getMessageText());
        holder.textViewTime.setText(AppUtils.getTimeFromTimestamp(messageContent.getMessageTimestamp()));

        if (messageContent.getMessageText().length() < 1) {
            holder.textViewText.setVisibility(View.GONE);
        } else {
            holder.textViewText.setVisibility(View.VISIBLE);
        }
        // if multiple messages came from same user at a time, use single
        // user picture/name for all consecutive messages
        if (position > 0) {
            MessageContent previousMessage = mChats.get(position - 1);
            if (previousMessage.getUserId().equals(messageContent.getUserId())) {
                holder.textViewUser.setVisibility(View.INVISIBLE);
                if (holder.userImage != null) {
                    holder.userImage.setVisibility(View.INVISIBLE);
                }
            } else {
                // if consecutive messages are from different user, put
                // a space between them
                holder.itemView.setPadding(0, mContext.getResources()
                        .getInteger(R.integer.chat_row_gap_different_user), 0, 0);

            }
        }

        // Own message row
        if (ownMessage(messageContent)) {
            holder.imageViewStatus.setImageResource(mChats.get(position)
                    .getMessageStatus() // TODO: need to change for particular user seen
                    .size() > 0 ? R.drawable.check_all : R.drawable.check);

        } else {
            // For other's message row
            // update Message Status to Seen
            if (messageContent.getMessageStatus().containsKey(AppUtils
                    .getDeviceName()) == false) { // DeviceName is User Id

                updateMessageStatusToSeen(position);

            }
        }

        if (mChats.get(position).isHasAttachment()) {
            holder.viewAttachment.setVisibility(View.VISIBLE);

            String url = mChats.get(position).getAttachmentDetail().get(DbKeys
                    .Messages.attachmentUrl);
            if (AppUtils.isImageUrl(url)) {
                holder.imageViewAttachment.setImageDrawable(mContext
                        .getResources().getDrawable(android.R.drawable
                                .ic_menu_report_image));
            }

            holder.viewAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(holder, position);
                    holder.imageButtonPausePlay.setVisibility(View.GONE);
                }
            });


            holder.imageButtonPausePlay.setVisibility(View.VISIBLE);
        } else {
            holder.viewAttachment.setVisibility(View.GONE);

        }

    }

    private void downloadFile(final ViewHolder holder, final int position) {
        String url = mChats.get(position).getAttachmentDetail().get(DbKeys
                .Messages.attachmentUrl);

        if (AppUtils.isImageUrl(url)) {
            StorageReference httpsReference = mFirebaseStorage.getReferenceFromUrl
                    (url);
            Log.d(mContext.getPackageName(), "url= " + url);
            Log.d(mContext.getPackageName(), "httpsReference= " +
                    httpsReference.getName());
            holder.attachmentLoadProgress.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(url)
                    .thumbnail(0.5f)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.attachmentLoadProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.attachmentLoadProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageViewAttachment);
        } else {
            // Attachment is not an Image
//                holder.imageViewAttachment.getLayoutParams().height = 20;
//                holder.imageViewAttachment.requestLayout();
        }
    }

    private void updateMessageStatusToSeen(int _position) {
        Map<String, String> status = new HashMap<>();
        status.put(AppUtils.getDeviceName(), Constants.STATUS_SEEN);

        mDatabaseRef.updateMessageStatus(mChatIds.get(_position), status);
    }

    private boolean ownMessage(MessageContent messageContent) {
        return messageContent.getUserId().equals(UserPref.getUser(mContext).getName());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser;
        TextView textViewText;
        TextView textViewTime;
        ImageView userImage;
        ImageView imageViewStatus;
        View viewAttachment;
        ImageView imageViewAttachment;
        ProgressBar attachmentLoadProgress;
        ImageButton imageButtonPausePlay;
        boolean ownMessage;

        public ViewHolder(View view) {
            super(view);
            textViewUser = (TextView) view.findViewById(R.id.textViewUser);
            textViewText = (TextView) view.findViewById(R.id.textViewText);
            textViewTime = (TextView) view.findViewById(R.id.textViewTime);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            imageViewStatus = (ImageView) view.findViewById(R.id.imageViewStatus);
            viewAttachment = view.findViewById(R.id.attachmentView);
            imageViewAttachment = (ImageView) view.findViewById(R.id.imageViewAttachment);
            attachmentLoadProgress = (ProgressBar) view.findViewById(R.id.attachmentLoadProgress);
            imageButtonPausePlay = (ImageButton) view.findViewById(R.id
                    .imageButtonPausePlay);
        }
    }

}
