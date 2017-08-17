package com.example.wgu_companion.wgucompanion;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String messageText = intent.getStringExtra("message");
        String titleText = intent.getStringExtra("title");
        String tickerText = intent.getStringExtra("ticker");

        createNotification(context, titleText, messageText, tickerText);
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_terms_logo)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        mBuilder.setContentIntent(pIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
