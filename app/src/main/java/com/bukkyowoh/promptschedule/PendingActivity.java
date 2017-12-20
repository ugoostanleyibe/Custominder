package com.bukkyowoh.promptschedule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PendingActivity extends AppCompatActivity {
        protected ListView pendingListView;
        protected PSProvider provider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState); setContentView(R.layout.pending_activity);
                pendingListView = (ListView) findViewById(R.id.pendingListView);
                pendingListView.setEmptyView(findViewById(R.id.noPending));
                provider = new PSProvider(this);
        }

        @Override
        protected void onResumeFragments() {
                super.onResumeFragments(); ArrayList<HashMap<String, String>> items = new ArrayList<>();
                for (PSReminder reminder : provider.get("pending")) {
                        String dateStr = DateFormat.getDateInstance().format(new Date(reminder.getTime())),
                                timeStr = DateFormat.getTimeInstance(DateFormat.SHORT)
                                        .format(new Date(reminder.getTime())), details = String
                                .format(Locale.getDefault(), "Scheduled For %s @ %s",
                                        dateStr, timeStr), prefix = "Prompt Schedule #";

                        items.add(Utils.mapFromStrings("name", prefix + (reminder.getId()+1), "details", details));
                }

                pendingListView.setAdapter(new SimpleAdapter(this, items, R.layout.list_view,
                        new String[]{"name", "details"}, new int[]{R.id.name, R.id.details}));
        }
}