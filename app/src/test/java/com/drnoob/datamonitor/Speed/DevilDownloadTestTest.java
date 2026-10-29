package com.drnoob.datamonitor.Speed;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DevilDownloadTestTest {
    @Test
    public void subMegabitRateIsNotTruncatedToZero() {
        // 100 KB in one second = 0.8 Mbps (old integer maths gave 0).
        assertEquals(0.8, DevilDownloadTest.rateMbps(100_000, 1.0), 1e-9);
    }

    @Test
    public void largeDownloadsDoNotOverflow() {
        // 400 MB in 10 s = 320 Mbps (int bytes*8 would overflow).
        assertEquals(320.0, DevilDownloadTest.rateMbps(400_000_000L, 10.0), 1e-9);
    }
}
