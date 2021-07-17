package com.example.quiz.notification;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseCloudMessaging extends FirebaseMessagingService{
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(Constants.TAG, "New token: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Toast.makeText(getApplicationContext(), "New Message", Toast.LENGTH_SHORT).show();
        Log.d(Constants.TAG, remoteMessage.getNotification().getBody());
    }
}
