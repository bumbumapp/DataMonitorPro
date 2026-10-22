package com.drnoob.datamonitor.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

/** Regression: AppDataMonitor crashed with NPE on SCREEN_ON when no app limit was ever saved. */
public class AppDataUsageMonitorTest {
    @Test
    public void nullOrEmptyListMeansNothingToMonitor() {
        assertFalse(AppDataUsageMonitor.hasMonitoredApps(null));
        assertFalse(AppDataUsageMonitor.hasMonitoredApps(Collections.emptyList()));
        assertTrue(AppDataUsageMonitor.hasMonitoredApps(Collections.singletonList("x")));
    }
}
