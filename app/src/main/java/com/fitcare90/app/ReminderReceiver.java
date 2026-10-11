package com.fitcare90.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String kind = intent.getStringExtra("kind");
        int hour = intent.getIntExtra("hour", 10), minute = intent.getIntExtra("minute", 30), id = intent.getIntExtra("id", 1001);
        String title = "Fit & Care 90";
        String message;
        if ("posture".equals(kind)) message = "Posture break: stand up, walk for 2–3 minutes and relax your shoulders.";
        else if ("sleep".equals(kind)) message = "Sleep wind-down: prepare for your next 7–9 hours of rest where possible.";
        else message = "Water check: drink water if you need it and update your daily tracker.";
        ReminderScheduler.showNow(context, title, message);
        ReminderScheduler.schedule(context, kind == null ? "water" : kind, String.format(java.util.Locale.US, "%02d:%02d", hour, minute));
    }
}
