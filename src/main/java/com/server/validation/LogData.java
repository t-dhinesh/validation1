package com.server.validation;

public class LogData {
    private String timestamp;
    private String event;
    private String sequence;
    private String sequenceSession;

    public LogData(String timestamp, String event, String sequence, String sequenceSession) {
        this.timestamp = timestamp;
        this.event = event;
        this.sequence = sequence;
        this.sequenceSession = sequenceSession;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getEvent() {
        return event;
    }

    public String getSequence() {
        return sequence;
    }

    public String getSequenceSession() {
        return sequenceSession;
    }
}
