package com.bukkyowoh.promptschedule;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

public class PSAlarmActivity extends AppCompatActivity {
        protected MediaPlayer mediaPlayer;
        protected PSReminder reminder;
        protected PSProvider provider;
        protected Vibrator vibrator;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState); getWindow()
                        .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                setContentView(R.layout.alarm_activity);
                provider = new PSProvider(this);

                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                reminder = provider.get(getIntent().getLongExtra("ID", 0));
                Utils.schedule(this::startAlarm, 2000);
        }

        @Override
        protected void onDestroy() {
                super.onDestroy(); stopAlarm(); Utils.clearScheduledTasks();
        }

        @SuppressWarnings("deprecation")
        protected void startAlarm() {
                try {
                        mediaPlayer = new MediaPlayer(); vibrator.vibrate(new long[]{0, 500, 500}, 0);
                        mediaPlayer.setOnCompletionListener(player -> scheduleLoop());
                        mediaPlayer.setDataSource(reminder.getRecording().getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                } catch (Exception bug) {
                        Utils.log("BUG => " + bug.getMessage());
                }
        }

        protected void stopAlarm() {
                mediaPlayer.stop(); vibrator.cancel();
        }

        protected void scheduleLoop() {
                Utils.schedule(mediaPlayer::start, 2000);
        }

        public void dismissAlarm(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAndRemoveTask();
                else finish();
        }
}