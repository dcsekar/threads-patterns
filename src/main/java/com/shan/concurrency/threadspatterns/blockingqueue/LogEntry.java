package com.shan.concurrency.threadspatterns.blockingqueue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LogEntry represents a log message to be written to file.
 */
@Data
@AllArgsConstructor
public class LogEntry {
    private String level;
    private String message;
    private String source;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s", timestamp, level, source, message);
    }

    public static LogEntry poison() {
        return new LogEntry("POISON", "POISON_PILL", "SYSTEM", LocalDateTime.now());
    }

    public boolean isPoison() {
        return "POISON".equals(level);
    }
}
