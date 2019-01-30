package com.bukkyowoh.promptschedule;

class PSReminder {
        private PSRecording mRecording;
        private String mKey;
        private long mTime;
        private long mId;

        PSReminder(PSRecording recording, long time, long id) {
                mKey = recording.getName();
                mRecording = recording;
                mTime = time;
                mId = id;
        }

        PSRecording getRecording() {
                return mRecording;
        }

        String getKey() {
                return mKey;
        }

        long getTime() {
                return mTime;
        }

        long getId() {
                return mId;
        }
}