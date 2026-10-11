package com.fitcare90.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReminderScheduler.createChannel(this);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AndroidBridge(this), "AndroidFitCare");
        webView.loadUrl("file:///android_asset/www/index.html");
        ReminderScheduler.scheduleDefaults(this);
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 4001);
        }
    }
    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
    public static class AndroidBridge {
        private final Activity activity;
        AndroidBridge(Activity a) { activity = a; }
        @android.webkit.JavascriptInterface
        public void saveReminders(String water, String posture, String sleep) {
            activity.runOnUiThread(() -> {
                ReminderScheduler.schedule(activity, "water", safeTime(water, "10:30"));
                ReminderScheduler.schedule(activity, "posture", safeTime(posture, "14:00"));
                ReminderScheduler.schedule(activity, "sleep", safeTime(sleep, "22:30"));
                Toast.makeText(activity, "Reminder times saved", Toast.LENGTH_SHORT).show();
            });
        }
        @android.webkit.JavascriptInterface
        public void testNotification() {
            activity.runOnUiThread(() -> ReminderScheduler.showNow(activity, "Test reminder", "Fit & Care notifications are working."));
        }
        private String safeTime(String t, String fallback) {
            return t != null && t.matches("^([01]\\d|2[0-3]):[0-5]\\d$") ? t : fallback;
        }
    }
}
