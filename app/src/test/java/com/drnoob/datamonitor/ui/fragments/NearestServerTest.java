package com.drnoob.datamonitor.ui.fragments;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class NearestServerTest {

    private static List<String> server(String lat, String lon) {
        return Arrays.asList(lat, lon, "", "City", "", "host");
    }

    @Test
    public void picksTheClosestServer() {
        HashMap<Integer, List<String>> servers = new HashMap<>();
        servers.put(0, server("51.5", "-0.1"));   // London
        servers.put(1, server("40.4", "49.9"));   // Baku
        servers.put(2, server("41.0", "29.0"));   // Istanbul
        assertEquals(1, NetworkDiagnosticsFragment.nearestServer(servers, 40.38, 49.85));
        assertEquals(2, NetworkDiagnosticsFragment.nearestServer(servers, 41.1, 28.9));
    }

    // Launch crash: the list was read before it was downloaded.
    @Test
    public void emptyOrBrokenListGivesNoServer() {
        assertEquals(-1, NetworkDiagnosticsFragment.nearestServer(new HashMap<>(), 0, 0));
        HashMap<Integer, List<String>> broken = new HashMap<>();
        broken.put(3, Arrays.asList("x", "y"));
        broken.put(4, null);
        assertEquals(-1, NetworkDiagnosticsFragment.nearestServer(broken, 0, 0));
    }
}
