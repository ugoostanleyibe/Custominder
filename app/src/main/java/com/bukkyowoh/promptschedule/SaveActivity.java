package com.bukkyowoh.promptschedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class SaveActivity extends AppCompatActivity {
        protected FloatingActionButton playPauseButton;
        protected DatePickerDialog datePickerDialog;
        protected TimePickerDialog timePickerDialog;
        protected MediaPlayer mediaPlayer;
        protected PSRecording recording;
        protected Calendar currentTime;
        protected Calendar alarmTime;
        protected PSProvider provider;
        protected TextView dateField;
        protected TextView timeField;
        protected Button saveButton;
        protected boolean ignored;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState); setContentView(R.layout.save_activity);
                playPauseButton = (FloatingActionButton) findViewById(R.id.playPauseButton);
                saveButton = (Button) findViewById(R.id.saveButton); ignored = true;
                dateField = (TextView) findViewById(R.id.dateField);
                timeField = (TextView) findViewById(R.id.timeField);
                recording = PSRecorder.getLastRecording();
                currentTime = Calendar.getInstance();
                alarmTime = Calendar.getInstance();
                provider = new PSProvider(this);

                int year = currentTime.get(Calendar.YEAR), month = currentTime.get(Calendar.MONTH),
                        day = currentTime.get(Calendar.DAY_OF_MONTH),
                        hour = currentTime.get(Calendar.HOUR_OF_DAY),
                        minute = currentTime.get(Calendar.MINUTE);

                datePickerDialog = new DatePickerDialog(this, (view, alarmYear, alarmMonth, alarmDay) -> {
                        setShowingDate(alarmYear, alarmMonth+1, alarmDay);
                        alarmTime.set(Calendar.DAY_OF_MONTH, alarmDay);
                        alarmTime.set(Calendar.MONTH, alarmMonth);
                        alarmTime.set(Calendar.YEAR, alarmYear);
                }, year, month, day);

                timePickerDialog = new TimePickerDialog(this, (view, alarmHour, alarmMinute) -> {
                        alarmTime.set(Calendar.HOUR_OF_DAY, alarmHour);
                        alarmTime.set(Calendar.MINUTE, alarmMinute);
                        setShowingTime(alarmHour, alarmMinute);
                }, hour, minute+1, true);

                setShowingDate(year, month+1, day); setShowingTime(hour, minute+1);

                try {
                        mediaPlayer = new MediaPlayer(); alarmTime.set(Calendar.MINUTE, minute+1);
                        mediaPlayer.setOnCompletionListener(player -> enterPausedMode());
                        mediaPlayer.setDataSource(recording.getPath());
                        mediaPlayer.prepareAsync();
                } catch (Exception bug) {
                        Utils.log("BUG => " + bug.getMessage());
                }
        }

        @Override
        protected void onPause() {
                super.onPause(); if (mediaPlayer.isPlaying()) mediaPlayer.pause(); enterPausedMode();
        }

        @Override
        protected void onResumeFragments() {
                super.onResumeFragments(); Utils.schedule(() -> {
                        mediaPlayer.start(); enterPlayingMode();
                }, 500);
        }

        @Override
        protected void onDestroy() {
                super.onDestroy(); if (ignored) PSRecorder.ditchLastRecording();
                Utils.clearScheduledTasks();
        }

        public void togglePlayback(View view) {
                if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause(); enterPausedMode();
                } else {
                        mediaPlayer.start(); enterPlayingMode();
                }
        }

        public void saveReminder(View view) {
                if (alarmTime.after(Calendar.getInstance())) {
                        provider.save(new PSReminder(recording, alarmTime.getTimeInMillis(),
                                provider.nextId())); ignored = false; finish();
                } else Utils.alert("Invalid Time", this);
        }

        public void showDatePicker(View view) {
                datePickerDialog.show();
        }

        public void showTimePicker(View view) {
                timePickerDialog.show();
        }

        protected void enterPausedMode() {
                playPauseButton.setImageResource(R.drawable.play_icon);
        }

        protected void enterPlayingMode() {
                playPauseButton.setImageResource(R.drawable.pause_icon);
        }

        protected void setShowingDate(int year, int month, int day) {
                dateField.setText(String.format(Locale.getDefault(), "%04d/%02d/%02d", year, month, day));
        }

        protected void setShowingTime(int hour, int minute) {
                timeField.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }
}