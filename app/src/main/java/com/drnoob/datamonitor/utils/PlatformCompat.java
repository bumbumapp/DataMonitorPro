package com.drnoob.datamonitor.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;

/** Helpers for Android 12-16 platform restrictions (exact alarms, receivers, services). */
public final class PlatformCompat {
    private static final String TAG = "PlatformCompat";

    private PlatformCompat() {
    }

    /** True if an exact alarm may be scheduled (Android 12+ requires the user to allow it). */
    public static boolean canUseExactAlarm(int sdkInt, boolean canScheduleExactAlarms) {
        return sdkInt < Build.VERSION_CODES.S || canScheduleExactAlarms;
    }

    /** setExact without SecurityException: falls back to an inexact alarm when exact alarms are not allowed. */
    public static void setExactSafe(AlarmManager manager, int type, long triggerAtMillis, PendingIntent operation) {
        if (manager == null || operation == null) {
            return;
        }
        boolean exactAllowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms();
        try {
            if (canUseExactAlarm(Build.VERSION.SDK_INT, exactAllowed)) {
                manager.setExact(type, triggerAtMillis, operation);
            } else {
                manager.setAndAllowWhileIdle(type, triggerAtMillis, operation);
            }
        } catch (SecurityException e) {
            Log.w(TAG, "Exact alarm not permitted, using inexact alarm", e);
            manager.set(type, triggerAtMillis, operation);
        }
    }

    /** registerReceiver with an explicit export flag (required on Android 14+). */
    public static void registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter) {
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    /** startService that does not crash when the app is in the background (Android 8+). */
    public static void startServiceSafe(Context context, Intent intent) {
        try {
            context.startService(intent);
        } catch (IllegalStateException | SecurityException e) {
            Log.w(TAG, "Could not start service " + intent.getComponent(), e);
        }
    }

    /** Starts a foreground service; swallows background-start restrictions (Android 12+). */
    public static void startForegroundServiceSafe(Context context, Intent intent) {
        try {
            ContextCompat.startForegroundService(context, intent);
        } catch (IllegalStateException | SecurityException e) {
            // ForegroundServiceStartNotAllowedException extends IllegalStateException.
            Log.w(TAG, "Could not start foreground service " + intent.getComponent(), e);
        }
    }

    /** startForeground with the declared specialUse type on Android 14+. Returns false if not allowed. */
    public static boolean startForeground(Service service, int id, Notification notification) {
        try {
            int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                    ? ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE : 0;
            ServiceCompat.startForeground(service, id, notification, type);
            return true;
        } catch (IllegalStateException | SecurityException e) {
            Log.w(TAG, "startForeground not allowed", e);
            return false;
        }
    }
}
