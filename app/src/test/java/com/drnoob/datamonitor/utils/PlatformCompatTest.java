package com.drnoob.datamonitor.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlatformCompatTest {
    @Test
    public void exactAlarmAlwaysAllowedBeforeAndroid12() {
        assertTrue(PlatformCompat.canUseExactAlarm(30, false));
    }

    @Test
    public void exactAlarmNeedsUserPermissionOnAndroid12Plus() {
        // Previously setExact() was called unconditionally -> SecurityException crash on Android 14+.
        assertFalse(PlatformCompat.canUseExactAlarm(34, false));
        assertTrue(PlatformCompat.canUseExactAlarm(34, true));
    }
}
