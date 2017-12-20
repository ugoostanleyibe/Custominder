package com.bukkyowoh.promptschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PSAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
                context.startActivity(new Intent(context, PSAlarmActivity.class).putExtra("ID",
                        intent.getLongExtra("ID", 0)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        }
}