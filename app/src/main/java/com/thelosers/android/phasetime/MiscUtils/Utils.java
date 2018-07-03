package com.thelosers.android.phasetime.MiscUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import com.thelosers.android.phasetime.HomeActivity;
import com.thelosers.android.phasetime.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

  public static Date addDays(Date date, int days) {

    GregorianCalendar cal = new GregorianCalendar();

    cal.setTime(date);
    cal.add(Calendar.DATE, days);

    return cal.getTime();
  }


  public static Date subtractDays(Date date, int days) {

    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.add(Calendar.DATE, -days);

    return cal.getTime();
  }


  public static NotificationManager mManager;


  @SuppressWarnings("static-access")
  public static void generateNotification(Context context) {

    Intent resultIntent = new Intent(context, HomeActivity.class);
    TaskStackBuilder TSB = TaskStackBuilder.create(context);
    TSB.addParentStack(HomeActivity.class);
    // Adds the Intent that starts the Activity to the top of the stack
    TSB.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent =
        TSB.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

    Notification notification = new NotificationCompat.Builder(context)
        .setContentTitle("PhaseTime")
        .setTicker("Golden Hour")
        .setContentText("Golden Hour Starts after 1 hour")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true)

        .setContentIntent(resultPendingIntent)
        .setOngoing(true)
        .build();

    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(11221, notification);


  }
}

