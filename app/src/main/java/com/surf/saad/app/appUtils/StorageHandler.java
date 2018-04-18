package com.surf.saad.app.appUtils;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;

public class StorageHandler {

    UploadTask uploadTask;
    StorageReference fileReference;

    public void uploadFile(Activity context, FirebaseStorage firebaseStorage,
                           Uri uploadFilePath,
                           StorageMetadata metadata, OnProgressListener onProgressListener,
                           OnCompleteListener onCompleteListener,
                           OnSuccessListener onSuccessListener) {
        try {
            String fileName = FileUtils.getName(context, uploadFilePath);
            AppUtils
                    .getFileNameAndExtention
                            (uploadFilePath.getPath());

            fileReference = firebaseStorage.getReference().child
                    (ServerUtils.getServerFilePathToUpload(fileName));
            FileInputStream inputStream = (FileInputStream) context.getContentResolver().openInputStream
                    (uploadFilePath);
            uploadTask = fileReference.putStream(inputStream,
                    metadata);


            uploadTask.addOnProgressListener(context, onProgressListener)
                    .addOnCompleteListener(context, onCompleteListener)
                    .addOnSuccessListener(onSuccessListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean pauseUpload() {
        if (uploadTask == null)
            return false;

        if (uploadTask.isInProgress()) {
            uploadTask.pause();
            return true;
        }
        return false;
    }

    public boolean resumeUpload() {
        if (uploadTask == null)
            return false;

        if (uploadTask.isPaused()) {
            uploadTask.resume();
            Log.d(this.getClass().getName(), "Uploading resume");
            return true;

        }
        return false;

    }
}
