package com.bukkyowoh.promptschedule;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;

public class HomeActivity extends AppCompatActivity implements HoldingButtonLayoutListener {
        protected ViewPropertyAnimator slideToCancelLayoutAnimator;
        protected ViewPropertyAnimator timerBoardAnimator;
        protected HoldingButtonLayout holdingButtonLayout;
        protected View slideToCancelLayout;
        protected Runnable timerRunnable;
        protected TextView timerBoard;
        protected HomeActivity self;
        protected boolean allowed;
        protected long startTime;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState); setContentView(R.layout.home_activity); self = this;
                (holdingButtonLayout = (HoldingButtonLayout) findViewById(R.id.holdingButtonLayout))
                        .addListener(this); slideToCancelLayout = findViewById(R.id.slideToCancelLayout);
                timerBoard = (TextView) findViewById(R.id.timerBoard); Utils.log("App Created");
        }

        /*@Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.home_menu, menu);
                return true;
        }*/

        @Override
        public void onBeforeExpand() {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        cancelAllAnimations(); slideToCancelLayout.setTranslationX(0.0f);
                        slideToCancelLayout.setAlpha(0.0f); slideToCancelLayout.setVisibility(View.VISIBLE);
                        (slideToCancelLayoutAnimator = slideToCancelLayout.animate()
                                .alpha(1.0f).setDuration(200)).start();
                        timerBoard.setTranslationY(timerBoard.getHeight());
                        timerBoard.setAlpha(0.0f); timerBoard.setVisibility(View.VISIBLE);
                        (timerBoardAnimator = timerBoard.animate().translationY(0.0f)
                                .alpha(1.0f).setDuration(200)).start(); allowed = true;
                } else {
                        allowed = false; holdingButtonLayout.cancel(); ActivityCompat.requestPermissions(self,
                                new String[]{Manifest.permission.RECORD_AUDIO}, 10101);
                }
        }

        @Override
        public void onExpand() {
                if (allowed) {
                        startTime = System.currentTimeMillis(); runTimer();
                        PSRecorder.setCurrentFileName("PromptSchedule" + Utils
                                .timeStringFromFormat("yyyyMMddHHmmss", startTime));
                        PSRecorder.setCurrentPath(getFilesDir() + "/" +
                                PSRecorder.getCurrentFileName() + ".m4a");
                        PSRecorder.startRecording();
                }
        }

        @Override
        public void onBeforeCollapse() {
                cancelAllAnimations();
                slideToCancelLayoutAnimator = slideToCancelLayout.animate().alpha(0.0f).setDuration(200);
                slideToCancelLayoutAnimator.setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                                slideToCancelLayout.setVisibility(View.INVISIBLE);
                                slideToCancelLayoutAnimator.setListener(null);
                        }
                });
                slideToCancelLayoutAnimator.start();
                timerBoardAnimator = timerBoard.animate()
                        .translationY(timerBoard.getHeight()).alpha(0.0f).setDuration(200);
                timerBoardAnimator.setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                                timerBoard.setVisibility(View.INVISIBLE);
                                timerBoardAnimator.setListener(null);
                        }
                });
                timerBoardAnimator.start();
        }

        @Override
        public void onCollapse(boolean isCancel) {
                try {
                        stopTimer();
                        if (allowed && isCancel) {
                                PSRecorder.stopRecordingCancelled();
                                Utils.alert("Recording Cancelled", this);
                        } else if (allowed) {
                                PSRecorder.stopRecordingNormal();
                                startActivity(new Intent(self, SaveActivity.class));
                        }
                } catch (Exception bug) {
                        Utils.log("BUG => " + bug.getMessage());
                }
        }

        @Override
        public void onOffsetChanged(float offset, boolean isCancel) {
                slideToCancelLayout.setTranslationX(holdingButtonLayout.getWidth() * offset * -1);
                slideToCancelLayout.setAlpha(1.0f - 2.5f * offset);
        }

        public void goToPending(View view) {
                startActivity(new Intent(this, PendingActivity.class));
        }

        public void goToHistory(View view) {
                startActivity(new Intent(this, HistoryActivity.class));
        }

        public void goToSettings(MenuItem item) {
                startActivity(new Intent(this, SettingsActivity.class));
        }

        public void showAboutApp(MenuItem item) {
                new AlertDialog.Builder(this).setTitle("About").setMessage(getString(R.string.about_app))
                        .setPositiveButton("COOL", (dialog, which) -> dialog.dismiss()).show();
        }

        protected void cancelAllAnimations() {
                if (slideToCancelLayoutAnimator != null) slideToCancelLayoutAnimator.cancel();
                if (timerBoardAnimator != null) timerBoardAnimator.cancel();
        }

        protected void runTimer() {
                timerRunnable = () -> { timerBoard.setText(getElapsedTime()); runTimer(); };
                timerBoard.postDelayed(timerRunnable, 50);
        }

        protected void stopTimer() {
                if (timerRunnable != null) timerBoard.getHandler().removeCallbacks(timerRunnable);
        }

        protected String getElapsedTime() {
                return Utils.timeStringFromFormat("mm:ss:SS", System.currentTimeMillis() - startTime);
        }
}