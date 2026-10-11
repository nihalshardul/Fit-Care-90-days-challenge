package com.fitcare90.app;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public class ReminderScheduler {
    static final String CHANNEL = "fitcare90_reminders";
    static void createChannel(Context c) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "Fit & Care reminders", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("Water, posture and sleep reminders");
            NotificationManager nm = c.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }
    }
    static void scheduleDefaults(Context c) {
        schedule(c, "water", "10:30");
        schedule(c, "posture", "14:00");
        schedule(c, "sleep", "22:30");
    }
    static void schedule(Context c, String kind, String time) {
        String[] p = time.split(":");
        if (p.length != 2) return;
        int hour, minute;
        try { hour = Integer.parseInt(p[0]); minute = Integer.parseInt(p[1]); }
        catch (Exception e) { return; }
        int id = kind.equals("water") ? 1001 : kind.equals("posture") ? 1002 : 1003;
        Intent intent = new Intent(c, ReminderReceiver.class);
        intent.putExtra("kind", kind);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        intent.putExtra("id", id);
        PendingIntent pi = PendingIntent.getBroadcast(c, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, hour); next.set(Calendar.MINUTE, minute); next.set(Calendar.SECOND, 0); next.set(Calendar.MILLISECOND, 0);
        if (!next.after(Calendar.getInstance())) next.add(Calendar.DAY_OF_YEAR, 1);
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            if (Build.VERSION.SDK_INT >= 23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
            else am.set(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
        }
    }
    static void showNow(Context c, String title, String message) {
        createChannel(c);
        NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification.Builder b = Build.VERSION.SDK_INT >= 26 ?
            new android.app.Notification.Builder(c, CHANNEL) : new android.app.Notification.Builder(c);
        b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(message).setAutoCancel(true);
        nm.notify((int)(System.currentTimeMillis()%100000), b.build());
    }
}
