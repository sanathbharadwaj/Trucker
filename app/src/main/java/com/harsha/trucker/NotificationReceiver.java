package com.harsha.trucker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Admin on 12/7/2017.
 */

public class NotificationReceiver extends ParsePushBroadcastReceiver {

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
            int title = json.getInt("title");
            int s = json.getInt("alert");
            Status status = Status.values()[s];
            switch (status) {
                case ASSIGNED: mContext.sendBroadcast(new Intent("ASSIGNED"));
                    notifyUser("Pickup accepted", "Pickup Accepted by Sanath");
                    break;
                case ARRIVED: notifyUser("Driver arrived", "Driver arrived at your location");
                    mContext.sendBroadcast(new Intent("ARRIVED"));
                    break;
                case STARTED: notifyUser("Ride Started", "Riding with Sanath");
                    mContext.sendBroadcast(new Intent("STARTED"));
                    break;
                case FINISHED: notifyUser("Ride Ended", "Total fare Rs. 456");
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
        Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(alert)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }
}
