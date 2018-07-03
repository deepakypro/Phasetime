package com.thelosers.android.phasetime.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.thelosers.android.phasetime.MiscUtils.Utils;

public class NotificationBrodcastReceiver extends BroadcastReceiver

{

  @Override
  public void onReceive(Context context, Intent intent) {

    Log.i("App", "called receiver method");
    try {
      Utils.generateNotification(context);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
