package com.shan.concurrency.threadspatterns.countdownlatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch Demo - Batch Job Coordination
 *
 * Use Case: Wait for multiple tasks to complete before proceeding
 * Real-world Example: Processing 5 data files in parallel, then aggregating results
 *
 * How it works:
 * 1. Initialize CountDownLatch with count (number of tasks)
 * 2. Each task calls countDown() when complete
 * 3. Main thread calls await() to block until count reaches zero
 */
@Slf4j
@Component
public class CountDownLatchDemo {

    private static final int NUMBER_OF_TASKS = 5;

    public void demonstrate() {
        log.info("=== CountDownLatch Demo: Batch Job Coordination ===");
        log.info("Scenario: Processing {} data files in parallel", NUMBER_OF_TASKS);

        // Step 1: Create a CountDownLatch with count = number of tasks
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_TASKS);

        // Step 2: Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            // Step 3: Submit tasks to executor
            for (int i = 1; i <= NUMBER_OF_TASKS; i++) {
                String taskName = "DataFile-" + i;
                int processingTime = 1000 + (i * 500); // Variable processing time
                executor.submit(new BatchJobTask(taskName, latch, processingTime));
            }

            log.info("[{}] All tasks submitted. Waiting for completion...",
                    Thread.currentThread().getName());

            // Step 4: Wait for all tasks to complete
            latch.await();

            log.info("[{}] All tasks completed! Proceeding with result aggregation.",
                    Thread.currentThread().getName());

        } catch (InterruptedException e) {
            log.error("Main thread was interrupted while waiting", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }
}
