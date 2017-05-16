package com.example.muhammadfarhanbashir.gharkhana.helpers;

import android.content.Intent;
import android.util.Log;

import com.example.muhammadfarhanbashir.gharkhana.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;

/**
 * Created by muhammadfarhanbashir on 24/04/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Data Payload: " + remoteMessage.getNotification().getTitle());
        if (!remoteMessage.getNotification().getTitle().equals("")) {
            //Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                //JSONObject json = new JSONObject(remoteMessage.getNotification().toString());
                RemoteMessage.Notification notification = remoteMessage.getNotification();

                sendPushNotification(notification);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(RemoteMessage.Notification notification) {
        //optionally we can display the json into log
        //Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            //JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = notification.getTitle();
            String message = notification.getBody();
            String imageUrl = notification.getIcon();

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            //if there is no image
            if(imageUrl == null){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }else{
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }
    }
}
