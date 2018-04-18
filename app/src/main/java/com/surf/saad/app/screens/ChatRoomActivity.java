package com.surf.saad.app.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;
import com.surf.saad.app.SurfApp;
import com.surf.saad.app.adapters.MessagesAdapter;
import com.surf.saad.app.R;
import com.surf.saad.app.appUtils.AppUtils;
import com.surf.saad.app.data.MessageContent;
import com.surf.saad.app.appUtils.FileUtils;
import com.surf.saad.app.appUtils.StorageHandler;
import com.surf.saad.app.appUtils.SurfRecyclerViewListener;
import com.surf.saad.app.appUtils.UserPref;
import com.surf.saad.app.dbUtils.DatabaseUtils;
import com.surf.saad.app.dbUtils.DbKeys;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.surf.saad.app.data.Constants.CHAT_ROOM_ID;
import static com.surf.saad.app.data.Constants.PERMISSIONS_REQUEST_STORAGE;
import static com.surf.saad.app.data.Constants.SELECT_FILE_REQUEST_CODE;
import static com.surf.saad.app.data.Constants.USER_NAME;

public class ChatRoomActivity extends AppCompatActivity implements SurfRecyclerViewListener {
    EditText editText;
    RecyclerView recyclerViewMessages;

    int msgIdIndex = 0;
    DatabaseUtils databaseUtils;
    String mChatRoomId;
    LinearLayoutManager mLayoutManager;
    MessagesAdapter messagesAdapter;

    StorageHandler storageHandler;

    private Uri file;
    private StorageMetadata metadata;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("onCreate");
        setContentView(R.layout.activity_chat_room);
        databaseUtils = DatabaseUtils.getInstance(this);
        storageHandler = new StorageHandler();

        mChatRoomId = getIntent().getStringExtra(CHAT_ROOM_ID);
        String userName = getIntent().getStringExtra(USER_NAME);

        setTitle(getString(R.string.chatting_with_text) + " " + userName);

        editText = findViewById(R.id.editText);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(mLayoutManager);
        recyclerViewMessages.setItemAnimator(new DefaultItemAnimator());

        messagesAdapter = new MessagesAdapter(this, mChatRoomId, databaseUtils, mFirebaseStorage);
        recyclerViewMessages.setAdapter(messagesAdapter);
    }

    public void addFile(View view) {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            AppUtils.openFileSelectPage(ChatRoomActivity.this, getString(R.string
                            .select_file),
                    SELECT_FILE_REQUEST_CODE);

        } else {
            // Ask for permission

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, getString(R.string
                        .storage_permission_explaination), Toast.LENGTH_LONG).show();
            }

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_STORAGE);
        }

    }

    public void writeNewPost(View view) {
        sendMessageToDb(false, null, null, null);
    }

    private void sendAttachmentToDb(final Uri data) {
        Map<String, String> attachmentDetail = new HashMap<>();
        attachmentDetail.put(DbKeys.Messages.attachmentName, "");
        attachmentDetail.put(DbKeys.Messages.attachmentUrl, "");

        final String messageKey = databaseUtils.createMessageKey();

        OnSuccessListener onSuccessListener = new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadFiletoServer(data, messageKey);
            }
        };

        sendMessageToDb(true, messageKey, attachmentDetail, onSuccessListener);
    }

    private void sendMessageToDb(boolean hasAttachment, @Nullable String
            messageKey, @Nullable Map<String, String> attachmentDetail
            , @Nullable OnSuccessListener onSuccessListener) {
        String content = editText.getText().toString();
        editText.setText("");
        String dateTime = String.valueOf(System.currentTimeMillis());
        Map<String, String> status = new HashMap<>();
        String userName = UserPref.getUser(this).getName();

        MessageContent messageContent = new MessageContent(mChatRoomId
                , ++msgIdIndex + ""
                , content, dateTime, userName, status);

        messageContent.setHasAttachment(hasAttachment);

        if (attachmentDetail != null)
            messageContent.setAttachmentDetail(attachmentDetail);

        showLog("messageKey= " + messageKey);

        messageKey = databaseUtils.sendMessage(messageContent, messageKey,
                onSuccessListener);
        databaseUtils.updateChatRoom(mChatRoomId, messageKey, onSuccessListener);
    }

    private void uploadFiletoServer(Uri _file, String messageKey) {

        // Create the file metadata
        metadata = new StorageMetadata.Builder()
                .setContentType(AppUtils.getMimeType(_file, this))
                .setCustomMetadata(DbKeys.Messages.messageId, messageKey)
                .setCustomMetadata(DbKeys.Messages.attachmentName, FileUtils.getName(this, _file))
                .build();

        OnProgressListener onProgressListener = new
                OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                };
        OnCompleteListener onCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        };


        storageHandler.uploadFile(ChatRoomActivity.this, mFirebaseStorage,
                _file, metadata, onProgressListener, onCompleteListener,
                onSuccessListener);
    }

    @SuppressWarnings("VisibleForTests")
    private OnSuccessListener onSuccessListener = new
            OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    String messageKey = taskSnapshot.getMetadata().getCustomMetadata(DbKeys.Messages.messageId);
                    String fileName = taskSnapshot.getMetadata().getCustomMetadata
                            (DbKeys.Messages.attachmentName);
                    Log.d(this.getClass().getName(), "onSuccess Uploaded " +
                            "downloadUrl" + downloadUrl.toString() +
                            "messageKey" + messageKey +
                            "fileName" + fileName);

                    Map<String, String> attachmentDetail = new HashMap<>();
                    attachmentDetail.put(DbKeys.Messages.attachmentName, fileName);
                    attachmentDetail.put(DbKeys.Messages.attachmentUrl, downloadUrl.toString());
                    databaseUtils.updateMessageAttachmentUrl(messageKey, attachmentDetail);

                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == SELECT_FILE_REQUEST_CODE) {
            showLog(data.getData().toString());

            File file = new File(data.getData().getPath());

            showLog("file. getMimeType  = " + AppUtils.getMimeType(data
                    .getData(), this));
            showLog("file. getImagePath  = " + FileUtils.getPath(this, data
                    .getData()));
            showLog("file. getImagePath  = " +
                    FileUtils.getName(this, data
                            .getData()));
            if (AppUtils.isImageUrl(FileUtils.getPath(this, data.getData()))) {

            }
            sendAttachmentToDb(data.getData());

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    AppUtils.openFileSelectPage(ChatRoomActivity.this, "Select file ",
                            SELECT_FILE_REQUEST_CODE);
                } else {

                    // permission denied,
                }
                return;
            }

        }
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

    private void showLog(String message) {
        Log.d("BJIT " + this.getClass().getName(), message);
    }

    @Override
    public void scrollTo(int position) {
        if (recyclerViewMessages != null) {
            recyclerViewMessages.scrollToPosition(position);
        }
    }
}