package com.bukkyowoh.promptschedule;

class PSRecording {
        private String mName;
        private String mPath;

        PSRecording(String name, String path) {
                mName = name; mPath = path;
        }

        String getName() {
                return mName;
        }

        String getPath() {
                return mPath;
        }
}