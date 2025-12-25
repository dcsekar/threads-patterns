package com.shan.concurrency.threadspatterns.countdownlatch;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * BatchJobTask simulates a task that must complete before the main thread can proceed.
 * Real-world example: Processing multiple data files in parallel before aggregating results.
 */
@Slf4j
public class BatchJobTask implements Runnable {

    private final String taskName;
    private final CountDownLatch latch;
    private final int processingTimeMs;

    public BatchJobTask(String taskName, CountDownLatch latch, int processingTimeMs) {
        this.taskName = taskName;
        this.latch = latch;
        this.processingTimeMs = processingTimeMs;
    }

    @Override
    public void run() {
        try {
            log.info("[{}] Task '{}' started", Thread.currentThread().getName(), taskName);

            // Simulate processing work
            Thread.sleep(processingTimeMs);

            log.info("[{}] Task '{}' completed successfully", Thread.currentThread().getName(), taskName);
        } catch (InterruptedException e) {
            log.error("[{}] Task '{}' was interrupted", Thread.currentThread().getName(), taskName);
            Thread.currentThread().interrupt();
        } finally {
            // Decrement the count of the latch, releasing waiting threads when count reaches zero
            latch.countDown();
            log.info("[{}] Task '{}' counted down. Remaining tasks: {}",
                    Thread.currentThread().getName(), taskName, latch.getCount());
        }
    }
}
