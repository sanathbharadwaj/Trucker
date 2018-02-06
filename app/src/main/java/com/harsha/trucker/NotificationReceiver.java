package com.harsha.trucker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Admin on 12/7/2017.
 */

public class NotificationReceiver extends ParsePushBroadcastReceiver {

    private static int notificationId = 0;

    public enum Status{
        ASSIGNED,  ARRIVED, STARTED, FINISHED, CANCELED
    }

    Context context;

    @Override
    protected void onPushReceive(Context mContext, Intent intent) {
        //enter your custom here generateNotification();
        //super.onPushReceive(mContext, intent);
        context = mContext;
        Log.i("Push", "Message Received");
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            String extra = json.getString("title");
            int s = json.getInt("alert");
            Status status = Status.values()[s];
            switch (status) {
                case ASSIGNED: mContext.sendBroadcast(new Intent("ASSIGNED"));
                    notifyUser("Pickup accepted", "Pickup Accepted by " + extra);
                    break;
                case ARRIVED: notifyUser("Driver arrived", "Driver arrived at your location");
                    mContext.sendBroadcast(new Intent("ARRIVED"));
                    break;
                case STARTED: notifyUser("Ride Started", "Riding with " + extra);
                    mContext.sendBroadcast(new Intent("STARTED"));
                    break;
                case FINISHED: notifyUser("Ride Ended", "Total fare " + extra);
                    mContext.sendBroadcast(new Intent("FINISHED"));
                    break;
                case CANCELED: notifyUser("Ride Cancelled", "Ride has been cancelled");
                    mContext.sendBroadcast(new Intent("CANCELLED"));
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void notifyUser(String title, String alert)
    {
        Notification notification;

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent resultIntent = new Intent(context, TrackTripActivity.class);
        resultIntent.putExtra("id", notificationId);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT < 16) {
            notification = new Notification.Builder(context)
                    .setSound(uri)
                    .setContentTitle(title)
                    .setContentText(alert)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher).getNotification();
        }
        else
        {
            notification = new Notification.Builder(context)
                    .setSound(uri)
                    .setContentTitle(title)
                    .setContentText(alert)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        }
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(notificationId,notification);
        notificationId++;
    }
}
