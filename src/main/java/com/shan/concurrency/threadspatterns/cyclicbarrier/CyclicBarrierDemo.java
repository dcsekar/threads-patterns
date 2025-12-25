package com.shan.concurrency.threadspatterns.cyclicbarrier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CyclicBarrier Demo - Matrix Row Processing Synchronization
 *
 * Use Case: Synchronize multiple threads at a common barrier point
 * Real-world Example: Process matrix rows in parallel, wait for all to finish before column processing
 *
 * How it works:
 * 1. Initialize CyclicBarrier with number of parties and optional barrier action
 * 2. Each thread does its work and calls await() at the barrier
 * 3. When all parties reach the barrier, the barrier action runs and all threads are released
 * 4. Barrier can be reused (cyclic) for multiple synchronization points
 */
@Slf4j
@Component
public class CyclicBarrierDemo {

    private static final int NUMBER_OF_ROWS = 4;

    public void demonstrate() {
        log.info("=== CyclicBarrier Demo: Matrix Row Processing ===");
        log.info("Scenario: Processing {} matrix rows in parallel, then synchronize", NUMBER_OF_ROWS);

        // Barrier action: Runs when all threads reach the barrier
        Runnable barrierAction = () -> {
            log.info("[{}] *** BARRIER ACTION *** All rows processed! Ready for column processing.",
                    Thread.currentThread().getName());
        };

        // Step 1: Create CyclicBarrier with number of parties and barrier action
        CyclicBarrier barrier = new CyclicBarrier(NUMBER_OF_ROWS, barrierAction);

        // Step 2: Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_ROWS);

        try {
            // Step 3: Create matrix data and submit row processors
            for (int i = 0; i < NUMBER_OF_ROWS; i++) {
                int[] rowData = generateRowData(i, 5);
                executor.submit(new MatrixRowProcessor(i, rowData, barrier));
            }

            log.info("[{}] All row processors submitted", Thread.currentThread().getName());

        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
                log.info("=== CyclicBarrier Demo Completed ===");
            } catch (InterruptedException e) {
                log.error("Executor termination interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private int[] generateRowData(int rowNumber, int size) {
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = rowNumber * 10 + i;
        }
        return data;
    }
}
