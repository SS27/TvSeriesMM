package com.spstanchev.tvseries.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Stefan on 2/25/2015.
 */
public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver alarmReceiver = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            alarmReceiver.setAlarm(context);
        }
    }
}
