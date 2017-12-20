package com.bukkyowoh.promptschedule;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

class Utils {
        private static final String LOG_TAG = "PromptScheduleLogTag";
        private static final Handler HANDLER = new Handler();

        static void alert(String msg, Context context) {
                View alertView = View.inflate(context, R.layout.alert_view, null);
                ((TextView) alertView.findViewById(R.id.alertBoard)).setText(msg);
                AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(false)
                        .setView(alertView).create(); dialog.show(); schedule(dialog::dismiss, 2000);
        }

        static void schedule(Runnable task, long delay) {
                HANDLER.postDelayed(task, delay);
        }

        static void clearScheduledTasks() {
                HANDLER.removeCallbacksAndMessages(null);
        }

        static String timeStringFromFormat(String pattern, long stamp) {
                return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(stamp));
        }

        static HashMap<String, String> mapFromStrings(String... strings) {
                HashMap<String, String> map = new HashMap<>(); String key = null; int step = 1;

                for (String string: strings) {
                        switch (step % 2) {
                                case 1: key = string; step++; continue;
                                case 0: map.put(key, string); step++; break;
                        }
                }

                return map;
        }

        static void log(String msg) {
                Log.i(LOG_TAG, msg);
        }
}