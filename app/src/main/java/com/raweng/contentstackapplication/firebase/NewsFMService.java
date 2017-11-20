package com.raweng.contentstackapplication.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.builtio.contentstack.Contentstack;
import com.builtio.contentstack.Entry;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.raweng.contentstackapplication.NewsDetailActivity;
import com.raweng.contentstackapplication.R;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by shailesh on 13/11/17.
 */

public class NewsFMService extends FirebaseMessagingService {

    private String TAG = NewsFMService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        onMessageReceivedSendToDetailedActivity(remoteMessage);
    }

    public void onMessageReceivedSendToDetailedActivity(RemoteMessage remoteMessage){
        String title=remoteMessage.getNotification().getTitle();
        String message=remoteMessage.getNotification().getBody();

        Intent intent=new Intent(getApplicationContext(), NewsDetailActivity.class);
        if (remoteMessage.getData().size() > 0) {
            String getUid = remoteMessage.getData().get("uid");
            if (!getUid.equalsIgnoreCase("")){
                intent.putExtra("uid", getUid);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setSmallIcon(R.drawable.ic_news);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }


}
