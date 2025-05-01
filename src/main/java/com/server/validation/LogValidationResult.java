package com.server.validation;

public class LogValidationResult {
    private String logLine;
    private boolean exists;

    public LogValidationResult(String logLine, boolean exists) {
        this.logLine = logLine;
        this.exists = exists;
    }

    public String getLogLine() {
        return logLine;
    }

    public boolean isExists() {
        return exists;
    }
}
