package com.bukkyowoh.promptschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class BootCompletedReceiver extends BroadcastReceiver {
        protected List<PSReminder> reminders;
        protected PSProvider provider;

        @Override
        public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                        provider = new PSProvider(context); reminders = provider.get("pending");
                        for (PSReminder reminder : reminders) provider.schedule(reminder);
                }
        }
}