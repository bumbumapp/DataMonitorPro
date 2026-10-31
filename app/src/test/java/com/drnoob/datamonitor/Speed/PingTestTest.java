package com.drnoob.datamonitor.Speed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.ServerSocket;

import org.junit.Test;

public class PingTestTest {

    @Test
    public void parsesReplyLines() {
        assertEquals(12.3, PingTest.parseReplyTime("64 bytes from 1.1.1.1: icmp_seq=1 ttl=57 time=12.3 ms"), 1e-9);
        assertEquals(9, PingTest.parseReplyTime("64 bytes from 8.8.8.8: icmp_seq=2 ttl=118 time=9 ms"), 1e-9);
        assertEquals(0.5, PingTest.parseReplyTime("64 bytes from 10.0.2.2: icmp_seq=1 ttl=255 time<0.5 ms"), 1e-9);
        // The old parser read the second-to-last word here and threw NumberFormatException.
        assertEquals(-1, PingTest.parseReplyTime("From 10.0.0.1 icmp_seq=1 Destination Host Unreachable"), 1e-9);
        assertEquals(-1, PingTest.parseReplyTime("PING 1.1.1.1 (1.1.1.1) 56(84) bytes of data."), 1e-9);
    }

    @Test
    public void parsesSummaryLines() {
        assertEquals(12.3, PingTest.parseSummaryAvg("rtt min/avg/max/mdev = 9.1/12.3/15.0/2.1 ms"), 1e-9);
        assertEquals(20.5, PingTest.parseSummaryAvg("round-trip min/avg/max = 10.0/20.5/31.0 ms"), 1e-9);
        assertEquals(-1, PingTest.parseSummaryAvg("3 packets transmitted, 0 received, 100% packet loss"), 1e-9);
        // Emulator ICMP: broken clock gives 0.000 averages and absurd reply times.
        assertEquals(-1, PingTest.parseSummaryAvg("rtt min/avg/max/mdev = 0.000/0.000/0.000/0.000 ms, pipe 4"), 1e-9);
        assertEquals(-1, PingTest.parseReplyTime("64 bytes from 8.8.8.8: icmp_seq=1 ttl=255 time=4644956265794705 ms"), 1e-9);
    }

    @Test
    public void unreachableHostStillFinishes() throws Exception {
        PingTest t = new PingTest("203.0.113.1:9", 1); // TEST-NET-3, never answers
        t.start();
        t.join(15000);
        assertTrue(t.isFinished());
    }

    @Test
    public void fallsBackToTcpConnectTime() throws Exception {
        try (ServerSocket server = new ServerSocket(0)) {
            Thread acceptor = new Thread(() -> {
                try { while (true) server.accept().close(); } catch (Exception ignored) { }
            });
            acceptor.setDaemon(true);
            acceptor.start();
            // "ping" with a host:port argument fails, so the TCP fallback must produce the value.
            PingTest t = new PingTest("127.0.0.1:" + server.getLocalPort(), 2);
            t.start();
            t.join(10000);
            assertTrue(t.isFinished());
            assertTrue(t.getAvgRtt() > 0);
        }
    }
}
