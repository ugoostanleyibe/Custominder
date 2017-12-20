package com.bukkyowoh.promptschedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

class PSProvider {
        private SharedPreferences mPreferences;
        private AlarmManager mAlarmManager;
        private Context mContext;

        PSProvider(Context context) {
                mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                mContext = context;
        }

        List<PSReminder> get(String category) {
                List<PSReminder> all = new ArrayList<>(), past = new ArrayList<>(), pending = new ArrayList<>();
                TreeSet<String> keys = new TreeSet<>(mPreferences.getAll().keySet());

                for (String key : keys) {
                        String[] parts = mPreferences.getString(key, "?").split(" && ");
                        PSReminder reminder = new PSReminder(new PSRecording(key, parts[0]),
                                Long.parseLong(parts[1]), Long.parseLong(parts[2]));

                        if (reminder.getTime() > System.currentTimeMillis()) pending.add(reminder);
                        if (reminder.getTime() < System.currentTimeMillis()) past.add(reminder);
                        all.add(reminder);
                }

                Collections.reverse(pending); Collections.reverse(past); Collections.reverse(all);
                if (category.equals("pending")) return pending;
                if (category.equals("past")) return past;
                return all;
        }

        PSReminder get(long id) {
                return get("?").stream().filter(obj -> obj.getId() == id).findFirst().orElse(null);
        }

        long nextId() {
                return get("?").size();
        }

        /*void clear() {
                mPreferences.edit().clear().apply();
        }*/

        void save(PSReminder reminder) {
                mPreferences.edit().putString(reminder.getKey(), String.format(Locale.getDefault(),
                        "%s && %d && %d", reminder.getRecording().getPath(), reminder.getTime(),
                        reminder.getId())).apply(); schedule(reminder);
        }

        /*void delete(PSReminder reminder) {
                if ((new File(reminder.getRecording().getPath()).delete()))
                        mPreferences.edit().remove(reminder.getKey()).apply();
        }*/

        void schedule(PSReminder reminder) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTime(),
                        PendingIntent.getBroadcast(mContext, (int) reminder.getId(), new Intent(mContext,
                                        PSAlarmReceiver.class).putExtra("ID", reminder.getId()),
                                PendingIntent.FLAG_UPDATE_CURRENT));
        }

        /*void unshedule(PSReminder reminder) {
                mAlarmManager.cancel(PendingIntent.getBroadcast(mContext,
                        (int) reminder.getId(), new Intent(mContext, PSAlarmReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        }*/
}