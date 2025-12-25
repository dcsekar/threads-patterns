package com.shan.concurrency.threadspatterns.blockingqueue;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

/**
 * LogProducer generates log entries and puts them in the queue.
 * Represents multiple application components producing logs.
 */
@Slf4j
public class LogProducer implements Runnable {

    private final String producerName;
    private final BlockingQueue<LogEntry> queue;
    private final int numberOfLogs;

    public LogProducer(String producerName, BlockingQueue<LogEntry> queue, int numberOfLogs) {
        this.producerName = producerName;
        this.queue = queue;
        this.numberOfLogs = numberOfLogs;
    }

    @Override
    public void run() {
        try {
            log.info("[{}] Producer '{}' started (Queue capacity: {}, Current size: {})",
                    Thread.currentThread().getName(), producerName,
                    queue.remainingCapacity() + queue.size(), queue.size());

            for (int i = 1; i <= numberOfLogs; i++) {
                LogEntry entry = createLogEntry(i);

                log.info("[{}] Producer '{}' putting log {}/{} into queue (Queue size: {})",
                        Thread.currentThread().getName(), producerName, i, numberOfLogs, queue.size());

                // put() blocks if queue is full
                queue.put(entry);

                log.info("[{}] Producer '{}' successfully put log {}/{} (Queue size: {})",
                        Thread.currentThread().getName(), producerName, i, numberOfLogs, queue.size());

                Thread.sleep(100 + (int)(Math.random() * 200)); // Simulate variable production rate
            }

            log.info("[{}] Producer '{}' finished producing {} logs",
                    Thread.currentThread().getName(), producerName, numberOfLogs);

        } catch (InterruptedException e) {
            log.error("[{}] Producer '{}' was interrupted",
                    Thread.currentThread().getName(), producerName);
            Thread.currentThread().interrupt();
        }
    }

    private LogEntry createLogEntry(int sequence) {
        String[] levels = {"INFO", "WARN", "ERROR", "DEBUG"};
        String level = levels[sequence % levels.length];
        String message = String.format("Log message %d from %s", sequence, producerName);

        return new LogEntry(level, message, producerName, LocalDateTime.now());
    }
}
