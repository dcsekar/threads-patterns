package com.shan.concurrency.threadspatterns.blockingqueue;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * LogConsumer takes log entries from the queue and writes them.
 * Represents a file writer or log aggregator.
 */
@Slf4j
public class LogConsumer implements Runnable {

    private final String consumerName;
    private final BlockingQueue<LogEntry> queue;
    private final List<LogEntry> writtenLogs = new ArrayList<>();

    public LogConsumer(String consumerName, BlockingQueue<LogEntry> queue) {
        this.consumerName = consumerName;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            log.info("[{}] Consumer '{}' started",
                    Thread.currentThread().getName(), consumerName);

            while (true) {
                log.info("[{}] Consumer '{}' waiting for log entry (Queue size: {})",
                        Thread.currentThread().getName(), consumerName, queue.size());

                // take() blocks if queue is empty
                LogEntry entry = queue.take();

                // Check for poison pill (shutdown signal)
                if (entry.isPoison()) {
                    log.info("[{}] Consumer '{}' received poison pill. Shutting down.",
                            Thread.currentThread().getName(), consumerName);
                    break;
                }

                log.info("[{}] Consumer '{}' took log entry: {} (Queue size: {})",
                        Thread.currentThread().getName(), consumerName, entry, queue.size());

                // Simulate writing to file
                writeToFile(entry);
            }

            log.info("[{}] Consumer '{}' finished. Total logs written: {}",
                    Thread.currentThread().getName(), consumerName, writtenLogs.size());

        } catch (InterruptedException e) {
            log.error("[{}] Consumer '{}' was interrupted",
                    Thread.currentThread().getName(), consumerName);
            Thread.currentThread().interrupt();
        }
    }

    private void writeToFile(LogEntry entry) throws InterruptedException {
        Thread.sleep(150); // Simulate I/O operation

        writtenLogs.add(entry);

        log.info("[{}] Consumer '{}' wrote to file: {}",
                Thread.currentThread().getName(), consumerName, entry);
    }

    public int getWrittenLogsCount() {
        return writtenLogs.size();
    }
}
