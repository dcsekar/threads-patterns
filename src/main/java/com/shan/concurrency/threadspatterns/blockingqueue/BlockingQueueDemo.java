package com.shan.concurrency.threadspatterns.blockingqueue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * BlockingQueue Demo - Producer-Consumer File Writer
 *
 * Use Case: Thread-safe queue for producer-consumer pattern
 * Real-world Example: Multiple application components producing logs, single writer consuming
 *
 * How it works:
 * 1. Create BlockingQueue with capacity (e.g., ArrayBlockingQueue)
 * 2. Producers call put(item) - blocks if queue is full
 * 3. Consumers call take() - blocks if queue is empty
 * 4. Thread-safe without explicit synchronization
 * 5. Use poison pill pattern for graceful shutdown
 */
@Slf4j
@Component
public class BlockingQueueDemo {

    private static final int QUEUE_CAPACITY = 5;
    private static final int NUMBER_OF_PRODUCERS = 3;
    private static final int LOGS_PER_PRODUCER = 4;

    public void demonstrate() {
        log.info("=== BlockingQueue Demo: Producer-Consumer Log Writer ===");
        log.info("Scenario: {} producers generating logs, 1 consumer writing to file (Queue capacity: {})",
                NUMBER_OF_PRODUCERS, QUEUE_CAPACITY);

        // Step 1: Create bounded BlockingQueue
        BlockingQueue<LogEntry> logQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        // Step 2: Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_PRODUCERS + 1);

        try {
            // Step 3: Start consumer
            LogConsumer consumer = new LogConsumer("FileWriter", logQueue);
            executor.submit(consumer);

            // Step 4: Start producers
            for (int i = 1; i <= NUMBER_OF_PRODUCERS; i++) {
                String producerName = "Producer-" + i;
                executor.submit(new LogProducer(producerName, logQueue, LOGS_PER_PRODUCER));
            }

            log.info("[{}] All producers and consumer started", Thread.currentThread().getName());

            // Step 5: Wait for producers to finish
            Thread.sleep(5000);

            // Step 6: Send poison pill to signal consumer shutdown
            log.info("[{}] Sending poison pill to consumer", Thread.currentThread().getName());
            logQueue.put(LogEntry.poison());

        } catch (InterruptedException e) {
            log.error("Demo interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
                log.info("=== BlockingQueue Demo Completed ===");
            } catch (InterruptedException e) {
                log.error("Executor termination interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
