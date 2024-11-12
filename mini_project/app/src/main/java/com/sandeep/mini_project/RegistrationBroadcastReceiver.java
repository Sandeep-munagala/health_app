package com.sandeep.mini_project;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class RegistrationBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "registration_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Ensure this icon exists in drawable
                .setContentTitle("Registration Successful")
                .setContentText("You have successfully registered with the health app.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
