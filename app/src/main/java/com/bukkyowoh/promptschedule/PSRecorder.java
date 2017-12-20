package com.bukkyowoh.promptschedule;

import android.media.MediaRecorder;

import java.io.File;

class PSRecorder {
        private static PSRecording mLastRecording;
        private static MediaRecorder mRecorder;
        private static String mCurrentFileName;
        private static String mCurrentPath;

        static void startRecording() {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile(mCurrentPath);

                try {
                        mRecorder.prepare(); mRecorder.start();
                } catch (Exception bug) {
                        bug.getMessage();
                }
        }

        static void stopRecordingNormal() {
                mRecorder.stop(); mRecorder.release(); mRecorder = null;
                mLastRecording = new PSRecording(mCurrentFileName, mCurrentPath);
        }

        static void stopRecordingCancelled() {
                mRecorder.stop(); mRecorder.release(); mRecorder = null;
                ditchLastRecording();
        }

        static boolean ditchLastRecording() {
                return (new File(mCurrentPath).delete());
        }

        static String getCurrentFileName() {
                return mCurrentFileName;
        }

        static void setCurrentFileName(String currentFileName) {
                mCurrentFileName = currentFileName;
        }

        static void setCurrentPath(String currentPath) {
                mCurrentPath = currentPath;
        }

        static PSRecording getLastRecording() {
                return mLastRecording;
        }
}