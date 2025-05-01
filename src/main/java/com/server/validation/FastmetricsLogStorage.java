package com.server.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FastmetricsLogStorage {
    private static final List<String> logs = new ArrayList<>();

    public static void addLog(String log) {
        logs.add(log);
    }

    public static List<String> getLogs() {
        return new ArrayList<>(logs); // Avoid modifying original
    }

    public static void clearLogs() {
        logs.clear();
    }
}
