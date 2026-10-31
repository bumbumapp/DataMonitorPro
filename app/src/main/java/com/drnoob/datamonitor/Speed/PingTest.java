package com.drnoob.datamonitor.Speed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author erdigurbuz
 */
public class PingTest extends Thread {

    private static final Pattern TIME = Pattern.compile("time[=<]\\s*([0-9]+(?:\\.[0-9]+)?)");
    // "rtt min/avg/max/mdev = 9.1/12.3/15.0/2.1 ms" (Linux) or "round-trip min/avg/max = ..." (toybox)
    private static final Pattern SUMMARY = Pattern.compile("=\\s*[0-9.]+/([0-9.]+)/");
    private static final int TCP_TIMEOUT_MS = 2000;

    HashMap<String, Object> result = new HashMap<>();
    String server = "";
    int count;
    volatile double instantRtt = 0;
    volatile double avgRtt = 0.0;
    volatile boolean finished = false;
    boolean started = false;

    public PingTest(String serverIpAddress, int pingTryCount) {
        this.server = serverIpAddress;
        this.count = pingTryCount;
    }

    public double getAvgRtt() {
        return avgRtt;
    }

    public double getInstantRtt() {
        return instantRtt;
    }

    public boolean isFinished() {
        return finished;
    }

    /** RTT in ms from one ping reply line, or -1 when the line is not a reply. */
    static double parseReplyTime(String line) {
        if (line == null || !line.contains("icmp_seq")) return -1;
        Matcher m = TIME.matcher(line);
        return m.find() ? plausible(Double.parseDouble(m.group(1))) : -1;
    }

    /** Some ping builds (e.g. emulator ICMP) report 0 or clock-garbage times; treat those as no reply. */
    private static double plausible(double ms) {
        return ms > 0 && ms < 60000 ? ms : -1;
    }

    /** Average RTT in ms from the ping summary line, or -1 when the line is not the summary. */
    static double parseSummaryAvg(String line) {
        if (line == null || !(line.startsWith("rtt ") || line.startsWith("round-trip"))) return -1;
        Matcher m = SUMMARY.matcher(line);
        return m.find() ? plausible(Double.parseDouble(m.group(1))) : -1;
    }

    @Override
    public void run() {
        try {
            icmpPing();
            // ICMP is blocked on many networks/emulators: fall back to TCP connect time, which
            // is what users perceive as latency anyway. Without this the result was always 0 ms.
            if (avgRtt <= 0) tcpPing();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Always finish: the speed test loop waits for this before starting the download.
            finished = true;
        }
    }

    private void icmpPing() {
        Process pr = null;
        try {
            ProcessBuilder ps = new ProcessBuilder("ping", "-c", String.valueOf(count), "-W", "2", this.server);
            ps.redirectErrorStream(true);
            pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            double sum = 0;
            int replies = 0;
            while ((line = in.readLine()) != null) {
                double rtt = parseReplyTime(line);
                if (rtt >= 0) {
                    instantRtt = rtt;
                    sum += rtt;
                    replies++;
                }
                double avg = parseSummaryAvg(line);
                if (avg > 0) {
                    avgRtt = avg;
                    break;
                }
            }
            if (avgRtt <= 0 && replies > 0) avgRtt = sum / replies;
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pr != null) pr.destroy();
        }
    }

    private void tcpPing() {
        String host = server;
        int port = 8080;
        int colon = host.lastIndexOf(':');
        if (colon > 0 && host.indexOf(':') == colon) {
            try {
                port = Integer.parseInt(host.substring(colon + 1));
                host = host.substring(0, colon);
            } catch (NumberFormatException ignored) {
            }
        }
        InetAddress address;
        try {
            // Resolve once so DNS time is not counted as latency.
            address = InetAddress.getByName(host);
        } catch (Exception e) {
            return;
        }
        double sum = 0;
        int ok = 0;
        for (int i = 0; i < Math.max(1, count); i++) {
            long t0 = System.nanoTime();
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(address, port), TCP_TIMEOUT_MS);
                double ms = (System.nanoTime() - t0) / 1e6;
                instantRtt = ms;
                sum += ms;
                ok++;
            } catch (Exception e) {
                if (port != 80) port = 80; // speedtest servers also answer on 80
            }
        }
        if (ok > 0) avgRtt = sum / ok;
    }

}
